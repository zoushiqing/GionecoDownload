package com.gioneco.download.db

import android.content.Context
import android.util.Log
import com.gioneco.download.bean.DownloadInfo


/**
 *Created by zsq
 *on 2021-01-08
 */
class DBManager private constructor(context: Context) {
    private var helper: DBHelper = DBHelper(context)

    companion object {
        @Volatile
        var instance: DBManager? = null

        fun getInstance(context: Context): DBManager {
            if (instance == null) {
                synchronized(DBManager::class) {
                    if (instance == null) {
                        instance = DBManager(context)
                    }
                }
            }
            return instance!!
        }
    }

    fun saveInfos(infos: List<DownloadInfo>) {
        val db = helper.writableDatabase
        for (info in infos) {
            db.execSQL(
                "insert into downloadinfo(thread_id,start_pos, end_pos,complete_size,url) values (?,?,?,?,?)",
                arrayOf(
                    info.threadId,
                    info.startPos,
                    info.endPos,
                    info.completeSize,
                    info.url
                )
            )
        }
    }


    fun saveInfo(info: DownloadInfo) {
        val db = helper.writableDatabase
        db.execSQL(
            "insert into downloadinfo(thread_id,start_pos, end_pos,complete_size,url) values (?,?,?,?,?)",
            arrayOf(
                info.threadId,
                info.startPos,
                info.endPos,
                info.completeSize,
                info.url
            )
        )
    }


    /**
     * 查看数据库中是否有数据
     */
    fun isHasInfos(urlstr: String): Boolean {
        val db = helper.writableDatabase
        //        String sql = "select count(*) from downloadinfo where url=?";
        val cursor = db.query("downloadinfo", null, "url = ?", arrayOf(urlstr), null, null, null)
        val exists = cursor.moveToNext()
        cursor.close()
        return exists
    }


    fun closeDb() {
        helper.close()
    }

    fun delete(url: String) {
        val db = helper.writableDatabase
        db.delete("downloadinfo", "url=?", arrayOf(url))
        db.close()
    }

    fun updataInfos(threadId: Int, compeleteSize: Int, urlstr: String) {
        val db = helper.getWritableDatabase()
        val sql = "update downloadinfo set complete_size=? where thread_id=? and url=?"
        val bindArgs = arrayOf(compeleteSize, threadId, urlstr)
        db.execSQL(sql, bindArgs)
    }

    /**
     * 得到下载具体信息
     */
    fun getInfos(urlstr: String): List<DownloadInfo> {
        val db = helper.writableDatabase
        val list = ArrayList<DownloadInfo>()
        val sql =
            "select thread_id, start_pos, end_pos,complete_size,url from downloadinfo where url=?"
        val cursor = db.rawQuery(sql, arrayOf(urlstr))
        while (cursor.moveToNext()) {
            val info = DownloadInfo(
                cursor.getInt(0),
                cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                cursor.getString(4)
            )
            list.add(info)
        }
        cursor.close()
        return list
    }

    fun getInfo(urlstr: String, threadid: Int): DownloadInfo {
        val db = helper.writableDatabase
        val sql =
            "select thread_id, start_pos, end_pos,complete_size,url from downloadinfo where url=? and thread_id=?"
        val cursor = db.rawQuery(sql, arrayOf(urlstr, threadid.toString()))
        cursor.moveToFirst()
        val info = DownloadInfo(
            cursor.getInt(cursor.getColumnIndex("thread_id")),
            cursor.getInt(cursor.getColumnIndex("start_pos")),
            cursor.getInt(cursor.getColumnIndex("end_pos")),
            cursor.getInt(cursor.getColumnIndex("complete_size")),
            cursor.getString(cursor.getColumnIndex("url"))
        )
        Log.d(
            "DB", "get db info print: " + cursor.getInt(cursor.getColumnIndex("thread_id")) + " " +
                    cursor.getInt(cursor.getColumnIndex("start_pos")) + " " + cursor.getInt(
                cursor.getColumnIndex(
                    "end_pos"
                )
            ) + " " + cursor.getInt(cursor.getColumnIndex("complete_size")) + " "
        )
        cursor.close()
        return info
    }
}