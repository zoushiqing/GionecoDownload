package com.gioneco.download.utils

import android.util.Log
import com.gioneco.download.constant.Constant

/**
 *Created by zsq
 *on 2021-01-13
 */
fun <T> T.logI() {
    if (Constant.DEBUG)
        Log.i(Constant.TAG, this.toString())
}

fun <T> T.logI(tag: String) {
    if (Constant.DEBUG)
        Log.i(tag, this.toString())
}

fun <T> T.logE() {
    if (Constant.DEBUG)
        Log.e(Constant.TAG, this.toString())
}

fun <T> T.logE(tag: String) {
    if (Constant.DEBUG)
        Log.e(tag, this.toString())
}