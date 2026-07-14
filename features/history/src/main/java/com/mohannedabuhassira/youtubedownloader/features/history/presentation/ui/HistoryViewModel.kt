package com.mohannedabuhassira.youtubedownloader.features.history.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFile
import com.mohannedabuhassira.youtubedownloader.features.history.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {

    private val _historyItems = MutableStateFlow<List<DownloadFile>>(emptyList())
    val historyItems: StateFlow<List<DownloadFile>> = _historyItems.asStateFlow()

    init {
        refreshHistory()
    }

    fun refreshHistory() {
        viewModelScope.launch {
            repository.getDownloadedFiles().collect {
                _historyItems.value = it
            }
        }
    }

    fun deleteItem(item: DownloadFile) {
        viewModelScope.launch {
            if (repository.deleteFile(item)) {
                refreshHistory()
            }
        }
    }
}
