package com.mohannedabuhassira.youtubedownloader.features.history.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohannedabuhassira.youtubedownloader.core.designsystem.theme.YoutubeDownloaderTheme
import com.mohannedabuhassira.youtubedownloader.features.history.R
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFile
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFileType
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFormat
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.features.history.presentation.components.HistoryItem

@Composable
internal fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyItems by viewModel.historyItems.collectAsState()

    HistoryScreenContent(
        historyItems = historyItems,
        onBack = onBack,
        onDelete = viewModel::deleteItem,
        onPlay = viewModel::playFile,
        onShare = viewModel::shareFile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryScreenContent(
    historyItems: List<DownloadFile>,
    onBack: () -> Unit,
    onDelete: (DownloadFile) -> Unit,
    onPlay: (DownloadFile) -> Unit,
    onShare: (DownloadFile) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                title = {
                    Text(
                        text = stringResource(R.string.download_history),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (historyItems.isEmpty()) {
            EmptyHistoryState(
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = historyItems,
                    key = { it.id }
                ) { item ->
                    HistoryItem(
                        item = item,
                        onDelete = { onDelete(item) },
                        onPlay = { onPlay(item) },
                        onShare = { onShare(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_downloads),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    YoutubeDownloaderTheme {
        HistoryScreenContent(
            historyItems = listOf(
                DownloadFile(
                    id = 1L,
                    title = "Rick Astley - Never Gonna Give You Up",
                    quality = "1080p",
                    filePath = "/path/to/video.mp4",
                    thumbnail = null,
                    type = DownloadFileType.VIDEO,
                    format = DownloadFormat.MP4,
                    timestamp = System.currentTimeMillis(),
                    sizeBytes = 25_800_000,
                    status = DownloadStatus.COMPLETED
                ),
                DownloadFile(
                    id = 2L,
                    title = "Chill Lofi Beats 2024",
                    quality = "320kbps",
                    filePath = "/path/to/music.mp3",
                    thumbnail = null,
                    type = DownloadFileType.AUDIO,
                    format = DownloadFormat.MP3,
                    timestamp = System.currentTimeMillis() - 86400000,
                    sizeBytes = 5_200_000,
                    status = DownloadStatus.COMPLETED
                )
            ),
            onBack = {},
            onDelete = {},
            onPlay = {},
            onShare = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyHistoryStatePreview() {
    YoutubeDownloaderTheme {
        EmptyHistoryState()
    }
}
