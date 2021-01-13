package com.gioneco.download.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *Created by zsq
 *on 2021-01-08
 */
class ThreadPoolsUtil private constructor() {
    //获取文件大小  定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
    private var fixedThreadPool: ExecutorService = Executors.newFixedThreadPool(3)
    //下载线程池 可缓存 灵活回收空闲线程，若无可回收，则新建线程
    private var cachedThreadPool: ExecutorService = Executors.newCachedThreadPool()

    companion object {
        val instance = ThreadPoolsUtilHolder.threadPoolsUtil
    }

    private object ThreadPoolsUtilHolder {
        val threadPoolsUtil = ThreadPoolsUtil()
    }

    fun getFixedThreadPool(): ExecutorService {
        return fixedThreadPool
    }

    fun getCachedThreadPool(): ExecutorService {
        return cachedThreadPool
    }
}