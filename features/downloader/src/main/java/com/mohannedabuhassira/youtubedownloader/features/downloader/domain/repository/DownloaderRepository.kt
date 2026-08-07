package com.mohannedabuhassira.youtubedownloader.features.downloader.domain.repository

import com.mohannedabuhassira.youtubedownloader.core.network.client.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadOptions
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel
import kotlinx.coroutines.flow.Flow

internal interface DownloaderRepository {
    suspend fun getInfo(url: String): ApiResult<VideoModel>
    fun download(options: DownloadOptions): Flow<ApiResult<DownloadStatus>>
}
