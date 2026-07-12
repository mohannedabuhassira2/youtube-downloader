package com.mohannedabuhassira.youtubedownloader.core.network.client

import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDownloader @Inject constructor(
    private val youtubeDLEngine: YoutubeDL
) {
    suspend fun getInfo(url: String): VideoInfo = withContext(Dispatchers.IO) {
        Log.d("NetworkDownloader", "getInfo start for: $url")
        val startTime = System.currentTimeMillis()
        val request = YoutubeDLRequest(url).apply {
            addOption("--no-playlist")
            addOption("--flat-playlist")
            addOption("--no-check-certificate")
            addOption("--no-warnings")
            addOption("--ignore-config")
            addOption("--no-call-home")
            addOption("--socket-timeout", "10")
        }
        try {
            val info = youtubeDLEngine.getInfo(request)
            Log.d("NetworkDownloader", "getInfo end. Took ${System.currentTimeMillis() - startTime}ms")
            info
        } catch (e: Exception) {
            Log.e("NetworkDownloader", "getInfo failed", e)
            throw e
        }
    }

    fun streamDownload(
        config: NetworkDownloadConfig
    ): Flow<Float> = callbackFlow {
        val request = YoutubeDLRequest(config.url).apply {
            addOption("-o", "${config.destinationPath}/%(title)s.%(ext)s")

            if (config.isAudioOnly) {
                addOption("-x")
                addOption("--audio-format", config.audioFormat)
                addOption("--audio-quality", "0")
            } else {
                addOption("-f", "bestvideo+bestaudio/best")
            }
            
            addOption("--no-check-certificate")
            addOption("--no-call-home")
            addOption("--downloader", "aria2c")
        }

        try {
            youtubeDLEngine.execute(request) { progress, _, _ ->
                trySend(progress)
            }
            close()
        } catch (e: Exception) {
            close(e)
        }
    }
}
