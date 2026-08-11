package com.mohannedabuhassira.youtubedownloader.features.downloader.data.repository

import android.content.Context
import android.media.MediaScannerConnection
import com.mohannedabuhassira.youtubedownloader.core.network.client.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkDownloader
import com.mohannedabuhassira.youtubedownloader.core.storage.dao.MediaItemDao
import com.mohannedabuhassira.youtubedownloader.core.storage.entity.MediaItemEntity
import com.mohannedabuhassira.youtubedownloader.core.storage.entity.MediaType
import com.mohannedabuhassira.youtubedownloader.core.storage.entity.DownloadStatus as StorageDownloadStatus
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.mapper.toVideoModel
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.remote.YoutubeCliRequestBuilder
import com.mohannedabuhassira.youtubedownloader.features.downloader.di.DownloadLocation
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadOptions
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.repository.DownloaderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DownloaderRepositoryImpl @Inject constructor(
    private val networkDownloader: NetworkDownloader,
    private val mediaItemDao: MediaItemDao,
    private val cliRequestBuilder: YoutubeCliRequestBuilder,
    @ApplicationContext private val context: Context,
    @DownloadLocation private val downloadDirectory: File
) : DownloaderRepository {
    override suspend fun getInfo(url: String): ApiResult<VideoModel> {
        return try {
            val cliOptions = cliRequestBuilder.buildInfoOptions()
            val info = networkDownloader.getInfo(
                url = url,
                options = cliOptions
            )

            ApiResult.Success(info.toVideoModel())
        } catch (e: Exception) {
            ApiResult.Error(e.message, e)
        }
    }

    override fun download(options: DownloadOptions): Flow<ApiResult<DownloadStatus>> {
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs()
        }

        val qualityLabel = if (options.type == DownloadType.VIDEO) {
            options.videoQuality.label
        } else {
            options.audioQuality.label
        }
        
        val extension = if (options.type == DownloadType.AUDIO) "mp3" else "mp4"
        val baseTitle = "${options.title ?: "Unknown"} [$qualityLabel]"
        
        val uniqueTitle = getUniqueFileName(
            directory = downloadDirectory,
            baseName = baseTitle,
            extension = extension
        )
        
        val expectedFilePath = File(
            downloadDirectory,
            "$uniqueTitle.$extension"
        ).absolutePath
        
        val outputTemplate = "${downloadDirectory.absolutePath}/$uniqueTitle.%(ext)s"

        val cliOptions = cliRequestBuilder.buildDownloadOptions(
            options = options,
            outputTemplate = outputTemplate
        )

        val downloadedResult = networkDownloader.download(
            url = options.url,
            options = cliOptions,
            resultFilePath = expectedFilePath
        )

        return downloadedResult
            .onEach { status ->
                if (status is DownloadStatus.Completed) {
                    saveCompletedDownload(
                        options = options,
                        filePath = status.filePath,
                        qualityLabel = qualityLabel
                    )
                }
            }
            .map { status ->
                when (status) {
                    is DownloadStatus.Progress -> ApiResult.Loading(status.percentage)
                    is DownloadStatus.Completed -> ApiResult.Success(status)
                    is DownloadStatus.Failed -> ApiResult.Error(status.message)
                }
            }
            .onStart { emit(ApiResult.Loading(0f)) }
            .catch { e -> emit(ApiResult.Error(e.message ?: "Unknown error", e)) }
    }

    private suspend fun saveCompletedDownload(
        options: DownloadOptions,
        filePath: String,
        qualityLabel: String
    ) {
        try {
            val file = File(filePath)
            val sizeBytes = if (file.exists()) file.length() else 0L

            val entity = MediaItemEntity(
                title = options.title ?: "Unknown",
                mediaType = if (options.type == DownloadType.AUDIO) MediaType.AUDIO else MediaType.VIDEO,
                quality = qualityLabel,
                remoteUrl = options.url,
                localFilePath = filePath,
                thumbnailUrl = options.thumbnail,
                downloadStatus = StorageDownloadStatus.COMPLETED,
                durationMs = options.duration.toLong() * 1000L,
                sizeBytes = sizeBytes
            )

            mediaItemDao.insertMediaItem(entity)
            MediaScannerConnection.scanFile(
                context,
                arrayOf(filePath),
                null,
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUniqueFileName(
        directory: File,
        baseName: String,
        extension: String
    ): String {
        var name = baseName
        var file = File(directory, "$name.$extension")
        var counter = 1
        
        while (file.exists()) {
            name = "$baseName ($counter)"
            file = File(directory, "$name.$extension")
            counter++
        }
        
        return name
    }
}
