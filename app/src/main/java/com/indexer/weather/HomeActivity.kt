package com.indexer.weather

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.WindowManager
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
import kotlinx.android.synthetic.main.activity_main.main_views
import java.util.Calendar
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

    val now = Calendar.getInstance()

    val hour = now.get(Calendar.HOUR_OF_DAY) // Get hour in 24 hour format
    val minute = now.get(Calendar.MINUTE)

    val date = Utils.parseDate(hour.toString() + ":" + minute)
    val dateCompare = Utils.parseDate("18:00")

    if (dateCompare.before(date)) {
      main_view.setBackgroundColor(Color.parseColor("#06245F"))
      statusColor("#06245F")
    } else {
      main_view.setBackgroundColor(Color.parseColor("#06CDFF"))
      statusColor("#06CDFF")
    }

    weatherAdapter = WeatherAdapter(this)
    gridLayoutManager = GridLayoutManager(this, 3)
    appDatabase = AppDatabase.getDatabase(this)
    changeGridData()
    if (weatherAdapter.items?.size!! < 0) {
      val intent = Intent(this@HomeActivity, MainActivity::class.java)
      startActivity(intent)
      this.finish()
    } else {
      val countryCode = intent.getStringExtra(Config.country)
      if (countryCode != null) {
        homeGridViewModel.saveWeatherInformation(countryCode, appDatabase)
      }
    }
    country_weather.layoutManager = gridLayoutManager
    country_weather.adapter = weatherAdapter
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
        .putIntArray(Config.city_list, idList)
        .build()

    val recurringWork: PeriodicWorkRequest =
      PeriodicWorkRequest.Builder(FetchApiWorker().javaClass, 1, MINUTES)
          .setConstraints(constraints)
          .setInputData(myData)
          .build()

    WorkManager.getInstance()
        .enqueue(recurringWork)
  }

  @SuppressLint("ObsoleteSdkInt")
  private fun statusColor(color: String) {
    if (Build.VERSION.SDK_INT >= 21) {
      val window = window
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.statusBarColor = Color.parseColor(color)
    }
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
