package com.mohannedabuhassira.youtubedownloader.core.network.client

import android.util.Log
import com.farimarwat.commons.YoutubeDLRequest
import com.farimarwat.library.YoutubeDL
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

interface NetworkDownloader {
    suspend fun getInfo(
        url: String,
        options: List<Pair<String, String?>> = emptyList()
    ): NetworkVideoResource

    fun download(
        url: String,
        options: List<Pair<String, String?>> = emptyList(),
        resultFilePath: String
    ): Flow<DownloadStatus>
}

@Singleton
internal class NetworkDownloaderImpl @Inject constructor(
    private val youtubeDLEngine: YoutubeDL
) : NetworkDownloader {
    override suspend fun getInfo(
        url: String,
        options: List<Pair<String, String?>>
    ): NetworkVideoResource = withContext(Dispatchers.IO) {
        Log.d("NetworkDownloader", "getInfo start for: $url")

        val startTime = System.currentTimeMillis()
        suspendCancellableCoroutine { continuation ->
            val request = YoutubeDLRequest(url).apply {
                options.forEach { (option, value) ->
                    if (value != null) {
                        addOption(option, value)
                    } else {
                        addOption(option)
                    }
                }
            }

            youtubeDLEngine.getInfo(
                request = request,
                onSuccess = { info ->
                    Log.d("NetworkDownloader", "getInfo end. Took ${System.currentTimeMillis() - startTime}ms")
                    val resource = NetworkVideoResource(
                        id = info.id,
                        title = info.title,
                        thumbnail = info.thumbnail,
                        durationSeconds = info.duration,
                        uploader = info.uploader,
                        formats = info.formats?.map { f ->
                            NetworkFormatResource(
                                formatId = f.formatId,
                                formatNote = f.formatNote,
                                ext = f.ext,
                                fileSize = f.fileSize,
                                vcodec = f.vcodec,
                                acodec = f.acodec,
                                abr = f.abr.toDouble()
                            )
                        } ?: emptyList()
                    )
                    continuation.resume(resource)
                },
                onError = { e ->
                    Log.e("NetworkDownloader", "getInfo failed", e)
                    continuation.resumeWithException(e)
                }
            )
        }
    }

    override fun download(
        url: String,
        options: List<Pair<String, String?>>,
        resultFilePath: String
    ): Flow<DownloadStatus> = callbackFlow {
        val request = YoutubeDLRequest(url).apply {
            options.forEach { (option, value) ->
                if (value != null) {
                    addOption(option, value)
                } else {
                    addOption(option)
                }
            }
        }

        Log.d("NetworkDownloader", "Download started: $url")
        var maxProgressObserved = 0f
        val job = youtubeDLEngine.download(
            request = request,
            progressCallBack = { progress, downloadedBytes, _ ->
                if (progress != INIT_PROGRESS_INDICATOR) {
                    if (progress > maxProgressObserved) {
                        maxProgressObserved = progress
                        trySend(DownloadStatus.Progress(maxProgressObserved, downloadedBytes))
                    }
                }
            },
            onEndProcess = {
                trySend(DownloadStatus.Progress(100f))
                trySend(DownloadStatus.Completed(resultFilePath))
                close()
            },
            onError = { e ->
                trySend(DownloadStatus.Failed(e.message))
                close(e)
            }
        )

        awaitClose { job.cancel() }
    }

    private companion object {
        const val INIT_PROGRESS_INDICATOR = -1f
    }
}

