package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

internal data class VideoModel(
    val title: String?,
    val thumbnail: String?,
    val duration: Int,
    val uploader: String?
)
