package com.indexer.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.WindowManager
import com.indexer.weather.adapter.CountryAdapter
import com.indexer.weather.base.BaseViewHolder
import com.indexer.weather.base.Config
import com.indexer.weather.base.Utils
import com.indexer.weather.database.AppDatabase
import com.indexer.weather.utils.onChange
import kotlinx.android.synthetic.main.activity_home.main_view
import kotlinx.android.synthetic.main.activity_main.close_country
import kotlinx.android.synthetic.main.activity_main.country_input
import kotlinx.android.synthetic.main.activity_main.country_list
import kotlinx.android.synthetic.main.activity_main.main_views
import java.util.Calendar

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

  private lateinit var appDatabase: AppDatabase
  private lateinit var countryAdapter: CountryAdapter
  private lateinit var linearLayoutManager: LinearLayoutManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)


    linearLayoutManager = LinearLayoutManager(this)

    appDatabase = AppDatabase.getDatabase(this)
    checkDayAndNight()

    close_country.setOnClickListener {
      this.finish()
    }

    countryAdapter = CountryAdapter(this)
    country_list.adapter = countryAdapter
    country_list.layoutManager = linearLayoutManager
    country_input.editText?.onChange {
      getAllCountry(it)
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

  private fun checkDayAndNight() {
    val now = Calendar.getInstance()
    val hour = now.get(Calendar.HOUR_OF_DAY) // Get hour in 24 hour format
    val minute = now.get(Calendar.MINUTE)
    val date = Utils.parseDate(hour.toString() + ":" + minute)
    val dateCompare = Utils.parseDate("18:00 PM")
    val dateCompareTwo = Utils.parseDate("07:00 AM")


    if (date.after(dateCompareTwo) || date.before(dateCompare)) {
      main_views.setBackgroundColor(Color.parseColor("#06CDFF"))
      statusColor("#06CDFF")
    } else {
      main_views.setBackgroundColor(Color.parseColor("#06245F"))
      statusColor("#06245F")

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
