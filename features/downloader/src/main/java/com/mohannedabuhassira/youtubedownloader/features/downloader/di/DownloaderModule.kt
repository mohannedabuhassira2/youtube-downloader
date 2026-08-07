package com.mohannedabuhassira.youtubedownloader.features.downloader.di

import com.mohannedabuhassira.youtubedownloader.features.downloader.data.repository.DownloaderRepositoryImpl
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.repository.DownloaderRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import android.os.Environment
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadLocation

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DownloaderModule {
    @Binds
    @Singleton
    abstract fun bindDownloaderRepository(
        downloaderRepositoryImpl: DownloaderRepositoryImpl
    ): DownloaderRepository

    companion object {
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
}
