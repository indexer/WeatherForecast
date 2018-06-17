package com.indexer.weather.model


data class ForecastCountryObject(
        val weather: List<Weather>,
        val base: String,
        val main: WeatherMain,
        val visibility: Int,
        val wind: Wind,
        val dt: Int,
        val id: Int,
        val name: String,
        val cod: Int
)