package com.mohannedabuhassira.youtubedownloader.core.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaType {
    AUDIO, VIDEO
}

enum class DownloadStatus {
    COMPLETED, MISSING
}

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val mediaType: MediaType,
    val quality: String,
    val remoteUrl: String,
    val localFilePath: String,
    val thumbnailUrl: String?,
    val downloadStatus: DownloadStatus,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val sizeBytes: Long = 0L
)
