package com.indexer.weather.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "country", indices = [(Index(value = ["name"],
        name = "name"))])
data class Country(
        @PrimaryKey
        val name: String,
        val alpha2Code: String?,
        val alpha3Code: String?,
        val capital: String?,
        val region: String?,
        val subregion: String?,
        val population: Int?,
        val demonym: String?,
        val area: Double?,
        val gini: Double?,
        val nativeName: String?,
        val numericCode: String?,
        val flag: String?,
        val cioc: String?
)