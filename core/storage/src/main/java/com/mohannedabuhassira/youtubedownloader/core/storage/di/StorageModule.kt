package com.mohannedabuhassira.youtubedownloader.core.storage.di

import android.content.Context
import androidx.room.Room
import com.mohannedabuhassira.youtubedownloader.core.storage.database.YoutubeDownloaderDatabase
import com.mohannedabuhassira.youtubedownloader.core.storage.dao.MediaItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object StorageModule {
    private const val DATABASE_NAME = "youtube_downloader.db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): YoutubeDownloaderDatabase {
        return Room.databaseBuilder(
            context,
            YoutubeDownloaderDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideMediaItemDao(database: YoutubeDownloaderDatabase): MediaItemDao {
        return database.mediaItemDao()
    }
}
