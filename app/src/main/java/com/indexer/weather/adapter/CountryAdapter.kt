package com.indexer.weather.adapter

import android.view.ViewGroup


import android.view.LayoutInflater
import com.indexer.weather.R

import com.indexer.weather.base.BaseAdapter
import com.indexer.weather.base.BaseViewHolder
import com.indexer.weather.model.Country
import com.indexer.weather.viewholder.CountryViewHolder


class CountryAdapter (var onItemClickListener: BaseViewHolder.OnItemClickListener):
        BaseAdapter<CountryViewHolder, Country>() {

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.onBind(mItems[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.country_item, parent, false)
        return CountryViewHolder(view,onItemClickListener)
    }


}