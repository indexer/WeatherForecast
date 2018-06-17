package com.indexer.weather.adapter

import android.view.ViewGroup


import android.view.LayoutInflater
import com.indexer.weather.R
import com.indexer.weather.base.BaseAdapter
import com.indexer.weather.base.BaseViewHolder
import com.indexer.weather.model.SaveWeather
import com.indexer.weather.viewholder.WeatherViewHolder


class WeatherAdapter(var onItemClickListener: BaseViewHolder.OnItemClickListener) :
        BaseAdapter<WeatherViewHolder, SaveWeather>() {

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.onBind(mItems[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.weather_item, parent, false)
        return WeatherViewHolder(view, onItemClickListener)
    }


}