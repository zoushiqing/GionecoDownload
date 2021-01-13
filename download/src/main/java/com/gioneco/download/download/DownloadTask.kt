package com.gioneco.download.download

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.gioneco.download.bean.DownloadInfo
import com.gioneco.download.constant.Constant
import com.gioneco.download.constant.Constant.Companion.TAG
import com.gioneco.download.db.DBManager
import com.gioneco.download.listener.DownloadListener
import java.io.RandomAccessFile
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


/**
 *Created by zsq
 *on 2021-01-08
 */
class DownloadTask(
    context: Context,
    downLoadUrl: String,
    fileSize: Int,
    filename: String,
    private var threadId: Int,
    listener: DownloadListener
) : Runnable {
    private var mListener: DownloadListener = listener
    private var mContext: Context = context
    private var mDownLoadUrl: String = downLoadUrl
    private var mRandomAccessFile: RandomAccessFile? = null
    private var mFilename: String = filename
    private var size: Int = fileSize
    private val mHandler = Handler(Looper.getMainLooper())
    private var flag = false
    //重试次数
    private var mRetryCount = 0

    override fun run() {
        FileDownloader.instance.putDownloadState(mDownLoadUrl, Constant.DOWNLOAD_STATE_START)
        var connection: HttpURLConnection? = null
        var inputStream: BufferedInputStream? = null
        while (mRetryCount < 10) {
            var info: DownloadInfo? = null
            if (DBManager.getInstance(mContext).isHasInfos(mDownLoadUrl)) {     //判断是否存在未完成的该任务
                info = DBManager.getInstance(mContext).getInfo(mDownLoadUrl, threadId)
            }
            Log.d(TAG, "数据库中是否有数据 线程Id $threadId: $info")
            try {
                val url = URL(mDownLoadUrl)
                var compeltesize = info?.completeSize ?: 0
                //本地数据库中的保存的开始位置跟结束位置
                val startPos = info?.startPos ?: 0
                val endPos = info?.endPos ?: 0
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty("Connection", "Keep-Alive")
                Log.d(TAG, "线程id:$threadId Range:  bytes=${startPos + compeltesize}-$endPos")
                connection.setRequestProperty("Range", "bytes=${startPos + compeltesize}-$endPos")
                inputStream = BufferedInputStream(connection.inputStream)
                mRandomAccessFile = RandomAccessFile(mFilename, "rw")
                //上次的最后的写入位置
                mRandomAccessFile?.seek((startPos + compeltesize).toLong())
                Log.d(TAG, " 线程id: $threadId 开始位置: $startPos ")
                val buffer = ByteArray(8 * 1024)
                var length = 0
                while ({ length = inputStream.read(buffer);length }() > 0) {
                    if (FileDownloader.instance.getDownloadState(mDownLoadUrl) == Constant.DOWNLOAD_STATE_PAUSE) { //下载任务被暂停
                        return
                    }
                    Log.d(TAG, "线程id:$threadId ------写入: $length")
                    mRandomAccessFile?.write(buffer, 0, length)
                    compeltesize += length
                    DBManager.getInstance(mContext)
                        .updataInfos(threadId, compeltesize, mDownLoadUrl)  //保存数据库中的下载进度
                    sendMessage(Constant.DOWNLOAD_KEEP, calculateCompleteSize(), null)     //更新进度条
                }
                Log.d(
                    TAG,
                    "线程id: " + threadId + "已完成: " + calculateCompleteSize() + " 总大小: " + size
                )
                if (calculateCompleteSize() >= size) {      //判断下载是否完成
                    sendMessage(Constant.DOWNLOAD_COMPLETE, -1, mDownLoadUrl)
                    //改变状态，可以继续下载
                    FileDownloader.instance.putDownloadState(
                        mDownLoadUrl,
                        Constant.DOWNLOAD_STATE_PAUSE
                    )
                    //删除记录
                    DBManager.instance?.delete(mDownLoadUrl)
                    break
                }
            } catch (e: Exception) {
                if (e.message == "timeout")
                    flag = true
                if (!flag) {
                    val errorMsg =
                        "下载失败,线程id:$threadId 其他异常终止下载：" + e.message + " 重试次数:$mRetryCount"
                    Log.e(TAG, errorMsg)
                    stopDownload(errorMsg)
                } else if (mRetryCount == 9) { //当下载了10都次失败 就终止下载
                    val errorMsg =
                        "下载失败,线程id:$threadId 重试次数过多终止下载：" + e.message + " 重试次数:$mRetryCount"
                    stopDownload(errorMsg)
                    Log.e(TAG, errorMsg)
                }
                //如果是重试，将次数暴露给UI
                if (flag) {
                    mHandler.post {
                        mListener.onFail("线程id:${threadId}   重试次数：${mRetryCount + 1}")
                    }
                }
            } finally {
                try {
                    inputStream?.close()
                    connection?.disconnect()
                    mRandomAccessFile?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (flag) {
                mRetryCount++
            } else break
        }

    }

    private fun stopDownload(errorMsg: String) {
        FileDownloader.instance.putDownloadState(
            mDownLoadUrl,
            Constant.DOWNLOAD_STATE_PAUSE
        )
        sendMessage(Constant.DOWNLOAD_FAIL, -1, errorMsg)
    }

    private fun sendMessage(what: Int, arg1: Int, obj: Any?) {
        mHandler.post {
            when (what) {
                Constant.DOWNLOAD_KEEP -> {
                    mListener.onUpdate(arg1)
                }
                Constant.DOWNLOAD_COMPLETE -> {
                    mListener.onComplete(obj as String)
                }
                Constant.DOWNLOAD_FAIL -> {
                    //删除记录
                    DBManager.instance?.delete(mDownLoadUrl)
                    mListener.onFail(obj as String)
                }
            }
        }
    }

    /**
     * 计算总文件已下载大小
     */
    private fun calculateCompleteSize(): Int {
        var completeSize = 0
        val infos = DBManager.getInstance(mContext).getInfos(mDownLoadUrl)
        for (info in infos) {
            completeSize += info.completeSize
        }
        return completeSize
    }

}