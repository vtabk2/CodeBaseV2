package com.core.ads

import android.app.Application
import com.core.ads.di.FirebaseAdsEntryPoint
import dagger.hilt.android.EntryPointAccessors

/** Shared application base that creates the ads Remote Config adapter before Activities start. */
open class CodeBaseApplication : Application() {
    /** One application-wide adapter shared by every CoreActivity and preload cycle. */
    lateinit var firebaseAdsRemoteConfig: FirebaseAdsRemoteConfig
        private set

    override fun onCreate() {
        super.onCreate()
        firebaseAdsRemoteConfig = runCatching {
            EntryPointAccessors.fromApplication(
                this,
                FirebaseAdsEntryPoint::class.java,
            ).firebaseAdsRemoteConfig()
        }.getOrElse {
            // Keep library consumers without a Hilt binding functional with safe defaults.
            FirebaseAdsRemoteConfig()
        }
    }
}
