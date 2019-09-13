package com.example.realmperformacetest

import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun timer(label: String, textView: TextView, block: suspend () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        val startTime = System.currentTimeMillis()
        block()
        val endTime = System.currentTimeMillis()
        val elapsedSeconds = (endTime - startTime)
        textView.text = "$label : $elapsedSeconds ms"
    }
}