package com.core.ads.di

import com.core.ads.FirebaseAdsRemoteConfig
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Hilt bridge used by CodeBaseApplication to resolve the app-owned ads configuration early. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface FirebaseAdsEntryPoint {
    /** Returns the single Firebase Remote Config adapter configured by the consuming app. */
    fun firebaseAdsRemoteConfig(): FirebaseAdsRemoteConfig
}
