package com.mohannedabuhassira.youtubedownloader.features.history.data.repository

import com.mohannedabuhassira.youtubedownloader.core.storage.dao.MediaItemDao
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
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val mediaItemDao: MediaItemDao
) : HistoryRepository {
    override fun getDownloadedFiles(): Flow<List<DownloadFile>> {
        return mediaItemDao.getAllMediaItems()
            .map { entities ->
                entities.map { entity ->
                    val fileExists = entity.localFilePath.isNotEmpty() && File(entity.localFilePath).exists()

                    val status = if (entity.downloadStatus == StorageDownloadStatus.COMPLETED && !fileExists) {
                        DownloadStatus.MISSING
                    } else {
                        DownloadStatus.COMPLETED
                    }

                    val extension = if (entity.localFilePath.isNotEmpty()) entity.localFilePath.substringAfterLast(".", "") else ""

                    DownloadFile(
                        id = entity.id,
                        title = entity.title,
                        quality = entity.quality,
                        filePath = entity.localFilePath,
                        thumbnail = entity.thumbnailUrl,
                        type = if (entity.mediaType == StorageMediaType.AUDIO) DownloadFileType.AUDIO else DownloadFileType.VIDEO,
                        format = DownloadFormat.fromExtension(extension.ifEmpty { if (entity.mediaType == StorageMediaType.AUDIO) "mp3" else "mp4" }),
                        timestamp = entity.timestamp,
                        sizeBytes = entity.sizeBytes,
                        status = status
                    )
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun deleteFile(file: DownloadFile): Boolean {
        return try {
            if (file.filePath.isNotEmpty()) {
                val diskFile = File(file.filePath)
                if (diskFile.exists()) {
                    diskFile.delete()
                }
            }
            
            mediaItemDao.deleteMediaItemById(file.id)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
