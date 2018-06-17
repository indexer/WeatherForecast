package com.indexer.weather.model



data class ForecastReturnObject(
    val city: City,
    val cod: String,
    val message: Double,
    val cnt: Int,
    val list: List<X>
)