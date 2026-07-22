package com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.ui

import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQuality

data class DownloaderUiState(
    val url: String = "",
    val isLoading: Boolean = false,
    val videoInfo: VideoModel? = null,
    val progress: Float? = null,
    val errorMessage: String? = null,
    val isDownloading: Boolean = false,
    val downloadedFilePath: String? = null,
    val selectedQuality: VideoQuality? = null,
    val selectedAudioQuality: AudioQuality? = null,
    val selectedDownloadType: DownloadType? = null
)
