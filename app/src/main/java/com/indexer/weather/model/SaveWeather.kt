package com.indexer.weather.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weather")
data class SaveWeather(
  @PrimaryKey
  val id: Int?,
  val country: String,
  val main: String?,
  val temp: Double?,
  val pressure: Double?,
  val description: String,
  val saving_date: Date? = null
)