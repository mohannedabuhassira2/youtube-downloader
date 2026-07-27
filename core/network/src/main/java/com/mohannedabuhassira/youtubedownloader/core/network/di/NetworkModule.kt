package com.mohannedabuhassira.youtubedownloader.core.network.di

import android.content.Context
import android.os.Environment
import com.mohannedabuhassira.youtubedownloader.core.network.factory.YoutubeDLFactory
import com.farimarwat.library.YoutubeDL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadLocation

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
    
    @Provides
    @Singleton
    @DownloadLocation
    fun provideDownloadLocation(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "YoutubeDownloader"
        )
    }
}
