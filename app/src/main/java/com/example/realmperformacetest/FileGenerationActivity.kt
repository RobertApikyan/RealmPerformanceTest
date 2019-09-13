package com.example.realmperformacetest

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_file_generation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FileGenerationActivity : AppCompatActivity() {

    companion object {
        const val ext = ".jobs"
        const val fileName = "backup"
        const val fileUrl =
            "https://drive.google.com/uc?authuser=0&id=1QwgPvT1gslcPGUSWAMxAtBkYv5we7pUH&export=download"
    }

    val zipPath by lazy {
        "${Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/$fileName$ext"
    }

    val filePath by lazy {
        "${Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path}/$fileName$ext"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_generation)
        writeToFileBtn.setOnClickListener {
            writeDataOnDisk()
        }
        downloadBtn.setOnClickListener {
            downloadFile()
        }
        readBtn.setOnClickListener {
            readFile()
        }
        readFromZipBtn.setOnClickListener {
            unzipFile()
        }

    }

    private fun readFile() = GlobalScope.launch(Dispatchers.Main) {
        Storage.readFromFile(
            filePath
        ) { progress ->
            GlobalScope.launch(Dispatchers.Main) {
                readProgress.progress = progress
            }
        }
    }

    private fun writeDataOnDisk() = GlobalScope.launch(Dispatchers.Main) {
        Storage.writeToFile(
            filePath
        ) { progress ->
            GlobalScope.launch(Dispatchers.Main) {
                writePb.progress = progress
            }
        }
    }

    private fun unzipFile() = GlobalScope.launch(Dispatchers.Main) {
        Storage.readFromZipFile(
            zipPath = "${Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/$fileName",
            filePath = "${Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path}/$fileName"
        ) { progress ->
            GlobalScope.launch(Dispatchers.Main) {
                zipProgress.progress = progress
            }
        }
    }

    private fun downloadFile() {
        Storage.load(this, fileUrl, zipPath, filePath, { progress ->
            runOnUiThread {
                downloadPb.progress = progress
            }
        }, downloadTimeListener = {
            runOnUiThread {
                downloadTv.text = "Download Time: $it"
            }
        }, unzipTimeListener = {
            runOnUiThread {
                unzipTv.text = "Unzip Time: $it"
            }
        }, loadToDbTimeListener = {
            runOnUiThread {
                dbTv.text = "Load Jobs to db: $it"
            }
        })
    }


}