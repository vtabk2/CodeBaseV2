package com.sabo.test.di

import android.content.Context
import android.content.pm.ApplicationInfo
import com.core.ads.FirebaseAdsRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Host-app configuration for the shared ads Remote Config adapter. */
@Module
@InstallIn(SingletonComponent::class)
object AdsModule {
    /** Uses zero seconds in debug and the library production default in release builds. */
    @Provides
    @Singleton
    fun provideFirebaseAdsRemoteConfig(
        @ApplicationContext context: Context,
    ): FirebaseAdsRemoteConfig {
        val isDebuggable =
            (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val minimumFetchIntervalSeconds = if (isDebuggable) {
            FirebaseAdsRemoteConfig.DEBUG_MINIMUM_FETCH_INTERVAL_SECONDS
        } else {
            FirebaseAdsRemoteConfig.DEFAULT_MINIMUM_FETCH_INTERVAL_SECONDS
        }
        return FirebaseAdsRemoteConfig(minimumFetchIntervalSeconds)
    }
}
