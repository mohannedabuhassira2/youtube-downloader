package com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.ui

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect.Companion.dashPathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.mohannedabuhassira.youtubedownloader.core.designsystem.theme.YoutubeDownloaderTheme
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQuality
import java.io.File
import java.util.Locale

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
        onQualityChange = viewModel::onQualityChange,
        onGetInfoClick = viewModel::fetchVideoInfo,
        onDownloadClick = viewModel::startDownload
    )
}

@Composable
internal fun DownloaderScreenContent(
    uiState: DownloaderUiState,
    onUrlChange: (String) -> Unit,
    onQualityChange: (VideoQuality) -> Unit,
    onGetInfoClick: () -> Unit,
    onDownloadClick: (DownloadType) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = uiState.url,
                        onValueChange = onUrlChange,
                        placeholder = { Text("Paste YouTube URL here...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            Row {
                                if (uiState.url.isNotEmpty()) {
                                    IconButton(onClick = { onUrlChange("") }) {
                                        Icon(Icons.Rounded.Clear, "Clear")
                                    }
                                }
                                IconButton(onClick = {
                                    clipboardManager.getText()?.text?.let { onUrlChange(it) }
                                }) {
                                    Icon(Icons.Rounded.ContentPaste, "Paste")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onGetInfoClick,
                        enabled = uiState.url.isNotEmpty() && !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        AnimatedContent(
                            targetState = uiState.isLoading,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "ButtonContent"
                        ) { loading ->
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Search, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analyze Video")
                                }
                            }
                        }
                    }
                }
            }

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
                                onQualityChange = onQualityChange,
                                onDownloadClick = onDownloadClick,
                                onClearClick = { onUrlChange("") }
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
    Empty, VideoInfo, Error
}

@Composable
private fun VideoInfoCard(
    videoInfo: VideoModel,
    isDownloading: Boolean,
    progress: Float?,
    downloadedFilePath: String?,
    selectedQuality: VideoQuality,
    onQualityChange: (VideoQuality) -> Unit,
    onDownloadClick: (DownloadType) -> Unit,
    onClearClick: () -> Unit
) {
    var hasStartedDownloading by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = (progress ?: 0f) / 100f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "ProgressAnimation"
    )

    LaunchedEffect(isDownloading) {
        if (isDownloading) hasStartedDownloading = true
    }

    val showProgressPerimeter = hasStartedDownloading && animatedProgress < 0.99f

    val progressColor by animateColorAsState(
        targetValue = when {
            animatedProgress < 0.3f -> MaterialTheme.colorScheme.primary
            animatedProgress < 0.7f -> Color(0xFFFF9800)
            else -> Color(0xFF4CAF50)
        },
        animationSpec = tween(durationMillis = 500),
        label = "ProgressColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .then(
                if (showProgressPerimeter) {
                    Modifier.drawWithCache {
                        val path = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    rect = Rect(
                                        offset = Offset.Zero,
                                        size = size
                                    ),
                                    cornerRadius = CornerRadius(24.dp.toPx())
                                )
                            )
                        }
                        val pathMeasure = PathMeasure()
                        pathMeasure.setPath(path, false)
                        val totalLength = pathMeasure.length

                        onDrawWithContent {
                            drawContent()
                            drawPath(
                                path = path,
                                color = progressColor,
                                style = Stroke(
                                    width = 4.dp.toPx(),
                                    pathEffect = dashPathEffect(
                                        intervals = floatArrayOf(totalLength * animatedProgress, totalLength),
                                        phase = 0f
                                    ),
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                    }
                } else {
                    Modifier.animateContentSize()
                }
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                AnimatedContent(
                    targetState = !downloadedFilePath.isNullOrBlank(),
                    transitionSpec = {
                        fadeIn(tween(800)) togetherWith fadeOut(tween(800))
                    },
                    label = "MediaContent"
                ) { isDownloaded ->
                    if (isDownloaded && !downloadedFilePath.isNullOrBlank()) {
                        VideoPlayer(
                            filePath = downloadedFilePath,
                            onDismiss = onClearClick
                        )
                    } else {
                        Box {
                            AsyncImage(
                                model = videoInfo.thumbnail,
                                contentDescription = "Thumbnail",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = formatDuration(videoInfo.duration),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = videoInfo.title ?: "Unknown Title",
                        style = MaterialTheme.typography.titleLarge.copy(
                            lineHeight = 28.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onClearClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = videoInfo.uploader ?: "Unknown Uploader",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (!isDownloading && downloadedFilePath.isNullOrBlank()) {
                    Text(
                        text = "Download Quality",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VideoQuality.entries.forEach { quality ->
                            QualityChip(
                                quality = quality,
                                selected = selectedQuality == quality,
                                onClick = { onQualityChange(quality) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                Spacer(modifier = Modifier.height(20.dp))

                if (isDownloading || showProgressPerimeter) {
                    DownloadProgressSection(progress = (animatedProgress * 100f), color = progressColor)
                } else if (!downloadedFilePath.isNullOrBlank()) {
                    DownloadSuccessSection()
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DownloadActionButton(
                            text = "Audio",
                            icon = Icons.Rounded.MusicNote,
                            onClick = { onDownloadClick(DownloadType.AUDIO) },
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        DownloadActionButton(
                            text = "Video",
                            icon = Icons.Rounded.Movie,
                            onClick = { onDownloadClick(DownloadType.VIDEO) },
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityChip(
    quality: VideoQuality,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        label = "QualityChipBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "QualityChipContent"
    )

    Surface(
        onClick = onClick,
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(12.dp),
        border = if (!selected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = quality.label,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayer(filePath: String, onDismiss: () -> Unit) {
    val context = LocalContext.current

    val exoPlayer = remember(filePath) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(File(filePath)))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setShowSubtitleButton(false)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun DownloadSuccessSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Ready to Play!",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF4CAF50)
        )
    }
}

@Composable
private fun DownloadActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun DownloadProgressSection(progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (progress >= 99.9f) "Finalizing..." else "Downloading...",
                style = MaterialTheme.typography.titleSmall
            )
            Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text = "${progress.toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = color
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Please keep the app open",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "EmptyState")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Pulse"
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Ready to download?",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Enter a YouTube link above to start",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ErrorDisplay() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Failed to display the given URL. Please ensure you use a valid YouTube URL.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.US, "%d:%02d", m, s)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
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
                    thumbnail = "https://example.com/thumb.jpg"
                )
            ),
            onUrlChange = {},
            onQualityChange = {},
            onGetInfoClick = {},
            onDownloadClick = {}
        )
    }
}
