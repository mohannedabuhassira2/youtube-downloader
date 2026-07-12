package com.mohannedabuhassira.youtubedownloader.app

import android.app.Application
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YoutubeDownloaderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            YoutubeDL.getInstance().init(this)
        } catch (e: YoutubeDLException) {
            Log.e("YoutubeDownloaderApp", "Failed to initialize YoutubeDL", e)
        }
    }
}
