package com.gioneco.download.state.impl

import android.content.Context
import com.gioneco.download.constant.Constant
import com.gioneco.download.download.FileDownloader
import com.gioneco.download.listener.DownloadListener
import com.gioneco.download.state.DownloadState

/**
 *Created by zsq
 *on 2021-01-08
 */
class StopDownloadState : DownloadState {
    override fun startDownload(
        context: Context,
        downLoadUrl: String,
        filename: String,
        threadCount: Int,
        listener: DownloadListener
    ) {
    }

    override fun pauseDownload(
        downLoadUrl: String
    ) {
        FileDownloader.instance.putDownloadState(downLoadUrl, Constant.DOWNLOAD_STATE_PAUSE)
    }

}