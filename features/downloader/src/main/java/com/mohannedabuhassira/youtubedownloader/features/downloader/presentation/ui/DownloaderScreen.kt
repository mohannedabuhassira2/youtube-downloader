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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.mohannedabuhassira.youtubedownloader.core.designsystem.theme.YoutubeDownloaderTheme
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioQuality
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.AudioQualityInfo
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.QualityInfo
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoModel
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.VideoQuality
import java.io.File
import java.util.Locale

@Composable
fun DownloaderScreen(
    onNavigateToHistory: () -> Unit,
    viewModel: DownloaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DownloaderScreenContent(
        uiState = uiState,
        onUrlChange = viewModel::onUrlChange,
        onQualityChange = viewModel::onQualityChange,
        onAudioQualityChange = viewModel::onAudioQualityChange,
        onGetInfoClick = viewModel::fetchVideoInfo,
        onDownloadClick = viewModel::startDownload,
        onHistoryClick = onNavigateToHistory,
        onResetDownload = viewModel::resetDownloadState
    )
}

@Composable
internal fun DownloaderScreenContent(
    uiState: DownloaderUiState,
    onUrlChange: (String) -> Unit,
    onQualityChange: (VideoQuality) -> Unit,
    onAudioQualityChange: (AudioQuality) -> Unit,
    onGetInfoClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onResetDownload: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

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
                        text = "YouTube Downloader",
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
                        contentDescription = "History",
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
                                selectedAudioQuality = uiState.selectedAudioQuality,
                                selectedDownloadType = uiState.selectedDownloadType,
                                onQualityChange = onQualityChange,
                                onAudioQualityChange = onAudioQualityChange,
                                onDownloadClick = onDownloadClick,
                                onClearClick = { onUrlChange("") },
                                onResetDownload = onResetDownload
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
    selectedQuality: VideoQuality?,
    selectedAudioQuality: AudioQuality?,
    selectedDownloadType: DownloadType?,
    onQualityChange: (VideoQuality) -> Unit,
    onAudioQualityChange: (AudioQuality) -> Unit,
    onDownloadClick: () -> Unit,
    onClearClick: () -> Unit,
    onResetDownload: () -> Unit
) {
    var hasStartedDownloading by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = (progress ?: 0f) / 100f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "ProgressAnimation"
    )

    LaunchedEffect(isDownloading, progress) {
        if (isDownloading) {
            hasStartedDownloading = true
        } else if (progress == null) {
            hasStartedDownloading = false
        }
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
                            filePath = downloadedFilePath
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

                if (!downloadedFilePath.isNullOrBlank()) {
                    IconButton(
                        onClick = onResetDownload,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back to selection",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onClearClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Clear",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
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
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = videoInfo.uploader ?: "Unknown Uploader",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!isDownloading && downloadedFilePath.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Video Quality",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(videoInfo.qualities) { qualityInfo ->
                            FormatChip(
                                label = qualityInfo.quality.label,
                                sizeLabel = qualityInfo.sizeBytes?.let { formatSize(it) },
                                selected = selectedDownloadType == DownloadType.VIDEO && selectedQuality == qualityInfo.quality,
                                onClick = { onQualityChange(qualityInfo.quality) },
                                modifier = Modifier.width(90.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Audio Quality",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(videoInfo.audioQualities) { qualityInfo ->
                            FormatChip(
                                label = qualityInfo.quality.label,
                                sizeLabel = qualityInfo.sizeBytes?.let { formatSize(it) },
                                selected = selectedDownloadType == DownloadType.AUDIO && selectedAudioQuality == qualityInfo.quality,
                                onClick = { onAudioQualityChange(qualityInfo.quality) },
                                modifier = Modifier.width(90.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))

                if (isDownloading || showProgressPerimeter) {
                    DownloadProgressSection(progress = (animatedProgress * 100f), color = progressColor)
                } else if (downloadedFilePath.isNullOrBlank()) {
                    DownloadActionButton(
                        text = when (selectedDownloadType) {
                            DownloadType.VIDEO -> "Download Video"
                            DownloadType.AUDIO -> "Download Audio"
                            else -> "Download"
                        },
                        icon = when (selectedDownloadType) {
                            DownloadType.VIDEO -> Icons.Rounded.Movie
                            DownloadType.AUDIO -> Icons.Rounded.MusicNote
                            else -> Icons.Rounded.Download
                        },
                        onClick = onDownloadClick,
                        enabled = selectedDownloadType != null,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun FormatChip(
    label: String,
    sizeLabel: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        label = "FormatChipBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "FormatChipContent"
    )

    Surface(
        onClick = onClick,
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(12.dp),
        border = if (!selected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
        modifier = modifier.height(52.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            if (sizeLabel != null) {
                Text(
                    text = sizeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayer(filePath: String) {
    val context = LocalContext.current

    val exoPlayer = remember(filePath) {
        val file = File(filePath)
        val uri = if (filePath.startsWith("content://")) {
            Uri.parse(filePath)
        } else {
            // Using FileProvider even internally is safer for Scoped Storage compatibility
            try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                // If FileProvider fails, fallback to raw file URI (works in private storage)
                Uri.fromFile(file)
            }
        }
        
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
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
private fun DownloadActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
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

private fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "Unknown"
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = 0
    while (size > 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    return String.format(Locale.US, "%.1f %s", size, units[unitIndex])
}

@Preview(showBackground = true)
@Composable
private fun DownloaderScreenPreview() {
    YoutubeDownloaderTheme {
        DownloaderScreenContent(
            uiState = DownloaderUiState(
                url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                videoInfo = VideoModel(
                    id = "dQw4w9WgXcQ",
                    title = "Never Gonna Give You Up",
                    uploader = "Rick Astley",
                    duration = 212,
                    thumbnail = "https://example.com/thumb.jpg",
                    qualities = listOf(
                        QualityInfo(VideoQuality.P360, 4_200_000),
                        QualityInfo(VideoQuality.P720, 12_500_000),
                        QualityInfo(VideoQuality.P1080, 25_800_000)
                    ),
                    audioQualities = listOf(
                        AudioQualityInfo(AudioQuality.K128, 2_000_000),
                        AudioQualityInfo(AudioQuality.K192, 3_000_000),
                        AudioQualityInfo(AudioQuality.K320, 5_000_000)
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
private fun VideoInfoCardPreview() {
    YoutubeDownloaderTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            VideoInfoCard(
                videoInfo = VideoModel(
                    id = "dQw4w9WgXcQ",
                    title = "Sample Video with a very long title that might span multiple lines to test the UI layout and ensure everything is correctly aligned",
                    uploader = "Creator Name",
                    duration = 345,
                    thumbnail = "https://example.com/thumb.jpg",
                    qualities = listOf(
                        QualityInfo(VideoQuality.P360, 5_000_000),
                        QualityInfo(VideoQuality.P720, 15_000_000),
                        QualityInfo(VideoQuality.P1080, 30_000_000)
                    ),
                    audioQualities = listOf(
                        AudioQualityInfo(AudioQuality.K128, 4_000_000),
                        AudioQualityInfo(AudioQuality.K320, 8_000_000)
                    )
                ),
                isDownloading = false,
                progress = null,
                downloadedFilePath = null,
                selectedQuality = null,
                selectedAudioQuality = null,
                selectedDownloadType = null,
                onQualityChange = {},
                onAudioQualityChange = {},
                onDownloadClick = {},
                onClearClick = {},
                onResetDownload = {}
            )
        }
    }
}
