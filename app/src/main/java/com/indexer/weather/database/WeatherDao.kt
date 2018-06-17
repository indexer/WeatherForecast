package com.indexer.weather.database;

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query
import com.indexer.weather.model.SaveWeather
import android.arch.persistence.room.Delete

@Dao
interface WeatherDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertWeather(saveWeather: SaveWeather)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertWeathers(saveWeather: List<SaveWeather>)

  @Query("SELECT * FROM weather ORDER BY saving_date")
  fun getAllSaveWeather(): LiveData<MutableList<SaveWeather>>

  @Query("SELECT id FROM weather ORDER BY saving_date")
  fun getAllCityId(): IntArray

  @Delete
  fun deleteSaveWeather(user: SaveWeather?)

}