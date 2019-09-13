package com.example.realmperformacetest.downloadManager

import android.content.Context

interface Downloader {
    companion object {
        fun get():Downloader = DownloaderImpl()
    }

    fun download(context: Context, config: DownloadConfig): Download
}