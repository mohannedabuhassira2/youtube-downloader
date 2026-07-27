package com.mohannedabuhassira.youtubedownloader.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Downloader : Screen

    @Serializable
    data object History : Screen
}
