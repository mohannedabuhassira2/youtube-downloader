package com.mohannedabuhassira.youtubedownloader.features.history.data.repository

import android.content.Context
import android.os.Environment
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFile
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFileType
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFormat
import com.mohannedabuhassira.youtubedownloader.features.history.domain.repository.HistoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : HistoryRepository {

    override fun getDownloadedFiles(): Flow<List<DownloadFile>> = flow {
        val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val files = downloadDir?.listFiles()?.filter { file ->
            file.isFile && DownloadFormat.entries.any { it.extension.equals(file.extension, ignoreCase = true) }
        }?.map {
            val ext = it.extension.lowercase()
            val thumbFile = File("${it.absolutePath}.thumb")
            val thumbnailUrl = if (thumbFile.exists()) thumbFile.readText() else null
            
            DownloadFile(
                title = it.nameWithoutExtension,
                filePath = it.absolutePath,
                thumbnail = thumbnailUrl,
                type = if (ext == "mp4") DownloadFileType.VIDEO else DownloadFileType.AUDIO,
                format = DownloadFormat.fromExtension(ext),
                timestamp = it.lastModified(),
                sizeBytes = it.length()
            )
        }?.sortedByDescending { it.timestamp } ?: emptyList()
        
        emit(files)
    }

    override suspend fun deleteFile(file: DownloadFile): Boolean {
        File("${file.filePath}.thumb").delete()
        return File(file.filePath).delete()
    }
}
