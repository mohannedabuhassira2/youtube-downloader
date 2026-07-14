package com.mohannedabuhassira.youtubedownloader.features.history.domain.repository

import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFile
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getDownloadedFiles(): Flow<List<DownloadFile>>
    suspend fun deleteFile(file: DownloadFile): Boolean
}
