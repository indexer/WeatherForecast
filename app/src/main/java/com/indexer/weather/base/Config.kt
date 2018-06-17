package com.indexer.weather.base

object Config {
    //Preferences TAG
    const val BASE_URL = "http://api.openweathermap.org"
    const val LAST_LATITUDE = "last_latitude"
    const val LAST_LONGITUDE = "last_longitude"
    const val LOCATION_URL = "/data/2.5/weather"
    const val LOCATION_CITIES="/data/2.5/weather"
    //http://api.openweathermap.org/data/2.5/forecast/daily?q=London&units=metric&cnt=7
    const val LOCATION_FORECAST = "/data/2.5/forecast/daily"
    const val API_KEY = "c439d507efe743e30d330569ee3ac15a"
    const val USER_INFO = "user_info"
    const val no_internet = "no_internet"
    const val country="country"
    const val twocode="twocode"
    const val cioc ="cioc"
}