package com.mohannedabuhassira.youtubedownloader.core.network.factory

import android.content.Context
import android.util.Log
import com.farimarwat.library.YoutubeDL
import dagger.hilt.android.qualifiers.ApplicationContext

internal object YoutubeDLFactory {
    fun create(
        @ApplicationContext context: Context
    ): YoutubeDL {
        YoutubeDL.init(
            appContext = context,
            withFfmpeg = true,
            withAria2c = true,
            onSuccess = {
                Log.d("YoutubeDLFactory", "YoutubeDL-Boom initialized successfully")
            },
            onError = { error ->
                Log.e("YoutubeDLFactory", "YoutubeDL-Boom initialization failed: $error")
            }
        )

        return YoutubeDL.getInstance()
    }
}