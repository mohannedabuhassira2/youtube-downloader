package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

data class VideoModel(
    val id: String?,
    val title: String?,
    val thumbnail: String?,
    val duration: Int,
    val uploader: String?,
    val qualities: List<VideoQualityInfo> = emptyList(),
    val audioQualities: List<AudioQualityInfo> = emptyList()
)
