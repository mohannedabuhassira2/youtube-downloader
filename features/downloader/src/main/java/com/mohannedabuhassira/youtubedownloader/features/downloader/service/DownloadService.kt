package com.mohannedabuhassira.youtubedownloader.features.downloader.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.mohannedabuhassira.youtubedownloader.core.network.client.DownloadStatus
import com.mohannedabuhassira.youtubedownloader.features.downloader.R
import com.mohannedabuhassira.youtubedownloader.features.downloader.data.ApiResult
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.model.DownloadOptions
import com.mohannedabuhassira.youtubedownloader.features.downloader.domain.repository.DownloaderRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
internal class DownloadService : LifecycleService() {

    @Inject
    lateinit var repository: DownloaderRepository

    @Inject
    lateinit var progressManager: DownloadProgressManager

    private var downloadJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val optionsJson = intent?.getStringExtra(EXTRA_DOWNLOAD_OPTIONS)
        if (optionsJson != null) {
            val options = Json.decodeFromString<DownloadOptions>(optionsJson)
            startDownload(options)
        }

        return START_NOT_STICKY
    }

    private fun startDownload(options: DownloadOptions) {
        downloadJob?.cancel()

        val initialNotification = createInitialNotification(options)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, initialNotification)
        }

        downloadJob = repository.download(options)
            .onEach { result ->
                progressManager.updateProgress(result)
                updateNotification(result, options)
            }
            .launchIn(lifecycleScope)
    }

    private fun updateNotification(
        result: ApiResult<DownloadStatus>,
        options: DownloadOptions
    ) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        when (result) {
            is ApiResult.Loading -> {
                val progress = result.progress?.toInt() ?: 0
                val progressNotification = createProgressNotification(options, progress)
                notificationManager.notify(NOTIFICATION_ID, progressNotification)
            }

            is ApiResult.Success -> {
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                notificationManager.cancel(NOTIFICATION_ID)
                stopSelf()
            }

            is ApiResult.Error -> {
                val errorNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentTitle(getString(R.string.download_failed_notification_title))
                    .setContentText(result.message ?: getString(R.string.unknown_title))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .build()

                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                notificationManager.notify(NOTIFICATION_ID + 1, errorNotification)
                stopSelf()
            }
        }
    }

    private fun createInitialNotification(options: DownloadOptions) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(getString(R.string.downloading_notification_title, options.title ?: getString(R.string.unknown_title)))
            .setContentText("0%")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setProgress(100, 0, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

    private fun createProgressNotification(options: DownloadOptions, progress: Int) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(getString(R.string.downloading_notification_title, options.title ?: getString(R.string.unknown_title)))
            .setContentText("$progress%")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.download_channel_name)
            val descriptionText = getString(R.string.download_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(true)
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "download_channel"
        private const val EXTRA_DOWNLOAD_OPTIONS = "extra_download_options"

        fun start(context: Context, options: DownloadOptions) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra(EXTRA_DOWNLOAD_OPTIONS, Json.encodeToString(options))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
