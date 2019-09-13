package com.example.realmperformacetest.downloadManager.builder

interface DownloadBuilder {
    fun uri(uri: String): DownloadBuilder
    fun fileName(fileName: String): DownloadBuilder
    fun title(title: String): DownloadBuilder
    fun description(description: String): DownloadBuilder
    fun notificationType(notificationType: Int): DownloadBuilder
}