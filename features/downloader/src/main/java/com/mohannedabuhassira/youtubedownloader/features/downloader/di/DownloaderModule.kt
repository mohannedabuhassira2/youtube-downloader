package com.mohannedabuhassira.youtubedownloader.features.downloader.di

import com.mohannedabuhassira.youtubedownloader.features.downloader.data.repository.DownloaderRepositoryImpl
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.repository.DownloaderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DownloaderModule {
    @Binds
    @Singleton
    abstract fun bindDownloaderRepository(
        downloaderRepositoryImpl: DownloaderRepositoryImpl
    ): DownloaderRepository
}
