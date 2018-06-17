package com.indexer.weather

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.WindowManager
import com.indexer.weather.adapter.CountryAdapter
import com.indexer.weather.base.BaseViewHolder
import com.indexer.weather.base.Config
import com.indexer.weather.database.AppDatabase
import com.indexer.weather.utils.onChange
import com.indexer.weather.viewmodel.LocationData
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.Calendar.HOUR

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

    val currentTime = Date()
    val dateToCompare = Date()
    val calendar = Calendar.getInstance()
    calendar.time = dateToCompare
    calendar.set(HOUR, 12)
    linearLayoutManager = LinearLayoutManager(this)

    appDatabase = AppDatabase.getDatabase(this)



    if (calendar.after(currentTime)) {
      main_views.setBackgroundColor(Color.parseColor("#06245F"))
    } else {
      main_views.setBackgroundColor(Color.parseColor("#06CDFF"))
    }

    /*val locationObserver = Observer<Location> {
        val weather = RestClient.getService()
                .getWeatherForLocation(it?.latitude, it?.longitude)
        weather.enqueue(success = {
        }, failure = {

        })

        val id = "524901,703448,2643743"
        val weatherByMultipleCities = RestClient.getService()
                .getWeatherMultipleCities(id, "metric")
        weatherByMultipleCities.enqueue(success = {
            Log.e("multiple", it.body().toString())
        }, failure = {

        })
    }*/



    if (Build.VERSION.SDK_INT >= 21) {
      val window = window
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.statusBarColor = Color.CYAN
    }


    countryAdapter = CountryAdapter(this)
    country_list.adapter = countryAdapter
    country_list.layoutManager = linearLayoutManager
    country_input.editText?.onChange {
      getAllCountry(it)
    }

    /*askPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
        onGranted {
            locationData.observe(this@MainActivity, locationObserver)
        }
    }*/

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
