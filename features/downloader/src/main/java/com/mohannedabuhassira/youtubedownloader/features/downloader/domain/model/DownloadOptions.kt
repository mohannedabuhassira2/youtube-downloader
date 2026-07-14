package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model

data class DownloadOptions(
    val url: String,
    val title: String?,
    val thumbnail: String?,
    val duration: Int,
    val uploader: String?,
    val destinationPath: String,
    val type: DownloadType,
    val videoQuality: VideoQuality = VideoQuality.BEST,
    val audioFormat: AudioFormat = AudioFormat.MP3
)

enum class DownloadType {
    AUDIO, VIDEO
}

enum class VideoQuality(val label: String, val height: Int) {
    P144("144p", 144),
    P240("240p", 240),
    P360("360p", 360),
    P480("480p", 480),
    P720("720p", 720),
    P1080("1080p", 1080),
    P1440("2K", 1440),
    P2160("4K", 2160),
    BEST("Best", 0)
}

enum class AudioFormat(val extension: String) {
    MP3("mp3"),
    M4A("m4a"),
    WAV("wav"),
    FLAC("flac")
}
