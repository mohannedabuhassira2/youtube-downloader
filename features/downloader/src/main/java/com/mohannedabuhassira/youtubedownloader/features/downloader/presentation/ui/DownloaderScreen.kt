package com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.mohannedabuhassira.youtubedownloader.core.designsystem.theme.YoutubeDownloaderTheme
import com.mohannedabuhassira.youtubedownloader.features.downloader.R
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioQualityInfo
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQualityInfo
import com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.components.EmptyState
import com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.components.ErrorDisplay
import com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.components.UrlInputCard
import com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.components.videoinfo.VideoInfoCard

@Composable
internal fun DownloaderScreen(
    onNavigateToHistory: () -> Unit,
    viewModel: DownloaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startDownload()
        }
    }

    DownloaderScreenContent(
        uiState = uiState,
        onUrlChange = viewModel::onUrlChange,
        onQualityChange = viewModel::onQualityChange,
        onAudioQualityChange = viewModel::onAudioQualityChange,
        onGetInfoClick = viewModel::fetchVideoInfo,
        onDownloadClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    viewModel.startDownload()
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                viewModel.startDownload()
            }
        },
        onHistoryClick = onNavigateToHistory,
        onResetDownload = viewModel::resetDownloadState
    )
}

@Composable
private fun DownloaderScreenContent(
    uiState: DownloaderUiState,
    onUrlChange: (String) -> Unit,
    onQualityChange: (VideoQuality) -> Unit,
    onAudioQualityChange: (AudioQuality) -> Unit,
    onGetInfoClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onResetDownload: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.downloader_app_name),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onHistoryClick
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Rounded.History,
                        contentDescription = stringResource(R.string.history_content_desc),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UrlInputCard(
                url = uiState.url,
                isLoading = uiState.isLoading,
                onUrlChange = onUrlChange,
                onGetInfoClick = onGetInfoClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = when {
                    uiState.videoInfo != null -> DownloaderContentState.VideoInfo
                    uiState.errorMessage != null -> DownloaderContentState.Error
                    else -> DownloaderContentState.Empty
                },
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.5f)) togetherWith (fadeOut() + scaleOut(targetScale = 1f))
                },
                label = "MainContent"
            ) { contentState ->
                when (contentState) {
                    DownloaderContentState.VideoInfo -> {
                        uiState.videoInfo?.let { model ->
                            VideoInfoCard(
                                videoInfo = model,
                                isDownloading = uiState.isDownloading,
                                progress = uiState.progress,
                                downloadedFilePath = uiState.downloadedFilePath,
                                selectedQuality = uiState.selectedQuality,
                                selectedAudioQuality = uiState.selectedAudioQuality,
                                selectedDownloadType = uiState.selectedDownloadType,
                                onQualityChange = onQualityChange,
                                onAudioQualityChange = onAudioQualityChange,
                                onDownloadClick = onDownloadClick,
                                onClearClick = { onUrlChange("") },
                                onResetDownload = onResetDownload,
                                errorMessage = uiState.errorMessage
                            )
                        }
                    }

                    DownloaderContentState.Error -> {
                        ErrorDisplay()
                    }

                    DownloaderContentState.Empty -> {
                        EmptyState()
                    }
                }
            }
        }
    }
}

private enum class DownloaderContentState {
    Empty,
    VideoInfo,
    Error
}

@Preview(showBackground = true)
@Composable
private fun DownloaderScreenContentPreview() {
    YoutubeDownloaderTheme {
        DownloaderScreenContent(
            uiState = DownloaderUiState(
                url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                videoInfo = VideoModel(
                    id = "dQw4w9WgXcQ",
                    title = "Never Gonna Give You Up",
                    uploader = "Rick Astley",
                    duration = 212,
                    thumbnail = null,
                    qualities = listOf(
                        VideoQualityInfo(
                            quality = VideoQuality.P360,
                            sizeBytes = 4_200_000
                        ),
                        VideoQualityInfo(
                            quality = VideoQuality.P720,
                            sizeBytes = 12_500_000
                        )
                    ),
                    audioQualities = listOf(
                        AudioQualityInfo(
                            quality = AudioQuality.K128,
                            sizeBytes = 2_000_000
                        ),
                        AudioQualityInfo(
                            quality = AudioQuality.K320,
                            sizeBytes = 5_000_000
                        )
                    )
                )
            ),
            onUrlChange = {},
            onQualityChange = {},
            onAudioQualityChange = {},
            onGetInfoClick = {},
            onDownloadClick = {},
            onHistoryClick = {},
            onResetDownload = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloaderScreenEmptyPreview() {
    YoutubeDownloaderTheme {
        DownloaderScreenContent(
            uiState = DownloaderUiState(),
            onUrlChange = {},
            onQualityChange = {},
            onAudioQualityChange = {},
            onGetInfoClick = {},
            onDownloadClick = {},
            onHistoryClick = {},
            onResetDownload = {}
        )
    }
}
