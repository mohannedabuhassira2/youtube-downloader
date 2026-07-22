package com.mohannedabuhassira.youtubedownloader.features.downloader.data.repository

import com.mohannedabuhassira.youtubedownloader.core.network.client.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloader
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloadConfig
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioQualityInfo
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

            // Map video qualities with estimated sizes (p)
            val qualities = VideoQuality.entries.mapNotNull { q ->
                val format = formats.find { it.formatNote?.contains(q.label) == true }

                if (format != null) {
                    QualityInfo(q, format.fileSize)
                } else {
                    null
                }
            }

            // Map audio qualities with estimated sizes (kps)
            val durationSeconds = info.duration.toLong()
            val audioQualities = AudioQuality.entries.map { aq ->
                val estimatedSizeBytes = if (durationSeconds > 0) {
                    (aq.kbps * 1000L * durationSeconds) / 8
                } else null
                
                AudioQualityInfo(aq, estimatedSizeBytes)
            }

            ApiResult.Success(
                VideoModel(
                    id = info.id,
                    title = info.title,
                    thumbnail = info.thumbnail,
                    duration = info.duration,
                    uploader = info.uploader,
                    qualities = qualities,
                    audioQualities = audioQualities
                )
            )
        } catch (e: Exception) {
            ApiResult.Error(e.message, e)
        }
    }

    override fun download(
        options: DownloadOptions
    ): Flow<ApiResult<DownloadStatus>> {
        val videoFormat = if (options.type == DownloadType.VIDEO) {
            val qualityLabel = options.videoQuality.label
            "bestvideo[format_note*='$qualityLabel']+bestaudio/best"
        } else {
            "bestaudio/best"
        }

        val qualityLabel = if (options.type == DownloadType.VIDEO) options.videoQuality.label else options.audioQuality.label
        val titleWithQuality = "${options.title} [$qualityLabel]"

        val config = NetworkDownloadConfig(
            url = options.url,
            title = titleWithQuality,
            destinationPath = options.destinationPath,
            isAudioOnly = options.type == DownloadType.AUDIO,
            audioFormat = "mp3",
            audioQuality = options.audioQuality.kbps.toString(),
            videoFormat = videoFormat
        )

        return networkDownloader.streamDownload(config)
            .onEach { result ->
                if (result is DownloadStatus.Completed) {
                    try {
                        val thumbFile = File("${result.filePath}.thumb")
                        val qualityLabel = if (options.type == DownloadType.VIDEO) options.videoQuality.label else options.audioQuality.label
                        val metadata = "${options.thumbnail ?: ""}|$qualityLabel|${options.type.name}"
                        thumbFile.writeText(metadata)
                    } catch (e: Exception) {
                        e.printStackTrace()
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
