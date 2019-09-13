package com.example.realmperformacetest.downloadManager

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor

class Download(
    private val context: Context,
    val config: DownloadConfig,
    private val id: Long
) {
    private var listener:DownloadCompleteListener? = null

    private val manager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    private val completeBroadcast by lazy {
        CompleteBroadcast(listener)
    }

    fun setDownloadCompleteListener(listener: DownloadCompleteListener){
        this.listener = listener
        context.registerReceiver(completeBroadcast,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun makeQuery(): Cursor {
        val query = DownloadManager.Query()
        query.setFilterById(id)
        return manager.query(query)
    }

    fun queryStatus(): Int {
        var status = -1
        val cursor = makeQuery()
        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        }
        cursor.close()
        return status
    }

    fun queryProgress(): Int {
        var progress = -1
        val cursor = makeQuery()
        if (cursor.moveToFirst()) {
            progress =
                (cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)) * 100L / cursor.getLong(
                    cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                )).toInt()
        }
        cursor.close()
        return progress
    }

    class CompleteBroadcast(private val completeListener: DownloadCompleteListener?):BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
                completeListener?.onDownloadComplete()
                context.unregisterReceiver(this)
            }
        }
    }
}