package com.gioneco.download.download

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.gioneco.download.bean.DownloadInfo
import com.gioneco.download.constant.Constant
import com.gioneco.download.constant.Constant.Companion.TAG
import com.gioneco.download.db.DBManager
import com.gioneco.download.listener.DownloadListener
import com.gioneco.download.utils.ThreadPoolsUtil
import com.gioneco.download.utils.logI
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile


/**
 *Created by zsq
 *on 2021-01-08
 */
class FileDownloader private constructor() {

    private lateinit var context: Context
    private lateinit var downLoadUrl: String
    private var fileSize: Int = 0
    private lateinit var filename: String
    private var threadCount: Int = 0
    private val downloadStateMap: HashMap<String, Int> = HashMap()
    private lateinit var mListener: DownloadListener

    companion object {

        @SuppressLint("StaticFieldLeak")
        val instance = FileDownloaderHolder.fileDownloader
    }

    private object FileDownloaderHolder {
        @SuppressLint("StaticFieldLeak")
        val fileDownloader = FileDownloader()
    }

    private val mHandler = Handler(Looper.getMainLooper())


    @Synchronized
    fun init(
        context: Context,
        downLoadUrl: String, fileSize: Int,
        filename: String, threadCount: Int, listener: DownloadListener
    ): FileDownloader {
        "下载参数：context = [${context}], downLoadUrl = [${downLoadUrl}], fileSize = [${fileSize}], filename = [${filename}], threadCount = [${threadCount}], listener = [${listener}]".logI()
        this.context = context
        this.downLoadUrl = downLoadUrl
        this.fileSize = fileSize
        this.filename = filename
        this.threadCount = threadCount
        this.mListener = listener
        initDatas()
        return this
    }


    private fun initDatas() {
        var accessFile: RandomAccessFile? = null
        val file: File
        val block =
            if (fileSize % threadCount == 0) fileSize / threadCount else fileSize / threadCount + 1
        try {
            file = File(filename)
            if (file.parentFile?.exists() == false) {
                file.parentFile!!.mkdirs()
            }
            if (!DBManager.getInstance(context).isHasInfos(downLoadUrl)) {
                for (i in 0 until threadCount) {
                    val info = DownloadInfo(i, i * block, (i + 1) * block, 0, downLoadUrl)
                    DBManager.getInstance(context).saveInfo(info)
                }
            }
            "数据库中存储的下载记录：" + DBManager.getInstance(context).getInfos(downLoadUrl).logI()
            accessFile = RandomAccessFile(file, "rw")
            if (accessFile.length() == fileSize.toLong()) {
                return
            }
            accessFile.setLength(fileSize.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                accessFile?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun getDownloadState(downloadurl: String) = downloadStateMap[downloadurl] ?: -1

    @Synchronized
    fun putDownloadState(downloadurl: String, state: Int) {
        downloadStateMap[downloadurl] = state
    }

    @Synchronized
    fun startDownload() {
        if (downloadStateMap[downLoadUrl] != null && downloadStateMap[downLoadUrl] == Constant.DOWNLOAD_STATE_START) {
            "开始下载失败:已处于开始下载状态".logI()
            return
        }
        mHandler.post { mListener.onStart(fileSize) }
        for (i in 0 until threadCount) {
            ThreadPoolsUtil.instance.getCachedThreadPool()
                .execute(DownloadTask(context, downLoadUrl, fileSize, filename, i, mListener))
        }
    }
}