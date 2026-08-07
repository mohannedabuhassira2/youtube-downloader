package com.mohannedabuhassira.youtubedownloader.core.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mohannedabuhassira.youtubedownloader.core.storage.converters.StorageConverters
import com.mohannedabuhassira.youtubedownloader.core.storage.dao.MediaItemDao
import com.mohannedabuhassira.youtubedownloader.core.storage.entity.MediaItemEntity

@Database(entities = [MediaItemEntity::class], version = 1, exportSchema = false)
@TypeConverters(StorageConverters::class)
internal abstract class YoutubeDownloaderDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
}