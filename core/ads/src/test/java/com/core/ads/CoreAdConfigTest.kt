package com.core.ads

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreAdConfigTest {
    @Test
    fun sortedPlacements_filtersDisabledAndUsesRemotePlacementPriority() {
        val config = CoreAdConfig(
            placements = listOf(
                CoreAdPlacement("secondary", CoreAdType.INTERSTITIAL, listOf("secondary-id"), priority = 0),
                CoreAdPlacement("disabled", CoreAdType.INTERSTITIAL, listOf("disabled-id"), enabled = false),
                CoreAdPlacement("primary", CoreAdType.INTERSTITIAL, listOf("primary-id"), priority = 1),
            ),
        )
        val remote = object : CoreAdsRemoteConfig {
            override fun priorityFor(type: CoreAdType, adUnitId: String): Int? = null

            override fun placementPriority(type: CoreAdType, placement: String): Int? {
                return if (placement == "primary") -1 else null
            }
        }

        assertEquals(
            listOf("primary", "secondary"),
            config.sortedPlacements(CoreAdType.INTERSTITIAL, remote).map { it.placement },
        )
    }

    @Test
    fun sortedAdUnitIds_trimsDeduplicatesAndAppliesRemotePriority() {
        val placement = CoreAdPlacement(
            placement = "home",
            type = CoreAdType.REWARDED,
            adUnitIds = listOf(" fallback ", "high-floor", "fallback", ""),
        )
        val remote = CoreAdsRemoteConfig { _, id -> if (id == "high-floor") -1 else null }

        assertEquals(
            listOf("high-floor", "fallback"),
            CoreAdConfig().sortedAdUnitIds(placement, remote),
        )
    }

    @Test
    fun emptyConfig_hasNoEnabledNamedPlacement() {
        val config = CoreAdConfig(
            placements = listOf(
                CoreAdPlacement("", CoreAdType.APP_OPEN, listOf("app-open")),
                CoreAdPlacement("disabled", CoreAdType.REWARDED, listOf("rewarded"), enabled = false),
            ),
        )

        assertTrue(config.isEmpty(CoreAdsRemoteConfig.EMPTY))
    }
}
