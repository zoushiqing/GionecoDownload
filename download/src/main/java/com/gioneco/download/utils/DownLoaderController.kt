package com.gioneco.download.utils

import android.content.Context
import com.gioneco.download.constant.Constant
import com.gioneco.download.listener.DownloadListener
import com.gioneco.download.state.DownloadState
import com.gioneco.download.state.impl.StartDownloadState
import com.gioneco.download.state.impl.StopDownloadState

/**
 *Created by zsq
 *on 2021-01-08
 */
class DownLoaderController {

    private var mState: DownloadState? = null
    private val startDownloadState = StartDownloadState()
    private val stopDownloadState = StopDownloadState()


    fun setDebugMode(debug: Boolean) {
        Constant.DEBUG = debug
    }

    fun startDownload(
        context: Context,
        downLoadUrl: String,
        filename: String,
        threadCount: Int,
        listener: DownloadListener
    ) {
        mState = startDownloadState
        mState?.startDownload(context, downLoadUrl, filename, threadCount, listener)
    }

    fun stopDownload(
        downLoadUrl: String
    ) {
        mState = stopDownloadState
        mState?.pauseDownload(downLoadUrl)
    }

}