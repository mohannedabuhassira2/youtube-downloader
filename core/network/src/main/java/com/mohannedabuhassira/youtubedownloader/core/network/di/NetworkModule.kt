package com.mohannedabuhassira.youtubedownloader.core.network.di

import android.content.Context
import com.mohannedabuhassira.youtubedownloader.core.network.factory.YoutubeDLFactory
import com.farimarwat.library.YoutubeDL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideYoutubeDLEngine(
        @ApplicationContext context: Context
    ): YoutubeDL {
        return YoutubeDLFactory.create(context)
    }
}
