package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

data class VideoModel(
    val id: String?,
    val title: String?,
    val thumbnail: String?,
    val duration: Int,
    val uploader: String?,
    val qualities: List<QualityInfo> = emptyList(),
    val audioQualities: List<AudioQualityInfo> = emptyList()
)

data class QualityInfo(
    val quality: VideoQuality,
    val sizeBytes: Long? = null
)

data class AudioQualityInfo(
    val quality: AudioQuality,
    val sizeBytes: Long? = null
)
