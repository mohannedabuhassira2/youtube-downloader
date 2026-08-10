package com.mohannedabuhassira.youtubedownloader.features.history.presentation.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.mohannedabuhassira.youtubedownloader.features.history.R
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFile
import com.mohannedabuhassira.youtubedownloader.features.history.domain.model.DownloadFileType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

internal class MediaManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun playFile(file: DownloadFile) {
        val uri = getUriForFilePath(file.filePath) ?: return
        val mimeType = resolveMimeType(file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivitySafely(intent, context.getString(R.string.error_failed_open))
    }

    fun shareFile(file: DownloadFile) {
        val uri = getUriForFilePath(file.filePath) ?: return
        val mimeType = resolveMimeType(file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, context.getString(R.string.share_via))
        startActivitySafely(chooserIntent, context.getString(R.string.error_failed_share))
    }

    private fun getUriForFilePath(filePath: String): Uri? {
        if (filePath.isEmpty()) {
            return null
        }

        return try {
            if (filePath.startsWith("content://")) {
                filePath.toUri()
            } else {
                val file = File(filePath)
                if (!file.exists()) {
                    return null
                }
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            }
        } catch (e: Exception) {
            Log.e("FileActionHandler", "Error obtaining Uri for path: $filePath", e)
            null
        }
    }

    private fun resolveMimeType(file: DownloadFile): String {
        return if (file.type == DownloadFileType.VIDEO) "video/*" else "audio/*"
    }

    private fun startActivitySafely(
        intent: Intent,
        errorMessage: String
    ) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("FileActionHandler", errorMessage, e)
        }
    }
}
