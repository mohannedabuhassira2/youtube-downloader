package com.mohannedabuhassira.youtubedownloader.features.downloader.data.repository

import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloader
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloadConfig
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadOptions
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
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

    override suspend fun getInfo(url: String): ApiResult<VideoModel> {
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

    override fun download(options: DownloadOptions): Flow<ApiResult<Float>> {
        val config = NetworkDownloadConfig(
            url = options.url,
            destinationPath = options.destinationPath,
            isAudioOnly = options.type == DownloadType.AUDIO
        )

        return networkDownloader.streamDownload(config)
            .map { progress ->
                if (progress >= 100f) {
                    ApiResult.Success(progress)
                } else {
                    ApiResult.Loading(progress)
                }
            }
            .onStart { emit(ApiResult.Loading(0f)) }
            .catch { e -> emit(ApiResult.Error(e.message, e)) }
    }
}
