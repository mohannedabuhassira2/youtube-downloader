package com.mohannedabuhassira.youtubedownloader.core.network.factory

import android.content.Context
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import dagger.hilt.android.qualifiers.ApplicationContext

internal object YoutubeDLFactory {
    fun create(
        @ApplicationContext context: Context
    ): YoutubeDL {
        try {
            YoutubeDL.getInstance().init(context)
            FFmpeg.getInstance().init(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return YoutubeDL.getInstance()
    }
}