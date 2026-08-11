package com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.components.videoinfo

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mohannedabuhassira.youtubedownloader.core.common.util.formatDuration
import com.mohannedabuhassira.youtubedownloader.core.common.util.formatSize
import com.mohannedabuhassira.youtubedownloader.core.designsystem.theme.YoutubeDownloaderTheme
import com.mohannedabuhassira.youtubedownloader.features.downloader.R
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.*

@Composable
internal fun VideoInfoCard(
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
    onResetDownload: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    var hasStartedDownloading by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = (progress ?: 0f) / 100f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        label = "ProgressAnimation"
    )

    LaunchedEffect(
        isDownloading,
        progress
    ) {
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
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .progressBorder(
                show = showProgressPerimeter,
                progress = animatedProgress,
                color = progressColor
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column {
            VideoCardHeader(
                videoInfo = videoInfo,
                downloadedFilePath = downloadedFilePath,
                onResetDownload = onResetDownload,
                onClearClick = onClearClick
            )

            Column(modifier = Modifier.padding(20.dp)) {
                VideoCardMetadata(
                    title = videoInfo.title,
                    uploader = videoInfo.uploader
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (!isDownloading && downloadedFilePath.isNullOrBlank()) {
                    QualitySelectors(
                        videoInfo = videoInfo,
                        selectedDownloadType = selectedDownloadType,
                        selectedQuality = selectedQuality,
                        selectedAudioQuality = selectedAudioQuality,
                        onQualityChange = onQualityChange,
                        onAudioQualityChange = onAudioQualityChange
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))

                VideoCardFooter(
                    isDownloading = isDownloading,
                    showProgressPerimeter = showProgressPerimeter,
                    animatedProgress = animatedProgress,
                    progressColor = progressColor,
                    downloadedFilePath = downloadedFilePath,
                    selectedDownloadType = selectedDownloadType,
                    onDownloadClick = onDownloadClick
                )
            }
        }
    }
}

@Composable
private fun VideoCardHeader(
    videoInfo: VideoModel,
    downloadedFilePath: String?,
    onResetDownload: () -> Unit,
    onClearClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(
                RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                )
            )
    ) {
        AnimatedContent(
            targetState = !downloadedFilePath.isNullOrBlank(),
            transitionSpec = {
                fadeIn(tween(800)) togetherWith fadeOut(tween(800))
            },
            label = "MediaContent"
        ) { isDownloaded ->
            if (isDownloaded && !downloadedFilePath.isNullOrBlank()) {
                VideoPlayer(filePath = downloadedFilePath)
            } else {
                ThumbnailOverlay(videoInfo = videoInfo)
            }
        }

        if (!downloadedFilePath.isNullOrBlank()) {
            HeaderIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back_to_selection_content_desc),
                onClick = onResetDownload,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }

        HeaderIconButton(
            icon = Icons.Rounded.Clear,
            contentDescription = stringResource(R.string.clear_content_desc),
            onClick = onClearClick,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun ThumbnailOverlay(
    videoInfo: VideoModel
) {
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
                modifier = Modifier.padding(
                    horizontal = 6.dp,
                    vertical = 2.dp
                )
            )
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(12.dp)
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .size(36.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun VideoCardMetadata(
    title: String?,
    uploader: String?
) {
    Text(
        text = title ?: stringResource(R.string.unknown_title),
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(4.dp))
    
    Text(
        text = uploader ?: stringResource(R.string.unknown_uploader),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun QualitySelectors(
    videoInfo: VideoModel,
    selectedDownloadType: DownloadType?,
    selectedQuality: VideoQuality?,
    selectedAudioQuality: AudioQuality?,
    onQualityChange: (VideoQuality) -> Unit,
    onAudioQualityChange: (AudioQuality) -> Unit
) {
    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.video_quality_label),
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
        text = stringResource(R.string.audio_quality_label),
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

@Composable
private fun VideoCardFooter(
    isDownloading: Boolean,
    showProgressPerimeter: Boolean,
    animatedProgress: Float,
    progressColor: Color,
    downloadedFilePath: String?,
    selectedDownloadType: DownloadType?,
    onDownloadClick: () -> Unit
) {
    if (isDownloading || showProgressPerimeter) {
        DownloadProgressSection(
            progress = (animatedProgress * 100f),
            color = progressColor
        )
    } else if (downloadedFilePath.isNullOrBlank()) {
        DownloadActionButton(
            text = when (selectedDownloadType) {
                DownloadType.VIDEO -> stringResource(R.string.download_video_button)
                DownloadType.AUDIO -> stringResource(R.string.download_audio_button)
                else -> stringResource(R.string.download_button)
            },
            icon = when (selectedDownloadType) {
                DownloadType.VIDEO -> Icons.Rounded.Movie
                DownloadType.AUDIO -> Icons.Rounded.MusicNote
                else -> Icons.Rounded.Download
            },
            onClick = onDownloadClick,
            enabled = selectedDownloadType != null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun Modifier.progressBorder(
    show: Boolean,
    progress: Float,
    color: Color
): Modifier = if (show) {
    this.drawWithCache {
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
        pathMeasure.setPath(
            path = path,
            forceClosed = false
        )
        val totalLength = pathMeasure.length

        onDrawWithContent {
            drawContent()
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = 4.dp.toPx(),
                    pathEffect = dashPathEffect(
                        intervals = floatArrayOf(
                            totalLength * progress,
                            totalLength
                        ),
                        phase = 0f
                    ),
                    cap = StrokeCap.Round
                )
            )
        }
    }
} else {
    this.animateContentSize()
}

@Preview(showBackground = true)
@Composable
private fun VideoInfoCardPreview() {
    YoutubeDownloaderTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            VideoInfoCard(
                videoInfo = VideoModel(
                    id = "dQw4w9WgXcQ",
                    title = "Sample Video with a very long title that might span multiple lines",
                    uploader = "Creator Name",
                    duration = 345,
                    thumbnail = null,
                    qualities = listOf(
                        VideoQualityInfo(
                            quality = VideoQuality.P360,
                            sizeBytes = 5_000_000
                        ),
                        VideoQualityInfo(
                            quality = VideoQuality.P720,
                            sizeBytes = 15_000_000
                        )
                    ),
                    audioQualities = listOf(
                        AudioQualityInfo(
                            quality = AudioQuality.K128,
                            sizeBytes = 4_000_000
                        ),
                        AudioQualityInfo(
                            quality = AudioQuality.K320,
                            sizeBytes = 8_000_000
                        )
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
