package com.mohannedabuhassira.youtubedownloader.features.history.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.mohannedabuhassira.youtubedownloader.core.designsystem.theme.YoutubeDownloaderTheme
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.*
import java.io.File
import java.util.Locale
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyItems by viewModel.historyItems.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refreshHistory()
    }

    HistoryScreenContent(
        historyItems = historyItems,
        onBack = onBack,
        onDelete = { viewModel.deleteItem(it) },
        onPlay = { playFile(context, it) },
        onShare = { shareFile(context, it) }
    )
}

@Composable
private fun HistoryItem(
    item: DownloadFile,
    onDelete: () -> Unit,
    onPlay: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.status == DownloadStatus.MISSING) 
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.thumbnail,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                if (item.thumbnail == null) {
                    Icon(
                        imageVector = if (item.type == DownloadFileType.AUDIO) Icons.Rounded.MusicNote else Icons.Rounded.Movie,
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
                        imageVector = if (item.type == DownloadFileType.AUDIO) Icons.Rounded.MusicNote else Icons.Rounded.Movie,
                        contentDescription = null,
                        modifier = Modifier.padding(3.dp).size(12.dp),
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = if (item.status == DownloadStatus.MISSING) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Surface(
                        color = if (item.status == DownloadStatus.MISSING) 
                            MaterialTheme.colorScheme.error.copy(alpha = 0.1f) 
                        else 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${item.format.name} • ${item.quality}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            color = if (item.status == DownloadStatus.MISSING) 
                                MaterialTheme.colorScheme.error 
                            else 
                                MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = " • ${formatSize(item.sizeBytes)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (item.status == DownloadStatus.MISSING) 
                            MaterialTheme.colorScheme.error.copy(alpha = 0.7f) 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (item.status == DownloadStatus.COMPLETED) {
                    IconButton(
                        onClick = onPlay,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = "Play",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Share,
                            contentDescription = "Share",
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
                        Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun playFile(context: Context, filePath: String) {
    try {
        val file = File(filePath)
        if (!file.exists()) return
        
        val uri = if (filePath.startsWith("content://")) {
            filePath.toUri()
        } else {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, if (filePath.contains("mp4") || filePath.contains("video")) "video/*" else "audio/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun shareFile(context: Context, filePath: String) {
    try {
        val uri = if (filePath.startsWith("content://")) {
            Uri.parse(filePath)
        } else {
            val file = File(filePath)
            if (!file.exists()) return
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (filePath.contains("mp4") || filePath.contains("video")) "video/*" else "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = 0
    while (size > 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    return String.format(Locale.US, "%.1f %s", size, units[unitIndex])
}

@Composable
private fun HistoryScreenContent(
    historyItems: List<DownloadFile>,
    onBack: () -> Unit,
    onDelete: (DownloadFile) -> Unit,
    onPlay: (String) -> Unit,
    onShare: (String) -> Unit
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                title = { Text("Download History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No downloads found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
                items(historyItems) { item ->
                    HistoryItem(
                        item = item,
                        onDelete = { onDelete(item) },
                        onPlay = { onPlay(item.filePath) },
                        onShare = { onShare(item.filePath) }
                    )
                }
            }
        }
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
                    thumbnail = "https://example.com/thumb.jpg",
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
                    thumbnail = "https://example.com/audio_thumb.jpg",
                    type = DownloadFileType.AUDIO,
                    format = DownloadFormat.MP3,
                    timestamp = System.currentTimeMillis() - 86400000,
                    sizeBytes = 5_200_000,
                    status = DownloadStatus.COMPLETED
                ),
                DownloadFile(
                    id = 4L,
                    title = "Missing File Example",
                    quality = "1080p",
                    filePath = "/missing/path.mp4",
                    thumbnail = null,
                    type = DownloadFileType.VIDEO,
                    format = DownloadFormat.MP4,
                    timestamp = System.currentTimeMillis() - 172800000,
                    sizeBytes = 150_000_000,
                    status = DownloadStatus.MISSING
                )
            ),
            onBack = {},
            onDelete = {},
            onPlay = {},
            onShare = {}
        )
    }
}
