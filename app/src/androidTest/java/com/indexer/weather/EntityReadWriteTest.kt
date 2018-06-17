package com.indexer.weather

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Room
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import com.indexer.weather.database.AppDatabase
import com.indexer.weather.database.WeatherDao
import com.indexer.weather.model.SaveWeather
import java.io.IOException
import java.util.ArrayList
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import java.util.Date

@RunWith(AndroidJUnit4::class)
class EntityReadWriteTest {
  private var mUserDao: WeatherDao? = null
  private var mDb: AppDatabase? = null

  @Before
  fun createDb() {
    val context = InstrumentationRegistry.getTargetContext()
    mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .build()
    mUserDao = mDb!!.weatherDao
  }

  @After
  @Throws(IOException::class)
  fun closeDb() {
    mDb!!.close()
  }

  @Test
  @Throws(Exception::class)
  fun writeUserAndReadInList() {

    val weather = SaveWeather(
        1,
        "Nay Pyi Daw", "Cloud",
        302.11, 1009.0, "", Date()
    )
    val mList = ArrayList<SaveWeather>()
    mList.add(weather)
    mUserDao!!.insertWeathers(mList)
    val mProduct = mUserDao!!.getAllWeatherById(10)
    assertThat(1, equalTo(mProduct.id))
    mUserDao!!.deleteSaveWeather(weather)
  }
}