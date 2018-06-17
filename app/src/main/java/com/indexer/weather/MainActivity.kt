package com.indexer.weather

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.WindowManager
import com.indexer.ottohub.rest.RestClient
import com.indexer.ottohub.rest.enqueue
import com.indexer.weather.adapter.CountryAdapter
import com.indexer.weather.base.BaseViewHolder
import com.indexer.weather.base.Config
import com.indexer.weather.base.Utils
import com.indexer.weather.database.AppDatabase
import com.indexer.weather.utils.onChange
import com.indexer.weather.viewmodel.LocationData
import com.sembozdemir.permissionskt.askPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), BaseViewHolder.OnItemClickListener {

  override fun onItemClick(position: Int) {
    val twoCode = countryAdapter.getItem(position)
        .alpha2Code
    val cioc = countryAdapter.getItem(position)
        .cioc
    val countryName = countryAdapter.getItem(position)
        .capital
    val intent = Intent(this@MainActivity, HomeActivity::class.java)
    intent.putExtra(Config.twocode, twoCode)
    intent.putExtra(Config.country, countryName)
    intent.putExtra(Config.cioc, cioc)
    startActivity(intent)
    finish()
  }

  private lateinit var locationData: LocationData
  private lateinit var appDatabase: AppDatabase
  private lateinit var countryAdapter: CountryAdapter
  private lateinit var linearLayoutManager: LinearLayoutManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    locationData = LocationData(this)


    linearLayoutManager = LinearLayoutManager(this)

    appDatabase = AppDatabase.getDatabase(this)

    val now = Calendar.getInstance()

    val hour = now.get(Calendar.HOUR_OF_DAY) // Get hour in 24 hour format
    val minute = now.get(Calendar.MINUTE)

    val date = Utils.parseDate(hour.toString() + ":" + minute)
    val dateCompare = Utils.parseDate("18:00")

    if (dateCompare.before(date)) {
      main_views.setBackgroundColor(Color.parseColor("#06245F"))
      statusColor("#06245F")
    } else {
      main_views.setBackgroundColor(Color.parseColor("#06CDFF"))
      statusColor("#06CDFF")
    }

    val locationObserver = Observer<Location> {
      val weather = RestClient.getService()
          .getWeatherForLocation(it?.latitude, it?.longitude)
      weather.enqueue(success = {
        Log.e("current", it.body().toString())
      }, failure = {
        Log.e("error", it.message)
      })
    }



    countryAdapter = CountryAdapter(this)
    country_list.adapter = countryAdapter
    country_list.layoutManager = linearLayoutManager
    country_input.editText?.onChange {
      getAllCountry(it)
    }

    askPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
      onGranted {
        locationData.observe(this@MainActivity, locationObserver)
      }
    }

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

  private fun getAllCountry(country: String) {
    // Insert Data
    val search = "%$country%"
    val list = appDatabase
        .countryDao.findByCountryName(search)
    // Stuff that updates the UI
    countryAdapter.items = list
    countryAdapter.notifyDataSetChanged()

  }

}
