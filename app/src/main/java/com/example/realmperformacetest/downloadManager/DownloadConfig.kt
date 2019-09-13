package com.example.realmperformacetest.downloadManager

import android.net.Uri

class DownloadConfig(
    val uri:Uri,
    val fileName:String,
    val title:String,
    val description:String,
    val notificationVisibility:Int
)