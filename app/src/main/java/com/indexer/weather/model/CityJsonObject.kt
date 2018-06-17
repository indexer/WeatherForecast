package com.indexer.weather.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "city")
data class CityJsonObject(
  @PrimaryKey
  val id: Int,
  val name: String,
  val country: String
)