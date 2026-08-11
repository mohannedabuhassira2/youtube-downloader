package com.mohannedabuhassira.youtubedownloader.features.downloader.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private var downloadJob: Job? = null

    private val notificationBuilder: NotificationCompat.Builder by lazy {
        createBaseBuilder()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        super.onStartCommand(intent, flags, startId)

        val optionsJson = intent?.getStringExtra(EXTRA_DOWNLOAD_OPTIONS)
        if (optionsJson != null) {
            val options = Json.decodeFromString<DownloadOptions>(optionsJson)
            startDownload(options)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        downloadJob?.cancel()
        super.onDestroy()
    }

    private fun startDownload(options: DownloadOptions) {
        downloadJob?.cancel()

        val initialNotification = buildProgressNotification(options, progress = 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                initialNotification
            )
        }

        downloadJob = repository.download(options)
            .onEach { result ->
                progressManager.updateProgress(result)
                updateNotificationState(result, options)
            }
            .launchIn(lifecycleScope)
    }

    private fun updateNotificationState(
        result: ApiResult<DownloadStatus>,
        options: DownloadOptions
    ) {
        val videoTitle = options.title ?: getString(R.string.unknown_title)

        when (result) {
            is ApiResult.Loading -> {
                val progress = result.progress?.toInt() ?: 0
                notificationManager.notify(
                    NOTIFICATION_ID,
                    buildProgressNotification(options, progress)
                )
            }

            is ApiResult.Success -> {
                val successNotification = notificationBuilder
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle(getString(R.string.download_complete_notification_title))
                    .setContentText(videoTitle)
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .build()

                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                notificationManager.cancel(NOTIFICATION_ID)
                notificationManager.notify(SUCCESS_NOTIFICATION_ID, successNotification)
                stopSelf()
            }

            is ApiResult.Error -> {
                val errorNotification = notificationBuilder
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentTitle(getString(R.string.download_failed_notification_title))
                    .setContentText(getString(R.string.error_download_failed))
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .build()

                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                notificationManager.cancel(NOTIFICATION_ID)
                notificationManager.notify(ERROR_NOTIFICATION_ID, errorNotification)
                stopSelf()
            }
        }
    }

    private fun buildProgressNotification(
        options: DownloadOptions,
        progress: Int
    ): Notification {
        val videoTitle = options.title ?: getString(R.string.unknown_title)

        return notificationBuilder
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(getString(R.string.downloading_notification_title, videoTitle))
            .setContentText("$progress%")
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setContentIntent(createBringToFrontPendingIntent())
            .build()
    }

    private fun createBringToFrontPendingIntent(): PendingIntent {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }

        return PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createBaseBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setOnlyAlertOnce(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.download_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.download_channel_description)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 101
        private const val ERROR_NOTIFICATION_ID = 102
        private const val SUCCESS_NOTIFICATION_ID = 103
        private const val CHANNEL_ID = "download_channel_v2"
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
