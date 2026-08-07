package com.mohannedabuhassira.youtubedownloader.core.network.client

import android.content.Context
import android.util.Log
import com.farimarwat.library.YoutubeDL
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkInitializer {
    fun init()
}

@Singleton
internal class NetworkInitializerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkInitializer {
    private val isInitialized = AtomicBoolean(false)

    override fun init() {
        if (isInitialized.getAndSet(true)) return

        try {
            YoutubeDL.init(
                appContext = context,
                withFfmpeg = true,
                withAria2c = true,
                onSuccess = {
                    Log.d("YoutubeDLInitializer", "Initialized successfully")
                },
                onError = { error ->
                    Log.e("YoutubeDLInitializer", "Init failed: $error")
                    isInitialized.set(false)
                }
            )
        } catch (e: Exception) {
            Log.e("YoutubeDLInitializer", "Failed to start YoutubeDL init", e)
            isInitialized.set(false)
        }
    }
}

