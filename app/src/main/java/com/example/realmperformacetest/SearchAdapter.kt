package com.example.realmperformacetest

import android.view.View
import com.example.realmperformacetest.adapter.AppPagingAdapter
import com.example.realmperformacetest.adapter.AppViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_data.*

class SearchAdapter : AppPagingAdapter<DataModel, SearchAdapter.SearchViewHolder>() {
    override fun getDataItemLayoutResId() = R.layout.item_data

    override fun getDataItemViewHolder(itemView: View) = SearchViewHolder(itemView)

    class SearchViewHolder(override val containerView: View) :
        AppViewHolder<DataModel>(containerView), LayoutContainer {
        override fun onBind(data: DataModel) {
            idTv.text = data.id
            altTv.text = data.altId
            epcisTv.text = data.epcis
            dateTv.text = data.dateTime
            vuidTv.text = data.vuid
            parentIdTv.text = data.parentId
            topLevelParentIdTv.text = data.topParentId
        }
    }
}