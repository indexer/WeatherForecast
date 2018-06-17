package com.indexer.weather.job

import androidx.work.Worker
import com.indexer.ottohub.rest.RestClient
import com.indexer.ottohub.rest.enqueue
import com.indexer.weather.database.AppDatabase
import com.indexer.weather.model.SaveWeather
import java.util.Date

class FetchApiWorker : Worker() {
  private lateinit var saveWeatherList: ArrayList<SaveWeather>

  override fun doWork(): WorkerResult {
    var string: String? = ""
    val citylist = inputData.getIntArray("city_list")
    citylist.forEach { id ->
      string = if (string == "") {
        id.toString()
      } else {
        string.plus(",$id")
      }
    }
    var appDatabase = AppDatabase.getDatabase(applicationContext)
    val weatherByMultipleCities = RestClient.getService()
        .getWeatherMultipleCities(string!!, "metric")
    saveWeatherList = ArrayList()
    weatherByMultipleCities.enqueue(success = {
      if (it.isSuccessful) {
        for (weather in it.body()?.list!!) {
          val weather = SaveWeather(
              weather.id,
              weather.name, weather.weather[0].main,
              weather.main.temp, weather.main.pressure, "", Date()
          )
          saveWeatherList.add(weather)
          appDatabase.weatherDao.insertWeathers(saveWeatherList)
        }

      }

    }, failure = {

    })

    return WorkerResult.SUCCESS
  }

}