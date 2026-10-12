package com.core.ads

enum class CoreAdType {
    /** Full-screen ad shown between user actions. */
    INTERSTITIAL,
    /** Full-screen ad that can grant a reward after completion. */
    REWARDED,
    /** Ad shown when the application opens or returns to foreground. */
    APP_OPEN,
}

enum class CoreAppOpenReopenMode {
    /** Let the host application handle the reopen flow. */
    CUSTOM_ACTIVITY,
    /** Use an Interstitial flow when reopening. */
    INTERSTITIAL,
    /** Use an App Open ad when reopening. */
    APP_OPEN,
}

data class CoreFullscreenPolicy(
    /** Do not show an ad until a load result is ready. */
    val waitLoadToShow: Boolean = false,
    /** Retry a failed SDK load request. */
    val enableRetry: Boolean = false,
    /** Maximum number of retries after the initial load request. */
    val maxRetryCount: Int = 5,
    /** Delay before each retry, in seconds. */
    val retryIntervalSeconds: List<Long> = listOf(3, 6, 9, 12, 15),
)

data class CoreInterstitialPolicy(
    /** Maximum number of Interstitial ads shown during one session window. */
    val adsPerSession: Int = 50,
    /** Length of the session window, in seconds. */
    val sessionDurationSeconds: Long = 600,
    /** Minimum time between two Interstitial ads, in seconds. */
    val intervalSeconds: Long = 37,
    /** Minimum time between an App Open ad and the next Interstitial, in seconds. */
    val intervalAfterAppOpenSeconds: Long = 30,
    /** Number of meaningful actions required before another Interstitial can show. */
    val meaningfulActionsBetweenAds: Int = 0,
)

data class CoreRewardedPolicy(
    /** Maximum retries made by the caller when a Rewarded action needs another attempt. */
    val maxRetryOnContext: Int = 2,
    /** Delay between Rewarded context retries, in seconds. */
    val retryDelayOnContextSeconds: Int = 5,
)

data class CoreAppOpenPolicy(
    /** Delay before attempting to show App Open, in milliseconds. */
    val delayBeforeShowMillis: Long = 200,
    /** Minimum time between App Open/Interstitial fullscreen ads, in seconds. */
    val intervalSeconds: Long = 3_600,
    /** Reopen strategy selected by the host application. */
    val reopenMode: CoreAppOpenReopenMode = CoreAppOpenReopenMode.APP_OPEN,
)

/** A logical ad position and its ordered waterfall of ad unit IDs. */
data class CoreAdPlacement(
    /** Stable logical name used when showing a specific placement. */
    val placement: String,
    /** Ad format for this placement. */
    val type: CoreAdType,
    /** Ad unit IDs tried in waterfall order; Remote Config may override this order. */
    val adUnitIds: List<String>,
    /** Whether this placement can load and show ads. */
    val enabled: Boolean = true,
    /** Whether this placement participates in the startup preload group. */
    val preload: Boolean = true,
    /** Local placement order; lower values are preferred. */
    val priority: Int = 0,
    /** Whether the manager should preload this placement after its ad is dismissed. */
    val autoReloadAfterDismiss: Boolean = true,
    /** Skip Interstitial/App Open cooldown checks for this placement. */
    val ignoreInterval: Boolean = false,
    /** Marks a placement used in an onboarding/tutorial flow. */
    val isTutorialFlow: Boolean = false,
    /** Enables click tracking integration in the host application. */
    val trackClick: Boolean = false,
    /** Enables impression tracking integration in the host application. */
    val trackShow: Boolean = true,
    /** Additional Interstitial cooldown for this placement, in seconds. */
    val extraIntervalSeconds: Int = 0,
)

/**
 * The app can back this adapter with Firebase Remote Config or any other backend.
 * Returning null keeps the local order.
 */
fun interface CoreAdsRemoteConfig {
    /** Returns a lower-is-first priority for an ad unit, or null for local order. */
    fun priorityFor(type: CoreAdType, adUnitId: String): Int?

    /** Returns a lower-is-first priority for a placement, or null for local order. */
    fun placementPriority(type: CoreAdType, placement: String): Int? = null

    companion object {
        val EMPTY = CoreAdsRemoteConfig { _, _ -> null }
    }
}

data class CoreAdConfig(
    /** All logical ad placements declared by the host application. */
    val placements: List<CoreAdPlacement> = emptyList(),
    /** Retry behavior shared by Interstitial, Rewarded and App Open loads. */
    val fullscreenPolicy: CoreFullscreenPolicy = CoreFullscreenPolicy(),
    /** Frequency and cooldown policy for Interstitial ads. */
    val interstitialPolicy: CoreInterstitialPolicy = CoreInterstitialPolicy(),
    /** Retry policy available to Rewarded flows that are controlled by the host. */
    val rewardedPolicy: CoreRewardedPolicy = CoreRewardedPolicy(),
    /** Delay, cooldown and reopen behavior for App Open ads. */
    val appOpenPolicy: CoreAppOpenPolicy = CoreAppOpenPolicy(),
) {
    internal fun sortedPlacements(
        type: CoreAdType,
        remoteConfig: CoreAdsRemoteConfig,
    ): List<CoreAdPlacement> {
        return placements
            .asSequence()
            .filter { it.type == type && it.enabled && it.placement.isNotBlank() }
            .sortedWith(
                compareBy<CoreAdPlacement> {
                    remoteConfig.placementPriority(type, it.placement) ?: it.priority
                }.thenBy { it.priority }.thenBy { it.placement },
            )
            .toList()
    }

    internal fun sortedAdUnitIds(
        placement: CoreAdPlacement,
        remoteConfig: CoreAdsRemoteConfig,
    ): List<String> {
        return placement.adUnitIds
            .asSequence()
            .map(String::trim)
            .filter(String::isNotEmpty)
            .withIndex()
            .sortedWith(
                compareBy<IndexedValue<String>> {
                    remoteConfig.priorityFor(placement.type, it.value) ?: it.index
                }.thenBy { it.index },
            )
            .map(IndexedValue<String>::value)
            .distinct()
            .toList()
    }

    internal fun isEmpty(remoteConfig: CoreAdsRemoteConfig): Boolean {
        return CoreAdType.entries.none { type ->
            sortedPlacements(type, remoteConfig).any {
                sortedAdUnitIds(it, remoteConfig).isNotEmpty()
            }
        }
    }
}

data class CoreRewardResult(
    /** True when the Rewarded SDK object was successfully displayed. */
    val isShown: Boolean,
    /** True when the user completed the configured reward callback. */
    val isEarned: Boolean,
)
