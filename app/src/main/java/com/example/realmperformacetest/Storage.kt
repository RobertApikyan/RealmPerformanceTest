package com.example.realmperformacetest

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.example.realmperformacetest.downloadManager.DownloadCompleteListener
import com.example.realmperformacetest.downloadManager.DownloadConfig
import com.example.realmperformacetest.downloadManager.Downloader
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object Storage {

    private const val DEFAULT_GENERATION_COUNT = 50000

    fun init(context: Context) {
        Realm.init(context)
        val configuration = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded() // while development
            .name(context.getString(R.string.perf))
            .build()
        Realm.setDefaultConfiguration(configuration)
        Realm.compactRealm(configuration)
    }

    suspend fun clear() = withContext(Dispatchers.Default) {
        Realm.getDefaultInstance().use {
            it.executeTransaction {
                it.deleteAll()
            }
        }
        Realm.compactRealm(Realm.getDefaultConfiguration())
    }

    suspend fun generate() = withContext(Dispatchers.Default) {
        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                realm.deleteAll()
                var id = 0
                repeat(DEFAULT_GENERATION_COUNT) {
                    val newData = DataModelRealm()
                    newData.id = id++.toString()
                    newData.altId = UUID.randomUUID().toString()
                    newData.epcis = UUID.randomUUID().toString()
                    newData.dateTime = Date().toString()
                    newData.vuid =
                        UUID.randomUUID().toString()
                    newData.parentId = (id * 100).toString()
                    newData.topParentId = (id * 100).toString()
                    realm.insert(newData)
                }
            }
        }
    }

    suspend fun searchById(id: String, page: Int, rows: Int): MutableList<DataModel> =
        withContext(Dispatchers.Default) {
            if (id.isNotBlank()) {
                if (page == 0) {
                    val result =
                        Realm.getDefaultInstance().use {
                            return@use it.where(DataModelRealm::class.java)
                                .contains("id", id)
                                .findFirst()?.toDataModel()
                        }
                    if (result != null) {
                        return@withContext mutableListOf(result)
                    } else {
                        return@withContext mutableListOf<DataModel>()
                    }
                } else {
                    return@withContext mutableListOf<DataModel>()
                }
            } else {
                return@withContext Realm.getDefaultInstance().use {
                    return@use it.where(DataModelRealm::class.java)
                        .findAll()
                        .run {
                            if (size < rows) {
                                return@run this
                            } else {
                                return@run subList(page * rows, rows * (1 + page))
                            }
                        }
                        .map { it.toDataModel() }.toMutableList()
                }
            }
        }

    suspend fun writeToFile(filePath: String, progressListener: (Int) -> Unit) =
        withContext(Dispatchers.Default) {
            Realm.getDefaultInstance().use { realm ->
                val file = File(filePath)
                PrintWriter(file).use { writer ->
                    val jobs = realm.where(DataModelRealm::class.java).findAll()
                    var progress = 0
                    for (dataModelRealm in jobs) {
                        writer.println(dataModelRealm.toSequentString())
                        progress++
                        progressListener(progress * 100 / jobs.size)
                    }
                }
            }
        }

    suspend fun readFromFile(filePath: String, progressListener: (Int) -> Unit) =
        withContext(Dispatchers.Default) {
            Realm.getDefaultInstance().use { realm ->
                val file = File(filePath)
                BufferedReader(FileReader(file)).use { reader ->

                    var line: String? = reader.readLine()
                    var progress = 0
                    var transactionChunk = 0
                    val maxTransactionChunk = 5000
                    while (!line.isNullOrBlank()) {
                        if (transactionChunk == 0) {
                            realm.beginTransaction()
                        }
                        transactionChunk++

                        val dataModelRealm = DataModelRealm.fromSequentString(line)
                        progress++
                        progressListener(progress * 33 / DEFAULT_GENERATION_COUNT)
                        realm.insert(dataModelRealm)

                        if (transactionChunk == maxTransactionChunk) {
                            realm.commitTransaction()
                            transactionChunk = 0
                        }

                        line = reader.readLine()
                    }
                }
            }
        }

    suspend fun readFromZipFile(
        zipPath: String,
        filePath: String,
        progressListener: (Int) -> Unit
    ) =
        withContext(Dispatchers.Default) {
            val zipFile = File(zipPath)
            val zipSize = zipFile.length()
            ZipInputStream(FileInputStream(zipPath)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry

                while (entry != null) {
                    BufferedOutputStream(FileOutputStream(filePath)).use { bos ->
                        val bis = zis.buffered()
                        bis.readBytes()
                        var buffer = ByteArray(2048)
                        var read = bis.read(buffer)

                        var progress = 0

                        while (read != -1) {
                            bos.write(buffer)
                            progress += buffer.size
                            progressListener((progress * 100 / zipSize).toInt())

                            read = bis.read(buffer)
                            buffer = ByteArray(2048)
                        }
                        zis.closeEntry()
                        entry = null
                    }
                }

            }
        }

    fun load(
        context: Context,
        url: String,
        zipPath: String,
        filePath: String,
        progressListener: (Int) -> Unit,
        downloadTimeListener: (Long) -> Unit,
        unzipTimeListener: (Long) -> Unit,
        loadToDbTimeListener: (Long) -> Unit
    ) {
        val download = Downloader.get().download(
            context, DownloadConfig(
                Uri.parse(url),
                fileName = "backup.jobs",
                title = "Jobs",
                description = "Jobs description",
                notificationVisibility = DownloadManager.Request.VISIBILITY_VISIBLE
            )
        )
        var downloadTime: Long = System.currentTimeMillis()
        val completeList = object : DownloadCompleteListener {
            override fun onDownloadComplete() {
                downloadTimeListener(System.currentTimeMillis() - downloadTime)
                downloadTime = System.currentTimeMillis()
                progressListener(33)
                GlobalScope.launch {
                    FileUtils.unzip(zipPath, filePath)
                    unzipTimeListener(System.currentTimeMillis() - downloadTime)
                    downloadTime = System.currentTimeMillis()
                    progressListener(66)
                    readFromFile(filePath) {
                        progressListener(66 + it)
                    }
                    loadToDbTimeListener(System.currentTimeMillis() - downloadTime)
                    progressListener(100)
                }
            }
        }
        download.setDownloadCompleteListener(completeList)
    }


}