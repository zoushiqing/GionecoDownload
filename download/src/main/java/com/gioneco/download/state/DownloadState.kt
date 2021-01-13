package com.gioneco.download.state

import android.content.Context
import com.gioneco.download.listener.DownloadListener

/**
 *Created by zsq
 *on 2021-01-08
 */
interface DownloadState {
    fun startDownload(
        context: Context,
        downLoadUrl: String,
        filename: String,
        threadCount: Int,
        listener: DownloadListener
    )

    fun pauseDownload( downLoadUrl: String)
}