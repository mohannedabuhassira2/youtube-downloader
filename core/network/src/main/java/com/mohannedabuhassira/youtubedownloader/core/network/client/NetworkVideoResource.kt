package com.mohannedabuhassira.youtubedownloader.core.network.client

data class NetworkVideoResource(
    val id: String?,
    val title: String?,
    val thumbnail: String?,
    val durationSeconds: Int,
    val uploader: String?,
    val formats: List<NetworkFormatResource> = emptyList()
)

data class NetworkFormatResource(
    val formatId: String?,
    val formatNote: String?,
    val ext: String?,
    val fileSize: Long,
    val vcodec: String?,
    val acodec: String?,
    val abr: Double
)
