package com.mohannedabuhassira.youtubedownloader.core.network.di

import com.farimarwat.library.YoutubeDL
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloader
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloaderImpl
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkInitializer
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkInitializerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkModule {
    @Binds
    @Singleton
    abstract fun bindNetworkDownloader(
        impl: NetworkDownloaderImpl
    ): NetworkDownloader

    @Binds
    @Singleton
    abstract fun bindNetworkInitializer(
        impl: NetworkInitializerImpl
    ): NetworkInitializer

    companion object {
        @Provides
        @Singleton
        fun provideYoutubeDLEngine(): YoutubeDL {
            return YoutubeDL.getInstance()
        }
    }
}
