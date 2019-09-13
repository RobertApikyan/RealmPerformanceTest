package com.example.realmperformacetest.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class PagingScrollListener(
    private val mLinearLayoutManager: LinearLayoutManager,
    val onLoadMore: (() -> Unit)? = null
) : RecyclerView.OnScrollListener() {

    private var previousTotal = 0 // The total number of containers in the data set after the last load
    private var loading = true // True if we are still waiting for the last set of data to load.
    private var visibleThreshold = 10 // The minimum amount of containers to have below your current scroll position before loading more.

    var firstVisibleItem: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        totalItemCount = mLinearLayoutManager.itemCount
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }

        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            // End has been reached
            // Do something

            onLoadMore?.invoke()

            loading = true
        }
    }

    fun setIsLoading(isLoading: Boolean) {
        this.loading = isLoading
    }

    fun reset() {
        previousTotal = 0
        loading = true
        visibleThreshold = 10
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
    }
}