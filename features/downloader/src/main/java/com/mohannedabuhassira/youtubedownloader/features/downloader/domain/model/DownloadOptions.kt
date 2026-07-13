package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

internal data class DownloadOptions(
    val url: String,
    val title: String?,
    val destinationPath: String,
    val type: DownloadType,
    val videoQuality: VideoQuality = VideoQuality.BEST
)

internal enum class DownloadType {
    AUDIO, VIDEO
}

internal enum class VideoQuality(val label: String) {
    LOW("360p"),
    MEDIUM("720p"),
    HIGH("1080p"),
    BEST("Best")
}
