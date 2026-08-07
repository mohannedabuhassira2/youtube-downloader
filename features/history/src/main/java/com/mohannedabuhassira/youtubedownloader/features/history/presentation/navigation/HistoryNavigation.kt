package com.mohannedabuhassira.youtubedownloader.features.history.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mohannedabuhassira.youtubedownloader.features.history.presentation.ui.HistoryScreen
import com.mohannedabuhassira.youtubedownloader.navigation.Screen

fun NavGraphBuilder.historyScreen(
    onBack: () -> Unit
) {
    composable<Screen.History> {
        HistoryScreen(
            onBack = onBack
        )
    }
}
