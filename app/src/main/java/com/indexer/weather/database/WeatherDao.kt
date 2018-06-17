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

  @Query("SELECT * FROM weather ORDER BY saving_date")
  fun getAllSaveWeather(): LiveData<MutableList<SaveWeather>>

  @Delete
  fun deleteSaveWeather(user: SaveWeather?)

}