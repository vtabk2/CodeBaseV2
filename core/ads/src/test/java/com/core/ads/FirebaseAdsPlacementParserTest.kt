package com.core.ads

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseAdsPlacementParserTest {
    @Test
    fun parsesCombinedInterstitialAndRewardedListWithWaterfall() {
        val placements = parseFirebaseAdPlacements(
            """
            [
              {
                "place_name": "home_inter_primary",
                "ad_id": "primary-id",
                "high_floor_ad_ids": ["fallback-id"],
                "ad_type": "interstitial",
                "is_enable": true,
                "priority": 0
              },
              {
                "place_name": "unlock_rewarded",
                "ad_id": "rewarded-id",
                "ad_type": "rewarded",
                "is_enable": false,
                "is_auto_load_after_dismiss": false,
                "plus_interval": 7,
                "priority": 1
              }
            ]
            """.trimIndent(),
        )

        assertEquals(2, placements.size)
        assertEquals(CoreAdType.INTERSTITIAL, placements[0].type)
        assertEquals(listOf("primary-id", "fallback-id"), placements[0].adUnitIds)
        assertEquals("unlock_rewarded", placements[1].placement)
        assertFalse(placements[1].enabled)
        assertFalse(placements[1].autoReloadAfterDismiss)
        assertEquals(7, placements[1].extraIntervalSeconds)
    }

    @Test
    fun skipsUnknownTypesAndMalformedEntries() {
        val placements = parseFirebaseAdPlacements(
            """
            [
              {"place_name":"unknown","ad_id":"unknown-id","ad_type":"native"},
              {"place_name":"missing-id","ad_type":"rewarded"},
              {"place_name":"app_open","ad_id":"app-open-id","ad_type":"app_open"}
            ]
            """.trimIndent(),
        )

        assertEquals(1, placements.size)
        assertEquals(CoreAdType.APP_OPEN, placements.single().type)
        assertTrue(placements.single().enabled)
    }

    @Test
    fun malformedJson_returnsEmptyList() {
        assertTrue(parseFirebaseAdPlacements("not-json").isEmpty())
    }
}
