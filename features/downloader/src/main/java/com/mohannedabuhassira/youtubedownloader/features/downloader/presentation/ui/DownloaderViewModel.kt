package com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.ui

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohannedabuhassira.youtubedownloader.core.network.client.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadOptions
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.repository.DownloaderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DownloaderViewModel @Inject constructor(
    private val repository: DownloaderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(DownloaderUiState())
    val uiState: StateFlow<DownloaderUiState> = _uiState.asStateFlow()

    fun onUrlChange(newUrl: String) {
        _uiState.update { 
            if (newUrl.isEmpty()) {
                it.copy(
                    url = "", 
                    videoInfo = null, 
                    errorMessage = null, 
                    downloadedFilePath = null, 
                    progress = null, 
                    isDownloading = false
                )
            } else {
                it.copy(url = newUrl)
            }
        }
    }

    fun onQualityChange(quality: VideoQuality) {
        _uiState.update { it.copy(
            selectedQuality = quality,
            selectedAudioQuality = null,
            selectedDownloadType = DownloadType.VIDEO
        ) }
    }

    fun onAudioQualityChange(quality: AudioQuality) {
        _uiState.update { it.copy(
            selectedAudioQuality = quality,
            selectedQuality = null,
            selectedDownloadType = DownloadType.AUDIO
        ) }
    }

    fun fetchVideoInfo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    videoInfo = null,
                    downloadedFilePath = null,
                    progress = null,
                    isDownloading = false,
                    selectedQuality = null,
                    selectedAudioQuality = null,
                    selectedDownloadType = null
                )
            }
            when (val result = repository.getInfo(_uiState.value.url)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, videoInfo = result.data) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun startDownload() {
        viewModelScope.launch {
            val type = _uiState.value.selectedDownloadType ?: return@launch
            
            // For simplicity and visibility in gallery, we save to public Download/YoutubeDownloader
            val publicDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "YoutubeDownloader")
            if (!publicDir.exists()) {
                publicDir.mkdirs()
            }
            
            val options = DownloadOptions(
                url = _uiState.value.url,
                title = _uiState.value.videoInfo?.title,
                thumbnail = _uiState.value.videoInfo?.thumbnail,
                duration = _uiState.value.videoInfo?.duration ?: 0,
                uploader = _uiState.value.videoInfo?.uploader,
                destinationPath = publicDir.absolutePath,
                type = type,
                videoQuality = _uiState.value.selectedQuality ?: VideoQuality.P144,
                audioQuality = _uiState.value.selectedAudioQuality ?: AudioQuality.K128
            )

            repository
                .download(options)
                .collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            val status = result.data as? DownloadStatus.Completed
                            if (status != null) {
                                // Trigger media scan so it shows up in Gallery/Audio manager
                                MediaScannerConnection.scanFile(
                                    context,
                                    arrayOf(status.filePath),
                                    null,
                                    null
                                )
                                
                                _uiState.update { it.copy(
                                    isDownloading = false,
                                    progress = 100f,
                                    downloadedFilePath = status.filePath
                                ) }
                            }
                        }
                        is ApiResult.Error -> {
                            _uiState.update { it.copy(isDownloading = false, errorMessage = result.message) }
                        }
                        is ApiResult.Loading -> {
                            _uiState.update { it.copy(isDownloading = true, progress = result.progress) }
                        }
                    }
                }
        }
    }

    fun resetDownloadState() {
        _uiState.update {
            it.copy(
                downloadedFilePath = null,
                progress = null,
                isDownloading = false,
                selectedQuality = null,
                selectedAudioQuality = null,
                selectedDownloadType = null
            )
        }
    }
}
