package com.indexer.weather.viewholder


import android.view.View

import com.indexer.weather.base.BaseViewHolder
import com.indexer.weather.base.Utils
import com.indexer.weather.model.SaveWeather
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.country_item.view.*


class WeatherViewHolder(itemView: View, listener: OnItemClickListener?) :
        BaseViewHolder(itemView, listener) {
    fun onBind(country: SaveWeather) {
        itemView.country_name.text = country.country
        itemView.weather_condition.text = country.main
        itemView.weather_icon.setImageResource(Utils.icon(country.main))
    }


}