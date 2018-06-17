package com.indexer.weather.model



data class LocationForecastObject(
    val weather: List<Weather>,
    val base: String,
    val main: WeatherMain,
    val visibility: Int,
    val dt: Int,
    val id: Int,
    val name: String,
    val cod: Int
)