package com.mohannedabuhassira.youtubedownloader.features.history.data.repository

import android.util.Log
import com.mohannedabuhassira.youtubedownloader.core.storage.dao.MediaItemDao
import com.mohannedabuhassira.youtubedownloader.core.storage.entity.MediaItemEntity
import com.mohannedabuhassira.youtubedownloader.core.storage.entity.DownloadStatus as StorageDownloadStatus
import com.mohannedabuhassira.youtubedownloader.core.storage.entity.MediaType as StorageMediaType
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFile
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFileType
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFormat
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.features.history.domain.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class HistoryRepositoryImpl @Inject constructor(
    private val mediaItemDao: MediaItemDao
) : HistoryRepository {
    override fun getDownloadedFiles(): Flow<List<DownloadFile>> {
        return mediaItemDao.getAllMediaItems()
            .map { entities -> entities.map { it.toDownloadFile() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun deleteFile(file: DownloadFile): Boolean = withContext(Dispatchers.IO) {
        try {
            if (file.filePath.isNotEmpty()) {
                val diskFile = File(file.filePath)
                if (diskFile.exists()) {
                    diskFile.delete()
                }
            }
            mediaItemDao.deleteMediaItemById(file.id)
            true
        } catch (e: Exception) {
            Log.e("HistoryRepositoryImpl", "Failed to delete file: ${file.filePath}", e)
            false
        }
    }

    private fun MediaItemEntity.toDownloadFile(): DownloadFile {
        val fileExists = localFilePath.isNotEmpty() && File(localFilePath).exists()

        val status = if (downloadStatus == StorageDownloadStatus.COMPLETED && !fileExists) {
            DownloadStatus.MISSING
        } else {
            DownloadStatus.COMPLETED
        }

        val extension = if (localFilePath.isNotEmpty()) localFilePath.substringAfterLast(".", "") else ""
        val defaultExtension = if (mediaType == StorageMediaType.AUDIO) "mp3" else "mp4"

        return DownloadFile(
            id = id,
            title = title,
            quality = quality,
            filePath = localFilePath,
            thumbnail = thumbnailUrl,
            type = if (mediaType == StorageMediaType.AUDIO) DownloadFileType.AUDIO else DownloadFileType.VIDEO,
            format = DownloadFormat.fromExtension(extension.ifEmpty { defaultExtension }),
            timestamp = timestamp,
            sizeBytes = sizeBytes,
            status = status
        )
    }
}

