package com.mohannedabuhassira.youtubedownloader.features.history.domain.model

data class DownloadFile(
    val id: String,
    val title: String,
    val quality: String?,
    val filePath: String,
    val thumbnail: String?,
    val type: DownloadFileType,
    val format: DownloadFormat,
    val timestamp: Long,
    val sizeBytes: Long
)

enum class DownloadFileType {
    AUDIO, VIDEO
}

enum class DownloadFormat(val extension: String) {
    MP3("mp3"),
    MP4("mp4"),
    M4A("m4a"),
    WAV("wav"),
    FLAC("flac");

    companion object {
        fun fromExtension(ext: String): DownloadFormat {
            return entries.find { it.extension.equals(ext, ignoreCase = true) } ?: MP4
        }
    }
}
