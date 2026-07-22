package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

data class DownloadOptions(
    val url: String,
    val title: String?,
    val thumbnail: String?,
    val duration: Int,
    val uploader: String?,
    val destinationPath: String,
    val type: DownloadType,
    val videoQuality: VideoQuality = VideoQuality.P144,
    val audioQuality: AudioQuality = AudioQuality.K128
)

enum class DownloadType {
    AUDIO, VIDEO
}

enum class VideoQuality(val label: String) {
    P144("144p"),
    P240("240p"),
    P360("360p"),
    P480("480p"),
    P720("720p"),
    P1080("1080p"),
    P1440("1440p"),
    P2160("2160p")
}

enum class AudioQuality(val kbps: Int) {
    K64(64),
    K128(128),
    K192(192),
    K256(256),
    K320(320);

    val label: String get() = "${kbps}kbps"
}
