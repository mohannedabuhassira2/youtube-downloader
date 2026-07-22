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
import androidx.core.net.toUri

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : HistoryRepository {
    override fun getDownloadedFiles(): Flow<List<DownloadFile>> = flow {
        val downloadDir = getOrCreateDownloadDirectory()
        val downloadedFiles = scanAndParseFiles(downloadDir)
        emit(downloadedFiles)
    }

    override suspend fun deleteFile(file: DownloadFile): Boolean {
        return try {
            val thumbFile = File("${file.filePath}.thumb")
            if (thumbFile.exists()) {
                thumbFile.delete()
            }

            if (file.filePath.startsWith("content://")) {
                context.contentResolver.delete(file.filePath.toUri(), null, null) > 0
            } else {
                File(file.filePath).delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun getOrCreateDownloadDirectory(): File {
        val publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return File(publicDownloads, "YoutubeDownloader").apply {
            if (!exists()) mkdirs()
        }
    }

    private fun scanAndParseFiles(directory: File): List<DownloadFile> {
        val rawFiles = directory.listFiles() ?: return emptyList()

        return rawFiles
            .filter(::isSupportedDownloadFile)
            .map(::toDownloadFile)
            .sortedByDescending { it.timestamp }
    }

    private fun isSupportedDownloadFile(file: File): Boolean {
        if (!file.isFile) {
            return false
        }
        
        return DownloadFormat.entries.any {
            it.extension.equals(file.extension, ignoreCase = true)
        }
    }

    private fun toDownloadFile(file: File): DownloadFile {
        val extension = file.extension.lowercase()
        val thumbFile = File("${file.absolutePath}.thumb")
        
        var parsedThumbnail: String? = null
        var parsedQuality: String? = null
        var parsedType: DownloadFileType? = null

        try {
            val content = thumbFile.readText()
            val parts = content.split("|")
            parsedThumbnail = parts[0]
            parsedQuality = parts[1]
            parsedType = DownloadFileType.valueOf(parts[2])
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return DownloadFile(
            id = file.absolutePath,
            title = file.nameWithoutExtension,
            quality = parsedQuality,
            filePath = file.absolutePath,
            thumbnail = parsedThumbnail ?: file.absolutePath,
            type = parsedType ?: if (extension == "mp4") DownloadFileType.VIDEO else DownloadFileType.AUDIO,
            format = DownloadFormat.fromExtension(extension),
            timestamp = file.lastModified(),
            sizeBytes = file.length()
        )
    }
}
