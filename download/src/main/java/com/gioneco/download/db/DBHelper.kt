package com.gioneco.download.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 *Created by zsq
 *on 2021-01-08
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, DBNAME, null, VERSION) {
    companion object {
        private const val DBNAME = "down.db"
        private const val VERSION = 1
        private const val CREATE_TABLE =
            "create table if not exists " +
                    "downloadinfo(_id integer PRIMARY KEY AUTOINCREMENT, " +
                    "thread_id integer," +
                    "start_pos integer, " +
                    "end_pos integer, " +
                    "complete_size integer," +
                    "url char)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}