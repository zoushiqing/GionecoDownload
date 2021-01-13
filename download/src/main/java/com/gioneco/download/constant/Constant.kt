package com.gioneco.download.constant

/**
 *Created by zsq
 *on 2021-01-08
 */
class Constant {
    companion object {
        const val TAG = "GionecoDownload"
        //下载中
        const val DOWNLOAD_KEEP = 1
        //下载完成
        const val DOWNLOAD_COMPLETE = 2
        //下载失败
        const val DOWNLOAD_FAIL = 3


        //暂停状态
        const val DOWNLOAD_STATE_PAUSE = 1
        //下载状态
        const val DOWNLOAD_STATE_START = 2

        //控制打印日志
        var DEBUG = false
    }

}