package com.indexer.weather.model


data class X(
    val dt: Int,
    val temp: Temp,
    val pressure: Double,
    val humidity: Int,
    val weather: List<Weather>,
    val speed: Double,
    val deg: Int,
    val clouds: Int,
    val rain: Double
)