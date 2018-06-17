package com.suthaw.restaurnat.rest

import com.indexer.weather.base.Config
import com.indexer.weather.model.ForecastCountryObject
import com.indexer.weather.model.ForecastReturnObject
import com.indexer.weather.model.MultipleCityReturnObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

  @GET(Config.LOCATION_URL)
  fun getWeatherForLocation(
    @Query("lat") latitude: Double?,
    @Query("lon") longitude: Double?
  ): Call<ForecastReturnObject>

  @GET(Config.LOCATION_URL)
  fun getWeatherForCountry(@Query("q") countryCode: String?): Call<ForecastCountryObject>

  @GET(Config.LOCATION_CITIES)
  fun getWeatherMultipleCities(
    @Query("id") id: String, @Query(
        "units"
    ) units: String
  ): Call<MultipleCityReturnObject>

  @GET(Config.LOCATION_FORECAST)
  fun getWeatherForecastLocation(
    @Query("lat") latitude: Double,
    @Query("lon") longitude: Double, @Query("cnt") cnt: Int, @Query("units") units: String
  ): Call<ForecastReturnObject>

}
