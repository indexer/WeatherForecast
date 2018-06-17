package com.indexer.weather.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.indexer.weather.base.Converters
import com.indexer.weather.model.Country
import com.indexer.weather.model.SaveWeather
import com.indexer.weather.utils.DataUtils
import com.indexer.weather.utils.ioThread

@Database(entities = [(Country::class), (SaveWeather::class)], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

  abstract val countryDao: CountryDao
  abstract val weatherDao: WeatherDao

  companion object {

    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      when (INSTANCE) {
        null -> {
          val countrylist = DataUtils(context).loadAllCountries()
          INSTANCE = Room.databaseBuilder(
              context.applicationContext,
              AppDatabase::class.java,
              "product_db"
          )
              .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                  super.onCreate(db)
                  // insert the data on the IO Thread
                  ioThread {
                    getDatabase(context).countryDao.insertAllCountry(countrylist)
                  }
                }
              })
              .allowMainThreadQueries()
              .build()

        }
      }
      return INSTANCE as AppDatabase
    }

    fun destroyInstance() {
      INSTANCE = null
    }
  }
}