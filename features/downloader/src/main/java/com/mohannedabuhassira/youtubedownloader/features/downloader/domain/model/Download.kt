package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

data class DownloadOptions(
    val url: String,
    val title: String?,
    val thumbnail: String?,
    val duration: Int,
    val uploader: String?,
    val type: DownloadType,
    val videoQuality: VideoQuality = VideoQuality.P144,
    val audioQuality: AudioQuality = AudioQuality.K128
)

enum class DownloadType {
    AUDIO, VIDEO
}
