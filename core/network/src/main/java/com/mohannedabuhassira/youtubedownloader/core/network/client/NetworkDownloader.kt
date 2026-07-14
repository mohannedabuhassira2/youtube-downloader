package com.mohannedabuhassira.youtubedownloader.core.network.client

import android.util.Log
import com.farimarwat.commons.VideoInfo
import com.farimarwat.library.YoutubeDL
import com.farimarwat.commons.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class NetworkDownloader @Inject constructor(
    private val youtubeDLEngine: YoutubeDL
) {
    suspend fun getInfo(
        url: String
    ): VideoInfo = withContext(Dispatchers.IO) {
        Log.d("NetworkDownloader", "getInfo start for: $url")
        val startTime = System.currentTimeMillis()
        suspendCancellableCoroutine { continuation ->
            youtubeDLEngine.getInfo(
                request = YoutubeDLRequest(url).apply {
                    addOption("--no-playlist")
                    addOption("--extractor-args", "youtube:player_client=android_vr,mweb")
                },
                onSuccess = { info ->
                    Log.d("NetworkDownloader", "getInfo end. Took ${System.currentTimeMillis() - startTime}ms")
                    continuation.resume(info)
                },
                onError = { e ->
                    Log.e("NetworkDownloader", "getInfo failed", e)
                    continuation.resumeWithException(e)
                }
            )
        }
    }

    fun streamDownload(
        config: NetworkDownloadConfig
    ): Flow<DownloadStatus> = callbackFlow {
        val request = YoutubeDLRequest(config.url).apply {
            val outputTemplate = "${config.destinationPath}/${config.title}.%(ext)s"
            addOption("-o", outputTemplate)
            addOption("--extractor-args", "youtube:player_client=android_vr")
            addOption("--no-playlist")
            addOption("--no-part")
            addOption("--downloader", "aria2c")

            if (config.isAudioOnly) {
                addOption("-x")
                addOption("--audio-format", config.audioFormat)
                addOption("--audio-quality", "0")
            } else {
                addOption("-S", "res,ext:mp4:m4a")
                addOption("-f", config.videoFormat)
                addOption("--merge-output-format", "mp4")
                addOption("--recode-video", "mp4")
            }
        }

        Log.d("NetworkDownloader", "Download started")

        var maxProgressObserved = 0f
        val job = youtubeDLEngine.download(
            request = request,
            progressCallBack = { progress, downloadedBytes, status ->
                if (progress != INIT_PROGRESS_INDICATOR) {
                    Log.d("NetworkDownloader", "Download progress: $progress - $downloadedBytes - $status")

                    // Only emit forward updates to keep UI animations smooth
                    if (progress > maxProgressObserved) {
                        maxProgressObserved = progress
                        trySend(DownloadStatus.Progress(maxProgressObserved))
                    }
                }
            },
            onEndProcess = {
                Log.d("NetworkDownloader", "Download done")
                trySend(DownloadStatus.Progress(100f))

                val extension = if (config.isAudioOnly) config.audioFormat else "mp4"
                val filePath = "${config.destinationPath}/${config.title}.$extension"

                Log.d("NetworkDownloader", "Final file path: $filePath")
                trySend(DownloadStatus.Completed(filePath))
                close()
            },
            onError = { e ->
                Log.e("NetworkDownloader", "Download failed: ${e.message}", e)
                trySend(DownloadStatus.Failed(e.message))
                close(e)
            }
        )

        awaitClose {
            job.cancel()
        }
    }

    private companion object {
        const val INIT_PROGRESS_INDICATOR = -1f
    }
}
