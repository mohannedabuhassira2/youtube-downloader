package com.mohannedabuhassira.youtubedownloader.features.downloader.data.remote

import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadOptions
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadType
import javax.inject.Inject

internal class YoutubeCliRequestBuilder @Inject constructor() {
    fun buildInfoOptions(): List<Pair<String, String?>> = listOf(
        "--no-playlist" to null,
        "--extractor-args" to "youtube:player_client=android_vr,mweb,web,ios"
    )

    fun buildDownloadOptions(
        options: DownloadOptions,
        outputTemplate: String
    ): List<Pair<String, String?>> = buildList {
        add("-o" to outputTemplate)
        add("--extractor-args" to "youtube:player_client=android_vr,mweb,web,ios")
        add("--no-playlist" to null)
        add("--no-part" to null)
        add("--downloader" to "aria2c")

        if (options.type == DownloadType.AUDIO) {
            add("-x" to null)
            add("--audio-format" to "mp3")
            add("--audio-quality" to options.audioQuality.kbps.toString())
        } else {
            val videoFormat = "bestvideo[format_note*='${options.videoQuality.label}']+bestaudio/best"
            add("-S" to "res,ext:mp4:m4a")
            add("-f" to videoFormat)
            add("--merge-output-format" to "mp4")
            add("--recode-video" to "mp4")
        }
    }
}
