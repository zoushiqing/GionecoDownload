package com.gioneco.download.bean

/**
 *Created by zsq
 *on 2021-01-08
 */
data class DownloadInfo(
    val threadId: Int,// 下载器id
    val startPos: Int,// 开始点
    val endPos: Int,// 结束点
    val completeSize: Int,// 完成度
    val url: String// 下载器网络标识
)