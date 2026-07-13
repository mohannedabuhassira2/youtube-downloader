package com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.ui

import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQuality

internal data class DownloaderUiState(
    val url: String = "",
    val isLoading: Boolean = false,
    val videoInfo: VideoModel? = null,
    val progress: Float? = null,
    val errorMessage: String? = null,
    val isDownloading: Boolean = false,
    val downloadedFilePath: String? = null,
    val selectedQuality: VideoQuality = VideoQuality.BEST
)
