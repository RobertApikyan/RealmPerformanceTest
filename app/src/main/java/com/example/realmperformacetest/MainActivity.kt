package com.example.realmperformacetest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.realmperformacetest.adapter.PagingScrollListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val adapter by lazy { SearchAdapter() }
    private var page = 0
    private var rows = 50

    private val pagingScrollListener by lazy {
        PagingScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            if (adapter.itemCount > 1) {
                timer("search", searchElapsedTimeTv) {
                    page++
                    val results = Storage.searchById(searchEt.text.toString(), page, rows)
                    adapter.addItems(results as MutableList<Any>)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        recyclerView.addOnScrollListener(pagingScrollListener)

        Storage.init(this)

        generateBtn.setOnClickListener {
            timer("generation", insertionElapsedTimeTv) {
                setProgressEnabled(true)
                page = 0
                Storage.generate()
                setProgressEnabled(false)
                searchBtn.performClick()
            }
        }

        searchBtn.setOnClickListener {
            page = 0
            timer("search", searchElapsedTimeTv) {
                val results = Storage.searchById(searchEt.text.toString(), page, rows)
                adapter.clearAddItems(results as MutableList<Any>)
                pagingScrollListener.reset()
            }
        }

        clearBtn.setOnClickListener {
            GlobalScope.launch {
                Storage.clear()
            }
        }

        searchBtn.performClick()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.generate) {
            startActivity(Intent(this, FileGenerationActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setProgressEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}
