package com.example.realmperformacetest.downloadManager

import android.app.DownloadManager
import android.content.Context
import android.os.Environment

class DownloaderImpl : Downloader {

    override fun download(context: Context, config: DownloadConfig): Download {
        var downloadId = -1L

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs()

        val request = DownloadManager.Request(config.uri)

        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_MOBILE
                    or
                    DownloadManager.Request.NETWORK_WIFI
        )

        if (config.title.isNotBlank()) {
            request.setTitle(config.title)
        }

        request.setNotificationVisibility(config.notificationVisibility)

        if (config.description.isNotBlank()){
            request.setDescription(config.description)
        }

        if (config.fileName.isNotBlank()) {
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                config.fileName
            )

            downloadId = manager.enqueue(request)
        }

        return Download(context,config,downloadId)
    }


}