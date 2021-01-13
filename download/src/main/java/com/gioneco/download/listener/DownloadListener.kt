package com.gioneco.download.listener

/**
 *Created by zsq
 *on 2021-01-11
 */
interface DownloadListener {

    /**
     * 开始下载
     */
    fun onStart(max:Int)

    /**
     * 下载中 字节
     */
    fun onUpdate(progress:Int)

    /**
     * 下载完成
     */
    fun onComplete(url:String)

    /**
     * 下载失败
     */
    fun onFail(errorMsg:String)
}