package com.indexer.weather.model


data class WeatherMain(
        val temp: Double,
        val pressure: Double,
        val humidity: Double,
        val temp_min: Double,
        val temp_max: Double
)