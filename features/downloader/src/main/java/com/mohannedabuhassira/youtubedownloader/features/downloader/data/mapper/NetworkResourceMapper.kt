package com.mohannedabuhassira.youtubedownloader.features.downloader.data.mapper

import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkFormatResource
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkVideoResource
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.*

internal fun NetworkVideoResource.toVideoModel(): VideoModel {
    val bestAudioSize = formats.findBestAudioSize()

    return VideoModel(
        id = id,
        title = title,
        thumbnail = thumbnail,
        duration = durationSeconds,
        uploader = uploader,
        qualities = mapVideoQualities(formats, bestAudioSize),
        audioQualities = mapAudioQualities(durationSeconds)
    )
}

private fun List<NetworkFormatResource>.findBestAudioSize(): Long {
    return filter { it.vcodec == "none" && it.acodec != "none" }
        .maxByOrNull { it.abr }
        ?.fileSize ?: 0L
}

private fun mapVideoQualities(
    formats: List<NetworkFormatResource>,
    bestAudioSize: Long
): List<VideoQualityInfo> {
    return VideoQuality.entries.mapNotNull { quality ->
        val format = formats.find { it.formatNote?.contains(quality.label) == true }

        format?.let {
            val totalSize = it.fileSize + bestAudioSize
            VideoQualityInfo(quality, totalSize.takeIf { size -> size > 0L })
        }
    }
}

private fun mapAudioQualities(durationSeconds: Int): List<AudioQualityInfo> {
    return AudioQuality.entries.map { audioQuality ->
        val estimatedSizeBytes = if (durationSeconds > 0) {
            (audioQuality.kbps * 1000L * durationSeconds) / 8
        } else null

        AudioQualityInfo(audioQuality, estimatedSizeBytes)
    }
}
