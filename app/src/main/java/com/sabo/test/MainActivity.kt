package com.sabo.test

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.core.ads.CoreAdPlacement
import com.core.ads.CoreAdConfig
import com.core.ads.CoreAdType
import com.core.baseui.CoreActivity
import com.sabo.test.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : CoreActivity<ActivityMainBinding>() {
    override fun createViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun provideCoreAdConfig(): CoreAdConfig {
        return CoreAdConfig(
            placements = listOf(
                CoreAdPlacement(
                    placement = "home_inter_primary",
                    type = CoreAdType.INTERSTITIAL,
                    adUnitIds = listOf(TEST_INTERSTITIAL_ID),
                    priority = 0,
                ),
                CoreAdPlacement(
                    placement = "home_inter_secondary",
                    type = CoreAdType.INTERSTITIAL,
                    adUnitIds = listOf(TEST_INTERSTITIAL_ID),
                    priority = 1,
                ),
                CoreAdPlacement(
                    placement = "unlock_rewarded",
                    type = CoreAdType.REWARDED,
                    adUnitIds = listOf(TEST_REWARDED_ID),
                    priority = 0,
                ),
                CoreAdPlacement(
                    placement = "app_open",
                    type = CoreAdType.APP_OPEN,
                    adUnitIds = listOf(TEST_APP_OPEN_ID),
                    priority = 0,
                ),
            ),
        )
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.showInterstitialButton.setOnClickListener {
            updateStatus(getString(R.string.ads_status_loading_interstitial))
            showInterstitial(placement = INTERSTITIAL_PLACEMENT) { isShown ->
                updateStatus(
                    getString(
                        R.string.ads_status_interstitial_result,
                        getString(if (isShown) R.string.ads_status_shown else R.string.ads_status_not_shown),
                    ),
                )
            }
        }

        binding.showRewardedButton.setOnClickListener {
            updateStatus(getString(R.string.ads_status_loading_rewarded))
            showRewarded { result ->
                updateStatus(
                    getString(
                        R.string.ads_status_rewarded_result,
                        getString(if (result.isShown) R.string.ads_status_shown else R.string.ads_status_not_shown),
                        getString(if (result.isEarned) R.string.ads_status_earned else R.string.ads_status_not_earned),
                    ),
                )
            }
        }

        binding.showAppOpenButton.setOnClickListener {
            updateStatus(getString(R.string.ads_status_loading_app_open))
            showAppOpen { isShown ->
                updateStatus(
                    getString(
                        R.string.ads_status_app_open_result,
                        getString(if (isShown) R.string.ads_status_shown else R.string.ads_status_not_shown),
                    ),
                )
            }
        }
    }

    /** Updates the visible result so a tester can verify the SDK callback on a real device. */
    private fun updateStatus(message: String) {
        binding.statusText.text = message
    }

    private companion object {
        // The first interstitial placement is intentionally selected for the manual test button.
        const val INTERSTITIAL_PLACEMENT = "home_inter_primary"

        // Google test IDs. Replace these in the consuming app before release.
        const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
        const val TEST_APP_OPEN_ID = "ca-app-pub-3940256099942544/9257395921"
    }
}
