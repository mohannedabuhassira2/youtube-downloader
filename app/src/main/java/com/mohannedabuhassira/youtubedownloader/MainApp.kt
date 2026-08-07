package com.mohannedabuhassira.youtubedownloader

import android.app.Application
import com.mohannedabuhassira.youtubedownloader.core.network.client.NetworkInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApp : Application() {
    @Inject
    lateinit var networkInitializer: NetworkInitializer

    override fun onCreate() {
        super.onCreate()
        networkInitializer.init()
    }
}
