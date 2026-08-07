package com.mohannedabuhassira.youtubedownloader.features.history.domain.model

internal data class DownloadFile(
    val id: Long,
    val title: String,
    val quality: String?,
    val filePath: String,
    val thumbnail: String?,
    val type: DownloadFileType,
    val format: DownloadFormat,
    val timestamp: Long,
    val sizeBytes: Long,
    val status: DownloadStatus = DownloadStatus.COMPLETED
)

internal enum class DownloadFileType {
    AUDIO, VIDEO
}

internal enum class DownloadStatus {
    COMPLETED, MISSING
}

internal enum class DownloadFormat(val extension: String) {
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
