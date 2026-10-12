package com.core.baseui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.core.ads.AdPreloadManager
import com.core.ads.CodeBaseApplication
import com.core.ads.CoreAdConfig
import com.core.ads.CoreAdsRemoteConfig
import com.core.ads.CoreRewardResult
import com.core.ads.FirebaseAdsRemoteConfig
import com.core.ads.di.AdPreloadEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * Shared Activity base with ViewBinding and priority full-screen ad preload.
 *
 * Subclasses provide their binding and placement/policy configuration. The ads module handles
 * waterfall loading and shares cached SDK objects by ad type + ad unit ID.
 */
abstract class CoreActivity<VB : ViewBinding> : AppCompatActivity() {
    private var _binding: VB? = null

    protected val binding: VB
        get() = _binding ?: error("Binding is only available between onCreate and onDestroy")

    /** Application-created Remote Config adapter shared by all Activities in the process. */
    protected val coreFirebaseAdsRemoteConfig: FirebaseAdsRemoteConfig?
        get() = (application as? CodeBaseApplication)?.firebaseAdsRemoteConfig

    private val adPreloadManager: AdPreloadManager by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            AdPreloadEntryPoint::class.java,
        ).adPreloadManager()
    }

    protected abstract fun createViewBinding(inflater: LayoutInflater): VB

    protected open fun provideCoreAdConfig(): CoreAdConfig = CoreAdConfig()

    protected open fun provideCoreAdsRemoteConfig(): CoreAdsRemoteConfig =
        coreFirebaseAdsRemoteConfig ?: CoreAdsRemoteConfig.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = createViewBinding(layoutInflater)
        setContentView(binding.root)
        val remoteConfig = provideCoreAdsRemoteConfig()
        adPreloadManager.preload(provideCoreAdConfig(), remoteConfig)
        (remoteConfig as? FirebaseAdsRemoteConfig)?.fetchAndActivate {
            // Refresh the preload after the application-wide adapter activates remote placements.
            if (!isFinishing && !isDestroyed) {
                preloadCoreAds()
            }
        }
        onViewReady(savedInstanceState)
    }

    protected open fun onViewReady(savedInstanceState: Bundle?) = Unit

    protected fun preloadCoreAds() {
        adPreloadManager.preload(provideCoreAdConfig(), provideCoreAdsRemoteConfig())
    }

    protected fun recordCoreMeaningfulAction() {
        adPreloadManager.recordMeaningfulAction()
    }

    protected fun showInterstitial(
        placement: String? = null,
        onComplete: (isShown: Boolean) -> Unit = {},
    ) {
        adPreloadManager.showInterstitial(this, placement, onComplete)
    }

    protected fun showRewarded(
        onComplete: (CoreRewardResult) -> Unit,
    ) {
        adPreloadManager.showRewarded(this, onComplete)
    }

    protected fun showAppOpen(
        onComplete: (isShown: Boolean) -> Unit = {},
    ) {
        adPreloadManager.showAppOpen(this, onComplete)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
