package com.example.realmperformacetest.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer

abstract class AppAdapter<D : Any, VH : AppViewHolder<D>> : RecyclerView.Adapter<VH>() {

    var rows = mutableListOf<D>()

    fun setItems(newItems: MutableList<D>){
        rows = newItems
        notifyDataSetChanged()
    }

    fun clearAddItems(newItems: List<D>) {
        rows.clear()
        rows.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setItem(index: Int, item: D){
        rows[index] = item
        notifyItemChanged(index)
    }

    fun addItem(item: D){
        rows.add(item)
        notifyItemInserted(rows.size - 1)
    }

    fun addItems(newItems: List<D>) {
        rows.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        getViewHolder(
            LayoutInflater.from(parent.context).inflate(
                getLayoutResId(viewType),
                parent, false
            ), viewType
        )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(rows[position])

    protected fun getItem(position: Int): D {
        return rows[position]
    }

    override fun getItemCount(): Int {
        return rows.size
    }

    abstract fun getLayoutResId(viewType: Int): Int

    abstract fun getViewHolder(itemView: View, viewType: Int): VH

    protected open fun areItemsTheSame(oldItem: D, newItem: D) = false

    protected open fun areContentsTheSame(oldItem: D, newItem: D) = oldItem == newItem
}

open class AppViewHolder<D : Any>(itemView: View) : RecyclerView.ViewHolder(itemView),
    LayoutContainer {

    override val containerView: View?
        get() = itemView

    lateinit var data: D
    val context: Context by lazy { itemView.context }

    fun bind(data: D) {
        this.data = data
        onBind(data)
    }

    protected open fun onBind(data: D) {

    }

}
