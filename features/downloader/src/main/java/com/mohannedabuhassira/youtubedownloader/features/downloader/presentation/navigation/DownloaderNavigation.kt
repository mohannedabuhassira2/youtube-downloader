package com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.ui.DownloaderScreen
import com.mohannedabuhassira.youtubedownloader.navigation.Screen

fun NavGraphBuilder.downloaderScreen(
    onNavigateToHistory: () -> Unit
) {
    composable<Screen.Downloader> {
        DownloaderScreen(
            onNavigateToHistory = onNavigateToHistory
        )
    }
}
