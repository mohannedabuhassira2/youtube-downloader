package com.mohannedabuhassira.youtubedownloader.features.history.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mohannedabuhassira.youtubedownloader.core.common.util.formatSize
import com.mohannedabuhassira.youtubedownloader.core.designsystem.theme.YoutubeDownloaderTheme
import com.mohannedabuhassira.youtubedownloader.features.history.R
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.*

@Composable
internal fun HistoryItem(
    item: DownloadFile,
    onDelete: () -> Unit,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMissing = item.status == DownloadStatus.MISSING

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMissing) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HistoryItemThumbnail(
                thumbnail = item.thumbnail,
                type = item.type
            )

            HistoryItemMetadata(
                title = item.title,
                formatName = item.format.name,
                quality = item.quality,
                sizeBytes = item.sizeBytes,
                isMissing = isMissing,
                modifier = Modifier.weight(1f)
            )

            HistoryItemActions(
                status = item.status,
                onPlay = onPlay,
                onShare = onShare,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun HistoryItemThumbnail(
    thumbnail: String?,
    type: DownloadFileType,
    modifier: Modifier = Modifier
) {
    val icon = if (type == DownloadFileType.AUDIO) {
        Icons.Rounded.MusicNote
    } else {
        Icons.Rounded.Movie
    }

    Box(
        modifier = modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = thumbnail,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (thumbnail == null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
            )
        }

        // Overlay Icon for Type
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp),
            color = Color.Black.copy(alpha = 0.6f),
            shape = CircleShape
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(3.dp)
                    .size(12.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun HistoryItemMetadata(
    title: String,
    formatName: String,
    quality: String?,
    sizeBytes: Long,
    isMissing: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            color = if (isMissing) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Surface(
                color = if (isMissing) {
                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "$formatName • $quality",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 2.dp
                    ),
                    color = if (isMissing) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = " • ${formatSize(sizeBytes)}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isMissing) {
                    MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun HistoryItemActions(
    status: DownloadStatus,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        if (status == DownloadStatus.COMPLETED) {
            IconButton(
                onClick = onPlay,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(R.string.play_content_desc),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = onShare,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = stringResource(R.string.share_content_desc),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = stringResource(R.string.delete_content_desc),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryItemPreview() {
    YoutubeDownloaderTheme {
        HistoryItem(
            item = DownloadFile(
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
            onDelete = {},
            onPlay = {},
            onShare = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryItemMissingPreview() {
    YoutubeDownloaderTheme {
        HistoryItem(
            item = DownloadFile(
                id = 1L,
                title = "Missing File Example",
                quality = "1080p",
                filePath = "/path/to/video.mp4",
                thumbnail = null,
                type = DownloadFileType.VIDEO,
                format = DownloadFormat.MP4,
                timestamp = System.currentTimeMillis(),
                sizeBytes = 25_800_000,
                status = DownloadStatus.MISSING
            ),
            onDelete = {},
            onPlay = {},
            onShare = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
