package com.core.ads

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Full-screen SDK objects are considered fresh for 55 minutes. */
private const val FULL_SCREEN_CACHE_TTL_MS = 55 * 60 * 1_000L
/** App Open SDK objects are considered fresh for four hours. */
private const val APP_OPEN_CACHE_TTL_MS = 4 * 60 * 60 * 1_000L
/** Prevent a stalled SDK request from blocking the preload queue indefinitely. */
private const val LOAD_TIMEOUT_MS = 10_000L

private data class AdCacheKey(
    val type: CoreAdType,
    val adUnitId: String,
)

/**
 * Process-wide ad cache. A loaded SDK object is shared by every placement that uses the same
 * [CoreAdType] and ad unit ID, while placement policies remain independent.
 */
@Singleton
class AdPreloadManager @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    /** Latest host configuration used by all show and preload operations. */
    private var config = CoreAdConfig()
    /** Latest priority adapter supplied by the host application. */
    private var remoteConfig = CoreAdsRemoteConfig.EMPTY
    /** Invalidates callbacks that belong to a previous configuration snapshot. */
    private var configGeneration = 0L
    /** Guards the one-time Mobile Ads SDK initialization request. */
    private var initializationStarted = false
    /** True after the Mobile Ads SDK initialization callback completes. */
    private var isInitialized = false
    /** Prevents overlapping preload batches. */
    private var isPreloading = false

    /** Monotonic start time of the current Interstitial session window. */
    private var sessionStartedAtMs = 0L
    /** Number of Interstitial impressions in the current session window. */
    private var interstitialShownInSession = 0
    /** Meaningful actions recorded since the last Interstitial impression. */
    private var meaningfulActionCount = 0
    /** Monotonic time of the last displayed Interstitial. */
    private var lastInterstitialShownAtMs = 0L
    /** Monotonic time of the last displayed App Open ad. */
    private var lastAppOpenShownAtMs = 0L

    /** Shared Interstitial SDK objects keyed by type and ad unit ID. */
    private val interstitialCache = mutableMapOf<AdCacheKey, InterstitialSlot>()
    /** Shared Rewarded SDK objects keyed by type and ad unit ID. */
    private val rewardedCache = mutableMapOf<AdCacheKey, RewardedSlot>()
    /** Shared App Open SDK objects keyed by type and ad unit ID. */
    private val appOpenCache = mutableMapOf<AdCacheKey, AppOpenSlot>()

    /** Starts or updates preload. Calling this repeatedly is safe. */
    fun preload(
        newConfig: CoreAdConfig,
        newRemoteConfig: CoreAdsRemoteConfig = CoreAdsRemoteConfig.EMPTY,
    ) {
        runOnMain {
            if (config != newConfig) {
                config = newConfig
                configGeneration++
                clearCache()
            }
            remoteConfig = newRemoteConfig
            if (!config.isEmpty(remoteConfig)) {
                ensureInitializedAndPreload()
            }
        }
    }

    /** Call this after a meaningful user action when the interstitial policy requires it. */
    fun recordMeaningfulAction() {
        runOnMain { meaningfulActionCount++ }
    }

    fun showInterstitial(
        activity: Activity,
        placement: String? = null,
        onComplete: (isShown: Boolean) -> Unit,
    ) {
        runOnMain {
            if (!isActivityUsable(activity)) {
                onComplete(false)
                return@runOnMain
            }

            val candidates = config.sortedPlacements(CoreAdType.INTERSTITIAL, remoteConfig)
                .let { placements ->
                    if (placement == null) placements.take(2)
                    else placements.filter { it.placement == placement }
                }
            val selected = candidates.firstNotNullOfOrNull { findInterstitial(it) }
            if (selected == null) {
                onComplete(false)
                ensureInitializedAndPreload()
                return@runOnMain
            }
            if (!selected.first.ignoreInterval && !canShowInterstitial(selected.first)) {
                onComplete(false)
                return@runOnMain
            }

            val ad = selected.second.ad ?: run {
                onComplete(false)
                ensureInitializedAndPreload()
                return@runOnMain
            }
            selected.second.clearAd()
            var completed = false
            var didShow = false
            fun complete(isShown: Boolean) {
                if (completed) return
                completed = true
                onComplete(isShown)
                if (selected.first.autoReloadAfterDismiss) {
                    ensureInitializedAndPreload()
                }
            }

            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    didShow = true
                    onInterstitialShown()
                }

                override fun onAdDismissedFullScreenContent() = complete(didShow)

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    complete(false)
                }
            }
            runCatching { ad.show(activity) }.onFailure { complete(false) }
        }
    }

    fun showRewarded(
        activity: Activity,
        onComplete: (CoreRewardResult) -> Unit,
    ) {
        runOnMain {
            if (!isActivityUsable(activity)) {
                onComplete(CoreRewardResult(false, false))
                return@runOnMain
            }

            val selected = config.sortedPlacements(CoreAdType.REWARDED, remoteConfig)
                .firstNotNullOfOrNull { findRewarded(it) }
            if (selected == null) {
                onComplete(CoreRewardResult(false, false))
                ensureInitializedAndPreload()
                return@runOnMain
            }

            val ad = selected.second.ad ?: run {
                onComplete(CoreRewardResult(false, false))
                ensureInitializedAndPreload()
                return@runOnMain
            }
            selected.second.clearAd()
            var completed = false
            var didShow = false
            var isEarned = false
            fun complete(isShown: Boolean) {
                if (completed) return
                completed = true
                onComplete(CoreRewardResult(isShown, isEarned))
                if (selected.first.autoReloadAfterDismiss) {
                    ensureInitializedAndPreload()
                }
            }

            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    didShow = true
                }

                override fun onAdDismissedFullScreenContent() = complete(didShow)

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    complete(false)
                }
            }
            runCatching {
                ad.show(activity, OnUserEarnedRewardListener { isEarned = true })
            }.onFailure { complete(false) }
        }
    }

    fun showAppOpen(
        activity: Activity,
        onComplete: (isShown: Boolean) -> Unit,
    ) {
        val delay = config.appOpenPolicy.delayBeforeShowMillis.coerceAtLeast(0L)
        if (delay == 0L) {
            showAppOpenNow(activity, onComplete)
        } else {
            mainHandler.postDelayed({ showAppOpenNow(activity, onComplete) }, delay)
        }
    }

    private fun showAppOpenNow(
        activity: Activity,
        onComplete: (isShown: Boolean) -> Unit,
    ) {
        runOnMain {
            if (!isActivityUsable(activity)) {
                onComplete(false)
                return@runOnMain
            }

            val selected = config.sortedPlacements(CoreAdType.APP_OPEN, remoteConfig)
                .firstNotNullOfOrNull { findAppOpen(it) }
            if (selected == null) {
                onComplete(false)
                ensureInitializedAndPreload()
                return@runOnMain
            }
            if (!selected.first.ignoreInterval && !canShowAppOpen()) {
                onComplete(false)
                return@runOnMain
            }

            val ad = selected.second.ad ?: run {
                onComplete(false)
                ensureInitializedAndPreload()
                return@runOnMain
            }
            selected.second.clearAd()
            var completed = false
            var didShow = false
            fun complete(isShown: Boolean) {
                if (completed) return
                completed = true
                onComplete(isShown)
                if (selected.first.autoReloadAfterDismiss) {
                    ensureInitializedAndPreload()
                }
            }

            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    didShow = true
                    lastAppOpenShownAtMs = SystemClock.elapsedRealtime()
                }

                override fun onAdDismissedFullScreenContent() = complete(didShow)

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    complete(false)
                }
            }
            runCatching { ad.show(activity) }.onFailure { complete(false) }
        }
    }

    private fun ensureInitializedAndPreload() {
        if (isPreloading || config.isEmpty(remoteConfig)) return
        if (isInitialized) {
            preloadInPriorityGroup()
            return
        }
        if (initializationStarted) return

        initializationStarted = true
        MobileAds.initialize(appContext) {
            mainHandler.post {
                isInitialized = true
                preloadInPriorityGroup()
            }
        }
    }

    private fun preloadInPriorityGroup() {
        if (isPreloading || config.isEmpty(remoteConfig)) return
        isPreloading = true
        val generation = configGeneration
        val selected = buildList {
            addAll(config.sortedPlacements(CoreAdType.INTERSTITIAL, remoteConfig).take(2))
            config.sortedPlacements(CoreAdType.REWARDED, remoteConfig).firstOrNull()?.let(::add)
            config.sortedPlacements(CoreAdType.APP_OPEN, remoteConfig).firstOrNull()?.let(::add)
        }.filter { it.preload }

        if (selected.isEmpty()) {
            finishPreload(generation)
            return
        }

        var remaining = selected.size
        fun onPlacementFinished() {
            remaining--
            if (remaining == 0) finishPreload(generation)
        }
        selected.forEach { placement ->
            loadPlacement(placement, generation, ::onPlacementFinished)
        }
    }

    private fun loadPlacement(
        placement: CoreAdPlacement,
        generation: Long,
        onFinished: () -> Unit,
    ) {
        val ids = config.sortedAdUnitIds(placement, remoteConfig)
        var finished = false
        fun finishOnce() {
            if (finished) return
            finished = true
            onFinished()
        }

        fun tryNext(index: Int) {
            if (generation != configGeneration || index >= ids.size) {
                finishOnce()
                return
            }
            val key = AdCacheKey(placement.type, ids[index])
            val state = stateFor(key)
            if (state.isFresh(ttlFor(placement.type))) {
                finishOnce()
                return
            }
            val waiter: (Boolean) -> Unit = { success ->
                if (success || generation != configGeneration) finishOnce()
                else tryNext(index + 1)
            }
            if (state.isLoading) {
                state.waiters += waiter
            } else {
                state.waiters += waiter
                startLoad(state, placement.type, ids[index], generation)
            }
        }

        tryNext(0)
    }

    private fun startLoad(
        state: LoadState,
        type: CoreAdType,
        adUnitId: String,
        generation: Long,
    ) {
        state.isLoading = true
        val retryPolicy = config.fullscreenPolicy
        val maxRetry = if (retryPolicy.enableRetry) {
            retryPolicy.maxRetryCount.coerceAtLeast(0)
        } else {
            0
        }

        fun attempt(retryIndex: Int) {
            if (generation != configGeneration) {
                state.isLoading = false
                state.finish(false)
                return
            }

            val requestToken = ++state.requestToken
            var callbackHandled = false
            var timeout: Runnable? = null

            fun complete(ad: Any?) {
                if (callbackHandled || requestToken != state.requestToken) return
                callbackHandled = true
                timeout?.let(mainHandler::removeCallbacks)
                if (generation != configGeneration) {
                    state.isLoading = false
                    state.finish(false)
                } else if (ad != null) {
                    state.store(ad)
                    state.loadedAtMs = SystemClock.elapsedRealtime()
                    state.isLoading = false
                    state.finish(true)
                } else if (retryIndex < maxRetry) {
                    val delay = retryPolicy.retryIntervalSeconds
                        .getOrNull(retryIndex)
                        ?.coerceAtLeast(0L)
                        ?.times(1_000L)
                        ?: 0L
                    mainHandler.postDelayed({ attempt(retryIndex + 1) }, delay)
                } else {
                    state.isLoading = false
                    state.finish(false)
                }
            }

            timeout = Runnable { complete(null) }
            mainHandler.postDelayed(timeout!!, LOAD_TIMEOUT_MS)

            when (type) {
                CoreAdType.INTERSTITIAL -> InterstitialAd.load(
                    appContext,
                    adUnitId,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) = complete(ad)

                        override fun onAdFailedToLoad(error: LoadAdError) = complete(null)
                    },
                )

                CoreAdType.REWARDED -> RewardedAd.load(
                    appContext,
                    adUnitId,
                    AdRequest.Builder().build(),
                    object : RewardedAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedAd) = complete(ad)

                        override fun onAdFailedToLoad(error: LoadAdError) = complete(null)
                    },
                )

                CoreAdType.APP_OPEN -> AppOpenAd.load(
                    appContext,
                    adUnitId,
                    AdRequest.Builder().build(),
                    object : AppOpenAdLoadCallback() {
                        override fun onAdLoaded(ad: AppOpenAd) = complete(ad)

                        override fun onAdFailedToLoad(error: LoadAdError) = complete(null)
                    },
                )
            }
        }

        attempt(0)
    }

    private fun finishPreload(generation: Long) {
        isPreloading = false
        if (generation != configGeneration && !config.isEmpty(remoteConfig)) {
            preloadInPriorityGroup()
        }
    }

    private fun findInterstitial(placement: CoreAdPlacement): Pair<CoreAdPlacement, InterstitialSlot>? {
        return config.sortedAdUnitIds(placement, remoteConfig)
            .firstNotNullOfOrNull { id ->
                interstitialCache[AdCacheKey(CoreAdType.INTERSTITIAL, id)]
                    ?.takeIf { it.isFresh(FULL_SCREEN_CACHE_TTL_MS) }
                    ?.let { placement to it }
            }
    }

    private fun findRewarded(placement: CoreAdPlacement): Pair<CoreAdPlacement, RewardedSlot>? {
        return config.sortedAdUnitIds(placement, remoteConfig)
            .firstNotNullOfOrNull { id ->
                rewardedCache[AdCacheKey(CoreAdType.REWARDED, id)]
                    ?.takeIf { it.isFresh(FULL_SCREEN_CACHE_TTL_MS) }
                    ?.let { placement to it }
            }
    }

    private fun findAppOpen(placement: CoreAdPlacement): Pair<CoreAdPlacement, AppOpenSlot>? {
        return config.sortedAdUnitIds(placement, remoteConfig)
            .firstNotNullOfOrNull { id ->
                appOpenCache[AdCacheKey(CoreAdType.APP_OPEN, id)]
                    ?.takeIf { it.isFresh(APP_OPEN_CACHE_TTL_MS) }
                    ?.let { placement to it }
            }
    }

    private fun stateFor(key: AdCacheKey): LoadState {
        return when (key.type) {
            CoreAdType.INTERSTITIAL -> interstitialCache.getOrPut(key) { InterstitialSlot() }
            CoreAdType.REWARDED -> rewardedCache.getOrPut(key) { RewardedSlot() }
            CoreAdType.APP_OPEN -> appOpenCache.getOrPut(key) { AppOpenSlot() }
        }
    }

    private fun ttlFor(type: CoreAdType): Long {
        return if (type == CoreAdType.APP_OPEN) APP_OPEN_CACHE_TTL_MS else FULL_SCREEN_CACHE_TTL_MS
    }

    private fun canShowInterstitial(placement: CoreAdPlacement): Boolean {
        val policy = config.interstitialPolicy
        val now = SystemClock.elapsedRealtime()
        if (sessionStartedAtMs == 0L || now - sessionStartedAtMs >= policy.sessionDurationSeconds * 1_000L) {
            sessionStartedAtMs = now
            interstitialShownInSession = 0
        }
        if (policy.adsPerSession > 0 && interstitialShownInSession >= policy.adsPerSession) return false
        if (now - lastInterstitialShownAtMs < (policy.intervalSeconds + placement.extraIntervalSeconds) * 1_000L) {
            return false
        }
        if (lastAppOpenShownAtMs > lastInterstitialShownAtMs &&
            now - lastAppOpenShownAtMs < policy.intervalAfterAppOpenSeconds * 1_000L
        ) {
            return false
        }
        return meaningfulActionCount >= policy.meaningfulActionsBetweenAds
    }

    private fun canShowAppOpen(): Boolean {
        val intervalMs = config.appOpenPolicy.intervalSeconds.coerceAtLeast(0L) * 1_000L
        val now = SystemClock.elapsedRealtime()
        return now - lastInterstitialShownAtMs >= intervalMs &&
            now - lastAppOpenShownAtMs >= intervalMs
    }

    private fun onInterstitialShown() {
        val now = SystemClock.elapsedRealtime()
        lastInterstitialShownAtMs = now
        interstitialShownInSession++
        meaningfulActionCount = 0
    }

    private fun clearCache() {
        interstitialCache.values.forEach(LoadState::invalidate)
        rewardedCache.values.forEach(LoadState::invalidate)
        appOpenCache.values.forEach(LoadState::invalidate)
        interstitialCache.clear()
        rewardedCache.clear()
        appOpenCache.clear()
    }

    private fun isActivityUsable(activity: Activity): Boolean {
        return !activity.isFinishing && (Build.VERSION.SDK_INT < 17 || !activity.isDestroyed)
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block() else mainHandler.post(block)
    }

    private open class LoadState {
        var loadedAtMs = 0L
        var isLoading = false
        var requestToken = 0L
        val waiters = mutableListOf<(Boolean) -> Unit>()

        open fun hasAd(): Boolean = false

        open fun store(ad: Any) = Unit

        open fun clearAd() = Unit

        fun isFresh(ttlMs: Long): Boolean {
            return hasAd() && loadedAtMs > 0L &&
                SystemClock.elapsedRealtime() - loadedAtMs < ttlMs
        }

        fun finish(success: Boolean) {
            val callbacks = waiters.toList()
            waiters.clear()
            callbacks.forEach { it(success) }
        }

        fun invalidate() {
            requestToken++
            clearAd()
            loadedAtMs = 0L
            isLoading = false
            finish(false)
        }
    }

    private class InterstitialSlot : LoadState() {
        var ad: InterstitialAd? = null

        override fun hasAd() = ad != null

        override fun store(ad: Any) {
            this.ad = ad as InterstitialAd
        }

        override fun clearAd() {
            ad = null
            loadedAtMs = 0L
        }
    }

    private class RewardedSlot : LoadState() {
        var ad: RewardedAd? = null

        override fun hasAd() = ad != null

        override fun store(ad: Any) {
            this.ad = ad as RewardedAd
        }

        override fun clearAd() {
            ad = null
            loadedAtMs = 0L
        }
    }

    private class AppOpenSlot : LoadState() {
        var ad: AppOpenAd? = null

        override fun hasAd() = ad != null

        override fun store(ad: Any) {
            this.ad = ad as AppOpenAd
        }

        override fun clearAd() {
            ad = null
            loadedAtMs = 0L
        }
    }
}
