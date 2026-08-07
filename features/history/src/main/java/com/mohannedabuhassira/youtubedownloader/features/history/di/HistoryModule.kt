package com.mohannedabuhassira.youtubedownloader.features.history.di

import com.mohannedabuhassira.youtubedownloader.features.history.data.repository.HistoryRepositoryImpl
import com.mohannedabuhassira.youtubedownloader.features.history.domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class HistoryModule {
    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository
}
