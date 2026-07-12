package com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohannedabuhassira.youtubedownloader.core.designsystem.theme.YoutubeDownloaderTheme
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel

@Composable
fun DownloaderEntry() {
    val viewModel: DownloaderViewModel = hiltViewModel()
    DownloaderScreen(viewModel = viewModel)
}

@Composable
internal fun DownloaderScreen(
    viewModel: DownloaderViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    DownloaderScreenContent(
        uiState = uiState,
        onUrlChange = viewModel::onUrlChange,
        onGetInfoClick = viewModel::fetchVideoInfo,
        onDownloadClick = viewModel::startDownload
    )
}

@Composable
internal fun DownloaderScreenContent(
    uiState: DownloaderUiState,
    onUrlChange: (String) -> Unit,
    onGetInfoClick: () -> Unit,
    onDownloadClick: (DownloadType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.url,
            onValueChange = onUrlChange,
            label = { Text("YouTube URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onGetInfoClick,
            enabled = uiState.url.isNotEmpty() && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Get Video Info")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        uiState.videoInfo?.let { info ->
            Text(text = "Title: ${info.title}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Uploader: ${info.uploader}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Duration: ${info.duration}s", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(
                    onClick = { onDownloadClick(DownloadType.AUDIO) },
                    enabled = !uiState.isDownloading,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Text("Audio")
                }
                Button(
                    onClick = { onDownloadClick(DownloadType.VIDEO) },
                    enabled = !uiState.isDownloading,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Text("Video")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isDownloading) {
            Text(text = "Downloading...")
            uiState.progress?.let {
                LinearProgressIndicator(
                    progress = { it / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "${it.toInt()}%")
            }
        }

        uiState.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloaderScreenPreview() {
    YoutubeDownloaderTheme {
        DownloaderScreenContent(
            uiState = DownloaderUiState(
                url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                videoInfo = VideoModel(
                    title = "Never Gonna Give You Up",
                    uploader = "Rick Astley",
                    duration = 212,
                    thumbnail = null
                )
            ),
            onUrlChange = {},
            onGetInfoClick = {},
            onDownloadClick = {}
        )
    }
}
