package com.mohannedabuhassira.youtubedownloader.features.downloader.service

import com.mohannedabuhassira.youtubedownloader.core.network.client.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DownloadProgressManager @Inject constructor() {
    private val _downloadProgress = MutableStateFlow<ApiResult<DownloadStatus>?>(null)
    val downloadProgress: StateFlow<ApiResult<DownloadStatus>?> = _downloadProgress.asStateFlow()

    fun updateProgress(status: ApiResult<DownloadStatus>) {
        _downloadProgress.value = status
    }

    fun clearProgress() {
        _downloadProgress.value = null
    }
}