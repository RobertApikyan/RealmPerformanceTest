package com.example.realmperformacetest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.realmperformacetest.R

abstract class AppPagingAdapter<D : Any, VH : AppViewHolder<D>> :
    AppAdapter<Any, AppViewHolder<Any>>() {

    private companion object {
        const val TYPE_LOADING_ITEM = -1
        const val TYPE_DATA_ITEM = 0
    }

    private var loadingView: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder<Any> {

        fun inflate(layoutId: Int) = LayoutInflater.from(parent.context).inflate(
            layoutId,
            parent, false
        )

        return when (viewType) {
            TYPE_DATA_ITEM -> getViewHolder(
                inflate(getLayoutResId(viewType)), viewType
            )
            TYPE_LOADING_ITEM -> {
                loadingView = inflate(getLoadingItemLayoutResId())
                return getLoadingItemViewHolder(loadingView!!)
            }
            else -> throw IllegalStateException(
                "Unknown viewType in ${this.javaClass.canonicalName ?: "AppPagingAdapter"}"
            )
        }
    }

    override fun onBindViewHolder(holder: AppViewHolder<Any>, position: Int) {
        @Suppress("UNCHECKED_CAST")
        if (!isLoadingPosition(position)) {
            holder.bind(rows[position])
        }
    }

    final override fun getLayoutResId(viewType: Int) = getDataItemLayoutResId()

    @Suppress("UNCHECKED_CAST")
    final override fun getViewHolder(itemView: View, viewType: Int): AppViewHolder<Any> =
        getDataItemViewHolder(itemView) as AppViewHolder<Any>

    final override fun getItemCount(): Int {
        return super.getItemCount() + 1 // +1 for loading item
    }

    final override fun getItemViewType(position: Int): Int {
        return if (isLoadingPosition(position))// +1 for loading item
            TYPE_LOADING_ITEM
        else
            TYPE_DATA_ITEM
    }

    private fun isLoadingPosition(position: Int) = position == super.getItemCount()

    // Default implementation for loading indicator
    protected open fun getLoadingItemViewHolder(loadingView: View) = AppLoadingView(loadingView)

    protected open fun getLoadingItemLayoutResId() = R.layout.item_ring_loading

    protected abstract fun getDataItemLayoutResId(): Int
    protected abstract fun getDataItemViewHolder(itemView: View): VH

    fun getLoadingView() = loadingView

    open class AppLoadingView(itemView: View) : AppViewHolder<Any>(itemView) {
        final override fun onBind(data: Any) {}
    }
}
