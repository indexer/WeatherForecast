package com.indexer.weather.viewholder


import android.view.View

import com.indexer.weather.base.BaseViewHolder
import com.indexer.weather.model.Country
import kotlinx.android.synthetic.main.country_item.view.*


class CountryViewHolder(itemView: View, listener: OnItemClickListener?) :
        BaseViewHolder(itemView, listener) {
    fun onBind(country: Country) {
        itemView.country_name.text = "${country.capital} / ${country.name}"
    }


}