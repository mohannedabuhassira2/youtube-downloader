package com.mohannedabuhassira.youtubedownloader.features.downloader.data

internal sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String?, val throwable: Throwable? = null) : ApiResult<Nothing>
    data class Loading(val progress: Float? = null) : ApiResult<Nothing>
}
