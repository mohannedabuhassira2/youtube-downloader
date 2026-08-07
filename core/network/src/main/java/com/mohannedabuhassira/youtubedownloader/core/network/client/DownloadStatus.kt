package com.mohannedabuhassira.youtubedownloader.core.network.client

sealed interface DownloadStatus {
    data class Progress(
        val percentage: Float,
        val downloadedBytes: Long = 0L
    ) : DownloadStatus
    data class Completed(val filePath: String) : DownloadStatus
    data class Failed(val message: String?) : DownloadStatus
}