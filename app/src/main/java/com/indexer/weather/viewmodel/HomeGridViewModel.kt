package com.indexer.weather.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.indexer.ottohub.rest.RestClient
import com.indexer.ottohub.rest.enqueue
import com.indexer.weather.database.AppDatabase
import com.indexer.weather.model.SaveWeather
import java.util.Date

class HomeGridViewModel(application: Application) : AndroidViewModel(application) {
  private lateinit var appDatabase: AppDatabase

  private fun saveItem(saveWeather: SaveWeather) {
    appDatabase.weatherDao.insertWeather(saveWeather)
  }

  fun getSaveWeather(appDatabase: AppDatabase): LiveData<MutableList<SaveWeather>> {
    this.appDatabase = appDatabase
    return appDatabase.weatherDao.getAllSaveWeather()
  }

  fun deleteWeather(saveWeather: SaveWeather?) {
    appDatabase.weatherDao.deleteSaveWeather(saveWeather)
  }

  fun saveWeatherInformation(
    countryCode: String,
    appDatabase: AppDatabase
  ) {
    this.appDatabase = appDatabase
    val getWetherByCountryCode =
      RestClient.getService()
          .getWeatherForCountry(countryCode)
    getWetherByCountryCode.enqueue(success = {
      if (it.isSuccessful) {
        Log.e("Current", "" + it.body().toString())
        val weather = SaveWeather(
            it.body()?.id,
            countryCode, it.body()?.weather?.get(0)?.main,
            it.body()?.main?.temp, it.body()?.main?.pressure, "", Date()
        )
        saveItem(weather)
      }
    }, failure = {
      Log.e("message", it.message)
    })
  }
}