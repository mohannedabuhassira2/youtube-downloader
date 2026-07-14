package com.mohannedabuhassira.youtubedownloader.features.downloader.data.repository

import com.mohannedabuhassira.youtubedownloader.core.network.client.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloader
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloadConfig
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioFormat
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioFormatInfo
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadOptions
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.QualityInfo
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.repository.DownloaderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

import java.io.File
import kotlinx.coroutines.flow.onEach

@Singleton
class DownloaderRepositoryImpl @Inject constructor(
    private val networkDownloader: NetworkDownloader
) : DownloaderRepository {
    override suspend fun getInfo(
        url: String
    ): ApiResult<VideoModel> {
        return try {
            val info = networkDownloader.getInfo(url)
            val formats = info.formats.orEmpty()

            // Map qualities with estimated sizes, completely filtering out missing ones
            val qualities = VideoQuality.entries.mapNotNull { q ->
                val format = if (q == VideoQuality.BEST) {
                    formats.filter { it.height > 0 }.maxByOrNull { it.height }
                } else {
                    formats.find { it.height == q.height }
                }

                when {
                    format != null -> QualityInfo(q, format.fileSize)
                    q == VideoQuality.BEST -> QualityInfo(q, null)
                    else -> null
                }
            }

            // Map audio formats with estimated sizes
            val audioFormats = AudioFormat.entries.map { af ->
                val audioOnly = formats.filter { it.vcodec == "none" }
                val format = audioOnly.filter { it.ext == af.extension }.maxByOrNull { it.asr }
                    ?: audioOnly.maxByOrNull { it.asr }

                AudioFormatInfo(af, format?.fileSize, format?.abr?.toInt())
            }

            ApiResult.Success(
                VideoModel(
                    id = info.id,
                    title = info.title,
                    thumbnail = info.thumbnail,
                    duration = info.duration,
                    uploader = info.uploader,
                    qualities = qualities,
                    audioFormats = audioFormats
                )
            )
        } catch (e: Exception) {
            ApiResult.Error(e.message, e)
        }
    }

    override fun download(
        options: DownloadOptions
    ): Flow<ApiResult<DownloadStatus>> {
        val videoFormat = when (val quality = options.videoQuality) {
            VideoQuality.BEST -> "bestvideo+bestaudio/best"
            else -> {
                val height = quality.name.removePrefix("P")
                "bestvideo[height<=$height]+bestaudio/best[height<=$height]"
            }
        }

        val qualitySuffix = if (options.type == DownloadType.VIDEO) " [${options.videoQuality.label}]" else ""
        val titleWithQuality = "${options.title}$qualitySuffix"

        val config = NetworkDownloadConfig(
            url = options.url,
            title = titleWithQuality,
            destinationPath = options.destinationPath,
            isAudioOnly = options.type == DownloadType.AUDIO,
            audioFormat = options.audioFormat.extension,
            videoFormat = videoFormat
        )

        return networkDownloader.streamDownload(config)
            .onEach { result ->
                if (result is DownloadStatus.Completed) {
                    options.thumbnail?.let { url ->
                        try {
                            val file = File("${result.filePath}.thumb")
                            file.writeText(url)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }.map { status ->
                when (status) {
                    is DownloadStatus.Progress -> ApiResult.Loading(status.percentage)
                    is DownloadStatus.Completed -> ApiResult.Success(status)
                    is DownloadStatus.Failed -> ApiResult.Error(status.message)
                }
            }
            .onStart { emit(ApiResult.Loading(0f)) }
            .catch { e -> emit(ApiResult.Error(e.message ?: "Unknown error", e)) }
    }
}
