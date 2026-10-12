package com.core.ads.di

import com.core.ads.AdPreloadManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AdPreloadEntryPoint {
    fun adPreloadManager(): AdPreloadManager
}
