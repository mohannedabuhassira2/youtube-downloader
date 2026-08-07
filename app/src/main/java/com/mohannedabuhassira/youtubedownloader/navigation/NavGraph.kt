package com.mohannedabuhassira.youtubedownloader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.navigation.downloaderScreen
import com.mohannedabuhassira.youtubedownloader.features.history.presentation.navigation.historyScreen

@Composable
fun YoutubeDownloaderNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Downloader,
        enterTransition = { slideInToLeft() },
        exitTransition = { slideOutToLeft() },
        popEnterTransition = { slideInToRight() },
        popExitTransition = { slideOutToRight() }
    ) {
        downloaderScreen(
            onNavigateToHistory = {
                navController.navigate(Screen.History)
            }
        )

        historyScreen(
            onBack = {
                navController.popBackStack()
            }
        )
    }
}
