package com.mohannedabuhassira.youtubedownloader.core.storage.converters

import androidx.room.TypeConverter
import com.mohannedabuhassira.youtubedownloader.core.storage.entity.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.core.storage.entity.MediaType

internal class StorageConverters {
    @TypeConverter
    fun fromMediaType(value: MediaType): String = value.name

    @TypeConverter
    fun toMediaType(value: String): MediaType = MediaType.valueOf(value)

    @TypeConverter
    fun fromDownloadStatus(value: DownloadStatus): String = value.name

    @TypeConverter
    fun toDownloadStatus(value: String): DownloadStatus = DownloadStatus.valueOf(value)
}
