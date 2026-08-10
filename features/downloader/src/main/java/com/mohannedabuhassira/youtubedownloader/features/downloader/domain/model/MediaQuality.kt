package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

import kotlinx.serialization.Serializable

@Serializable
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

@Serializable
enum class AudioQuality(val kbps: Int) {
    K64(64),
    K128(128),
    K192(192),
    K256(256),
    K320(320);

    val label: String get() = "${kbps}kbps"
}

data class VideoQualityInfo(
    val quality: VideoQuality,
    val sizeBytes: Long? = null
)

data class AudioQualityInfo(
    val quality: AudioQuality,
    val sizeBytes: Long? = null
)
