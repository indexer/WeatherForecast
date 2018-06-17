package com.indexer.weather

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.Location
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
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
import java.util.Calendar
import java.util.concurrent.TimeUnit.MINUTES
import com.brouding.simpledialog.SimpleDialog
import com.indexer.ottohub.rest.RestClient
import com.indexer.ottohub.rest.enqueue
import com.indexer.weather.adapter.SpacesItemDecoration
import com.indexer.weather.listener.ConnectivityReceiver
import com.indexer.weather.viewmodel.LocationData
import com.sembozdemir.permissionskt.askPermissions

class HomeActivity : AppCompatActivity(),
    BaseViewHolder.OnItemClickListener,
    ConnectivityReceiver.ConnectivityReceiverListener {

  private lateinit var appDatabase: AppDatabase
  private var saveWeather: SaveWeather? = null
  private lateinit var weatherAdapter: WeatherAdapter
  private lateinit var gridLayoutManager: LinearLayoutManager
  private lateinit var homeGridViewModel: HomeGridViewModel
  private lateinit var locationData: LocationData
  private var mSnackBar: Snackbar? = null

  override fun onNetworkConnectionChanged(isConnected: Boolean) {
    showMessage(isConnected)
  }

  private fun showMessage(isConnected: Boolean) {
    if (!isConnected) {
      val messageToUser = "You are offline now."
      mSnackBar =
          Snackbar.make(findViewById(R.id.main_view), messageToUser, Snackbar.LENGTH_LONG)
      mSnackBar!!.duration = Snackbar.LENGTH_INDEFINITE
      mSnackBar!!.show()
      weather_icon.visibility = View.GONE
    } else {
      val countryCode = intent.getStringExtra(Config.country)

      val list = appDatabase.weatherDao.getAllSaveWeather()
      if (list.value == null && countryCode == null) {
        val intent = Intent(this@HomeActivity, MainActivity::class.java)
        startActivity(intent)
        this.finish()
      } else {
        if (countryCode != null) {
          homeGridViewModel.saveWeatherInformation(countryCode, appDatabase)
        }
      }
      changeGridData()
      country_weather.layoutManager = gridLayoutManager
      country_weather.adapter = weatherAdapter
      country_weather.addItemDecoration(SpacesItemDecoration(16))
      queueNetWork()
    }
  }

  override fun onItemClick(position: Int) {
    saveWeather = weatherAdapter.getItem(position)
    dashBoardView(saveWeather)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
    locationData = LocationData(this)

    homeGridViewModel = ViewModelProviders.of(this)
        .get(HomeGridViewModel::class.java)



    add_country.setOnClickListener {
      val intent = Intent(this@HomeActivity, MainActivity::class.java)
      startActivity(intent)
      this.finish()
    }

    main_view.setOnLongClickListener {
      if (weatherAdapter.itemCount > 0) {

        SimpleDialog.Builder(this)
            .setTitle("Remove Country!", true)
            .setContent(saveWeather?.country!!)
            .onConfirm { _, _ ->
              homeGridViewModel.deleteWeather(saveWeather)
              changeGridData()
            }
            .onCancel { _, _ ->

            }
            .setBtnConfirmText("Delete")
            .setBtnCancelText("Cancel")
            .setBtnConfirmTextColor("#000000")
            .show()

      }
      true
    }

    val now = Calendar.getInstance()

    val hour = now.get(Calendar.HOUR_OF_DAY) // Get hour in 24 hour format
    val minute = now.get(Calendar.MINUTE)
    mywidget?.isSelected = true

    val date = Utils.parseDate(hour.toString() + ":" + minute)
    val dateCompare = Utils.parseDate("19:00")

    if (dateCompare.before(date)) {
      main_view.setBackgroundColor(Color.parseColor("#06245F"))
      statusColor("#06245F")
    } else {
      main_view.setBackgroundColor(Color.parseColor("#06CDFF"))
      statusColor("#06CDFF")
    }

    val locationObserver = Observer<Location> {
      val weather = RestClient.getService()
          .getWeatherForLocation(it?.latitude, it?.longitude)
      weather.enqueue(success = {
        forecast?.visibility = View.VISIBLE
        mywidget?.visibility = View.VISIBLE
        val weatherInformation =
          "Weather Station : ${it.body()?.name} Weather Condition : " +
              "${it.body()?.weather!![0].description} Temperature : " + Utils.formatTemperature(
              it.body()?.main?.temp
          )
        mywidget?.text = weatherInformation
      }, failure = {

      })
    }

    askPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
      onGranted {
        locationData.observe(this@HomeActivity, locationObserver)
      }

    }

    weatherAdapter = WeatherAdapter(this)
    gridLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    appDatabase = AppDatabase.getDatabase(this)

    registerReceiver(
        ConnectivityReceiver(),
        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    )

  }

  private fun dashBoardView(saveWeather: SaveWeather?) {
    this.saveWeather = saveWeather
    country.text = saveWeather?.country
    weather_condition.text = saveWeather?.main
    temp_condition.text = Utils.formatTemperature(saveWeather?.temp)
    weather_icon.setImageResource(Utils.icon(saveWeather?.main))
  }

  override fun onResume() {
    super.onResume()
    ConnectivityReceiver.connectivityReceiverListener = this

  }

  private fun queueNetWork() {

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val idList = appDatabase.weatherDao.getAllCityId()
    val myData = Data.Builder()
        .putIntArray(Config.city_list, idList)
        .build()

    //TODO interval need to change to 15Minutes 1 minutes is just for requirements
    val recurringWork: PeriodicWorkRequest =
      PeriodicWorkRequest.Builder(FetchApiWorker().javaClass, 1, MINUTES)
          .setConstraints(constraints)
          .setInputData(myData)
          .build()

    WorkManager.getInstance()
        .enqueue(recurringWork)
  }

  @SuppressLint("ObsoleteSdkInt") private fun statusColor(color: String) {
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
            val lastIndex = weatherAdapter.itemCount - 1
            dashBoardView(
                weatherAdapter.getItem(lastIndex)
            )
          }
          weatherAdapter.notifyDataSetChanged()
        })
  }

}
