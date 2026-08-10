package com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mohannedabuhassira.youtubedownloader.core.network.client.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadOptions
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.repository.DownloaderRepository
import com.mohannedabuhassira.youtubedownloader.features.downloader.service.DownloadProgressManager
import com.mohannedabuhassira.youtubedownloader.features.downloader.service.DownloadService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DownloaderViewModel @Inject constructor(
    application: Application,
    private val repository: DownloaderRepository,
    private val progressManager: DownloadProgressManager
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DownloaderUiState())
    val uiState: StateFlow<DownloaderUiState> = _uiState.asStateFlow()

    init {
        observeProgress()
    }

    private fun observeProgress() {
        progressManager.downloadProgress
            .onEach { result ->
                result?.let { handleDownloadResult(it) }
            }
            .launchIn(viewModelScope)
    }

    private fun handleDownloadResult(result: ApiResult<DownloadStatus>) {
        when (result) {
            is ApiResult.Loading -> {
                _uiState.update {
                    it.copy(
                        isDownloading = true,
                        progress = result.progress
                    )
                }
            }
            is ApiResult.Success -> {
                val successStatus = result.data as? DownloadStatus.Completed
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        progress = 100f,
                        downloadedFilePath = successStatus?.filePath
                    )
                }
            }
            is ApiResult.Error -> {
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun onUrlChange(newUrl: String) {
        _uiState.update {
            if (newUrl.isEmpty()) {
                progressManager.clearProgress()
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
        _uiState.update {
            it.copy(
                selectedQuality = quality,
                selectedAudioQuality = null,
                selectedDownloadType = DownloadType.VIDEO
            )
        }
    }

    fun onAudioQualityChange(quality: AudioQuality) {
        _uiState.update {
            it.copy(
                selectedAudioQuality = quality,
                selectedQuality = null,
                selectedDownloadType = DownloadType.AUDIO
            )
        }
    }

    fun fetchVideoInfo() {
        viewModelScope.launch {
            progressManager.clearProgress()
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
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            videoInfo = result.data
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is ApiResult.Loading -> {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun startDownload() {
        val type = _uiState.value.selectedDownloadType ?: return
        val videoInfo = _uiState.value.videoInfo ?: return

        val options = DownloadOptions(
            url = _uiState.value.url,
            title = videoInfo.title,
            thumbnail = videoInfo.thumbnail,
            duration = videoInfo.duration,
            uploader = videoInfo.uploader,
            type = type,
            videoQuality = _uiState.value.selectedQuality ?: VideoQuality.P144,
            audioQuality = _uiState.value.selectedAudioQuality ?: AudioQuality.K128
        )

        DownloadService.start(getApplication(), options)
    }

    fun resetDownloadState() {
        progressManager.clearProgress()
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
