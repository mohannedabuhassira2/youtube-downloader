package com.mohannedabuhassira.youtubedownloader.features.downloader.data.repository

import com.mohannedabuhassira.youtubedownloader.core.network.client.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloader
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloadConfig
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadOptions
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.repository.DownloaderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DownloaderRepositoryImpl @Inject constructor(
    private val networkDownloader: NetworkDownloader
) : DownloaderRepository {

    override suspend fun getInfo(
        url: String
    ): ApiResult<VideoModel> {
        return try {
            val info = networkDownloader.getInfo(url)
            ApiResult.Success(
                VideoModel(
                    title = info.title,
                    thumbnail = info.thumbnail,
                    duration = info.duration,
                    uploader = info.uploader
                )
            )
        } catch (e: Exception) {
            ApiResult.Error(e.message, e)
        }
    }

    override fun download(
        options: DownloadOptions
    ): Flow<ApiResult<DownloadStatus>> {
        val videoFormat = when (options.videoQuality) {
            VideoQuality.LOW -> "bestvideo[height<=360]+bestaudio/best[height<=360]"
            VideoQuality.MEDIUM -> "bestvideo[height<=720]+bestaudio/best[height<=720]"
            VideoQuality.HIGH -> "bestvideo[height<=1080]+bestaudio/best[height<=1080]"
            VideoQuality.BEST -> "bestvideo+bestaudio/best"
        }

        val qualitySuffix = if (options.type == DownloadType.VIDEO) " [${options.videoQuality.label}]" else ""
        val titleWithQuality = "${options.title}$qualitySuffix"

        val config = NetworkDownloadConfig(
            url = options.url,
            title = titleWithQuality,
            destinationPath = options.destinationPath,
            isAudioOnly = options.type == DownloadType.AUDIO,
            videoQuality = videoFormat
        )

        return networkDownloader.streamDownload(config)
            .map { status ->
                when (status) {
                    is DownloadStatus.Progress -> {
                        ApiResult.Loading(status.percentage)
                    }
                    is DownloadStatus.Completed -> {
                        ApiResult.Success(status)
                    }
                    is DownloadStatus.Failed -> {
                        ApiResult.Error(status.message)
                    }
                }
            }
            .onStart { emit(ApiResult.Loading(0f)) }
            .catch { e -> emit(ApiResult.Error(e.message ?: "Unknown error", e)) }
    }
}
