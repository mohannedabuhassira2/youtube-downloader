package com.mohannedabuhassira.youtubedownloader.core.network.client

data class NetworkDownloadConfig(
    val url: String,
    val title: String?,
    val destinationPath: String,
    val isAudioOnly: Boolean,
    val audioFormat: String = "mp3"
)
