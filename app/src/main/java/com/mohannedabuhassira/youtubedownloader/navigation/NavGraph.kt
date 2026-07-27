package com.mohannedabuhassira.youtubedownloader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mohannedabuhassira.youtubedownloader.features.downloader.presentation.ui.DownloaderScreen
import com.mohannedabuhassira.youtubedownloader.features.history.presentation.ui.HistoryScreen

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
        composable<Screen.Downloader> {
            DownloaderScreen(
                onNavigateToHistory = {
                    navController.navigate(Screen.History)
                }
            )
        }

        composable<Screen.History> {
            HistoryScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
