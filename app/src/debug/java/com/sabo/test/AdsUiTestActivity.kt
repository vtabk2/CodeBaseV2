package com.sabo.test

import android.os.Bundle
import android.view.LayoutInflater
import com.core.ads.CoreAdConfig
import com.core.ads.CoreAppOpenPolicy
import com.core.baseui.CoreActivity
import com.sabo.test.databinding.ActivityAdsUiTestBinding

/** Debug-only host used by instrumentation tests for the no-cache ad branches. */
class AdsUiTestActivity : CoreActivity<ActivityAdsUiTestBinding>() {
    override fun createViewBinding(inflater: LayoutInflater): ActivityAdsUiTestBinding {
        return ActivityAdsUiTestBinding.inflate(inflater)
    }

    override fun provideCoreAdConfig(): CoreAdConfig {
        return CoreAdConfig(
            // No placement makes every show call take the deterministic no-cache branch.
            appOpenPolicy = CoreAppOpenPolicy(delayBeforeShowMillis = 0),
        )
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        binding.showInterstitialButton.setOnClickListener {
            showInterstitial("missing_interstitial") { isShown ->
                binding.statusText.text = getString(
                    R.string.ads_test_status_interstitial,
                    isShown.toString(),
                )
            }
        }
        binding.showRewardedButton.setOnClickListener {
            showRewarded { result ->
                binding.statusText.text = getString(
                    R.string.ads_test_status_rewarded,
                    result.isShown.toString(),
                    result.isEarned.toString(),
                )
            }
        }
        binding.showAppOpenButton.setOnClickListener {
            showAppOpen { isShown ->
                binding.statusText.text = getString(
                    R.string.ads_test_status_app_open,
                    isShown.toString(),
                )
            }
        }
    }
}
