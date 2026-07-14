package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

data class VideoModel(
    val id: String?,
    val title: String?,
    val thumbnail: String?,
    val duration: Int,
    val uploader: String?,
    val qualities: List<QualityInfo> = emptyList(),
    val audioFormats: List<AudioFormatInfo> = emptyList()
)

data class QualityInfo(
    val quality: VideoQuality,
    val sizeBytes: Long? = null
)

data class AudioFormatInfo(
    val format: AudioFormat,
    val sizeBytes: Long? = null,
    val bitrateKbps: Int? = null
)
