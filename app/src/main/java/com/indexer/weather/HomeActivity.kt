package com.indexer.weather

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.indexer.weather.adapter.WeatherAdapter
import com.indexer.weather.base.BaseViewHolder
import com.indexer.weather.base.Config
import com.indexer.weather.base.Utils
import com.indexer.weather.database.AppDatabase
import com.indexer.weather.job.FetchApiWorker
import com.indexer.weather.model.SaveWeather
import com.indexer.weather.viewmodel.HomeGridViewModel
import kotlinx.android.synthetic.main.activity_home.*
import java.util.concurrent.TimeUnit.MINUTES

class HomeActivity : AppCompatActivity(), BaseViewHolder.OnItemClickListener {
  private lateinit var appDatabase: AppDatabase
  private var saveWeather: SaveWeather? = null
  private lateinit var weatherAdapter: WeatherAdapter
  private lateinit var gridLayoutManager: GridLayoutManager
  private lateinit var homeGridViewModel: HomeGridViewModel

  override fun onItemClick(position: Int) {
    saveWeather = weatherAdapter.getItem(position)
    dashBoradView(saveWeather)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)

    homeGridViewModel = ViewModelProviders.of(this)
        .get(HomeGridViewModel::class.java)





    add_country.setOnClickListener {
      val intent = Intent(this@HomeActivity, MainActivity::class.java)
      startActivity(intent)
      this.finish()
    }

    main_view.setOnLongClickListener {
      if (weatherAdapter.itemCount > 0) {
        homeGridViewModel.deleteWeather(saveWeather)
        changeGridData()
      }
      true
    }

    weatherAdapter = WeatherAdapter(this)
    gridLayoutManager = GridLayoutManager(this, 3)
    appDatabase = AppDatabase.getDatabase(this)
    val countryCode = intent.getStringExtra(Config.country)
    homeGridViewModel.saveWeatherInformation(countryCode, appDatabase)
    country_weather.layoutManager = gridLayoutManager
    country_weather.adapter = weatherAdapter
    changeGridData()
    queueNetWork()
  }

  private fun dashBoradView(saveWeather: SaveWeather?) {
    this.saveWeather = saveWeather
    country.text = saveWeather?.country
    weather_condition.text = saveWeather?.main
    temp_condition.text = Utils.formatTemperature(saveWeather?.temp)
    weather_icon.setImageResource(Utils.icon(saveWeather?.main))
  }

  private fun queueNetWork() {

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val idList = appDatabase.weatherDao.getAllCityId()
    val myData = Data.Builder()
        .putIntArray("city_list", idList)
        .build()

    val recurringWork: PeriodicWorkRequest =
      PeriodicWorkRequest.Builder(FetchApiWorker().javaClass, 1, MINUTES)
          .setConstraints(constraints)
          .setInputData(myData)
          .build()

    WorkManager.getInstance()
        .enqueue(recurringWork)
  }

  private fun changeGridData() {
    homeGridViewModel.getSaveWeather(appDatabase)
        .observe(this, Observer {
          weatherAdapter.items = it
          if (weatherAdapter.items?.isNotEmpty()!!) {
            dashBoradView(
                weatherAdapter
                    .getItem(weatherAdapter.itemCount - 1)
            )
          }
          weatherAdapter.notifyDataSetChanged()
        })
  }

}
