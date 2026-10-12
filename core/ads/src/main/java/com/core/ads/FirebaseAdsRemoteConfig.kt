package com.core.ads

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.android.gms.tasks.Task
import org.json.JSONArray
import org.json.JSONObject

/** Firebase Remote Config adapter that supplies placement lists and priority overrides. */
class FirebaseAdsRemoteConfig(
    /**
     * Minimum Firebase fetch interval in seconds. The consuming app owns this value so a
     * published JitPack artifact follows the host app's debug or release policy.
     */
    private val minimumFetchIntervalSeconds: Long = Companion.DEFAULT_MINIMUM_FETCH_INTERVAL_SECONDS,
) : CoreAdsRemoteConfig {
    companion object {
        /** Remote Config entry containing both Interstitial and Rewarded placements. */
        const val FULLSCREEN_AD_PLACES_KEY = "rewarded__rewarded_inter__inter_ad_places"

        /** Remote Config entry containing App Open placements. */
        const val APP_OPEN_AD_PLACES_KEY = "app_open_ad_places"

        /** Zero seconds is intended for debug builds that need to test console changes quickly. */
        const val DEBUG_MINIMUM_FETCH_INTERVAL_SECONDS = 0L

        /** One hour is the default production interval for a published library consumer. */
        const val DEFAULT_MINIMUM_FETCH_INTERVAL_SECONDS = 3_600L
    }

    init {
        require(minimumFetchIntervalSeconds >= 0L) {
            "minimumFetchIntervalSeconds must be greater than or equal to zero"
        }
    }

    /** Nullable so the app can continue with local priorities when Firebase is not configured. */
    private val firebaseRemoteConfig: FirebaseRemoteConfig? = runCatching {
        FirebaseRemoteConfig.getInstance()
    }.getOrNull()

    /** Current activated placement list, replaced atomically after defaults or remote activation. */
    @Volatile
    private var adPlacements: List<CoreAdPlacement> = emptyList()

    /** Current placement priorities derived from the configured placement list. */
    @Volatile
    private var placementPriorities: Map<CoreAdType, Map<String, Int>> = emptyMap()

    /** Current ad unit priorities derived from each placement's waterfall order. */
    @Volatile
    private var adUnitPriorities: Map<CoreAdType, Map<String, Int>> = emptyMap()

    /** Ensures defaults are installed before a fetch reads the active/default values. */
    private var defaultsReadyTask: Task<Void>? = null

    init {
        firebaseRemoteConfig?.let { remoteConfig ->
            remoteConfig.setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(minimumFetchIntervalSeconds)
                    .build(),
            )
            defaultsReadyTask = remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            defaultsReadyTask?.addOnCompleteListener {
                // Read after the asynchronous defaults write so the local placement list is ready.
                readActivatedValues(remoteConfig)
            }
        }
    }

    /**
     * Returns the Interstitial, Rewarded and App Open placements read from Remote Config.
     * The returned list is a stable snapshot for the caller's current preload cycle.
     */
    fun getAdPlacements(): List<CoreAdPlacement> = adPlacements

    /**
     * Fetches and activates Firebase values. The callback runs after the placement snapshot has
     * been replaced, so the host can call preload again and consume the new list.
     */
    fun fetchAndActivate(onComplete: (isSuccessful: Boolean) -> Unit = {}) {
        val remoteConfig = firebaseRemoteConfig ?: run {
            onComplete(false)
            return
        }

        val fetchTask = defaultsReadyTask?.continueWithTask {
            remoteConfig.fetchAndActivate()
        } ?: remoteConfig.fetchAndActivate()
        fetchTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                readActivatedValues(remoteConfig)
            }
            onComplete(task.isSuccessful)
        }
    }

    override fun priorityFor(type: CoreAdType, adUnitId: String): Int? {
        return adUnitPriorities[type]?.get(adUnitId)
    }

    override fun placementPriority(type: CoreAdType, placement: String): Int? {
        return placementPriorities[type]?.get(placement)
    }

    private fun readActivatedValues(remoteConfig: FirebaseRemoteConfig) {
        val placements = buildList {
            addAll(parseFirebaseAdPlacements(remoteConfig.getString(FULLSCREEN_AD_PLACES_KEY)))
            addAll(parseFirebaseAdPlacements(remoteConfig.getString(APP_OPEN_AD_PLACES_KEY)))
        }
        adPlacements = placements
        placementPriorities = placements
            .groupBy(CoreAdPlacement::type)
            .mapValues { (_, values) -> values.associate { it.placement to it.priority } }
        adUnitPriorities = placements
            .groupBy(CoreAdPlacement::type)
            .mapValues { (_, values) ->
                buildMap {
                    values.forEach { placement ->
                        placement.adUnitIds.forEachIndexed { index, adUnitId ->
                            putIfAbsent(adUnitId, index)
                        }
                    }
                }
            }
    }

}

/** Parses the placement-array format used by CodeBase's Firebase defaults XML. */
internal fun parseFirebaseAdPlacements(json: String): List<CoreAdPlacement> {
    if (json.isBlank()) return emptyList()

    return runCatching {
        val values = JSONArray(json)
        buildList {
            for (index in 0 until values.length()) {
                val value = values.optJSONObject(index) ?: continue
                val placement = value.optString("place_name").trim()
                val type = parseFirebaseAdType(value.optString("ad_type")) ?: continue
                val adUnitIds = parseFirebaseAdUnitIds(value)
                if (placement.isBlank() || adUnitIds.isEmpty()) continue

                add(
                    CoreAdPlacement(
                        placement = placement,
                        type = type,
                        adUnitIds = adUnitIds,
                        enabled = value.optBoolean("is_enable", true),
                        preload = value.optBoolean("is_preload", true),
                        priority = value.optInt("priority", index),
                        autoReloadAfterDismiss = value.optBoolean(
                            "is_auto_load_after_dismiss",
                            true,
                        ),
                        isTutorialFlow = value.optBoolean("is_tutorial_flow", false),
                        trackClick = value.optBoolean("is_tracking_click", false),
                        trackShow = value.optBoolean("is_tracking_show", true),
                        extraIntervalSeconds = value.optInt("plus_interval", 0),
                    ),
                )
            }
        }
    }.getOrDefault(emptyList())
}

private fun parseFirebaseAdUnitIds(value: JSONObject): List<String> {
    return buildList {
        value.optString("ad_id").trim().takeIf(String::isNotEmpty)?.let(::add)
        value.optJSONArray("high_floor_ad_ids")?.let { highFloorIds ->
            for (index in 0 until highFloorIds.length()) {
                highFloorIds.optString(index).trim().takeIf(String::isNotEmpty)?.let(::add)
            }
        }
    }.distinct()
}

private fun parseFirebaseAdType(value: String): CoreAdType? {
    return when (value.trim().lowercase()) {
        "interstitial" -> CoreAdType.INTERSTITIAL
        "rewarded" -> CoreAdType.REWARDED
        "app_open", "app-open", "appopen" -> CoreAdType.APP_OPEN
        else -> null
    }
}
