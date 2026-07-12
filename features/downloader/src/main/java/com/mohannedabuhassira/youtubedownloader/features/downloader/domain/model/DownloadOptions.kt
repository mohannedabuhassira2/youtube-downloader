package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

internal data class DownloadOptions(
    val url: String,
    val destinationPath: String,
    val type: DownloadType
)

internal enum class DownloadType {
    AUDIO, VIDEO
}
