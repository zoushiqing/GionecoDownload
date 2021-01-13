package com.gioneco.download.state.impl

import android.content.Context
import android.nfc.Tag
import android.os.Handler
import android.util.Log
import com.gioneco.download.constant.Constant.Companion.TAG
import com.gioneco.download.download.FileDownloader
import com.gioneco.download.listener.DownloadListener
import com.gioneco.download.state.DownloadState
import com.gioneco.download.utils.ThreadPoolsUtil
import java.net.HttpURLConnection
import java.net.URL


/**
 *Created by zsq
 *on 2021-01-08
 */
class StartDownloadState : DownloadState {
    override fun startDownload(
        context: Context,
        downLoadUrl: String,
        filename: String,
        threadCount: Int,
        listener: DownloadListener
    ) {
        ThreadPoolsUtil.instance.getFixedThreadPool().execute {
            val fileSize = getDownloadFileSize(downLoadUrl)
            FileDownloader.instance
                .init(context, downLoadUrl, fileSize, filename, threadCount,listener)
                .startDownload()
        }
    }

    private fun getDownloadFileSize(downLoadUrl: String): Int {
        var connection: HttpURLConnection? = null
        var fileSize = -1
        try {
            val url = URL(downLoadUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            fileSize = connection.contentLength
            Log.i(TAG, "后台文件总大小：$fileSize")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                connection?.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return fileSize
    }

    override fun pauseDownload(
        downLoadUrl: String
    ) {

    }

}