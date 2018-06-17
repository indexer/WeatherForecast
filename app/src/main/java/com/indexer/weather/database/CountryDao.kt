package com.indexer.weather.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query
import com.indexer.weather.model.Country

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCountry(products: List<Country>)

    @Query("SELECT * FROM country WHERE name LIKE  :countryName")
    fun findByCountryName(countryName: String): List<Country>

    @Query("SELECT * FROM country")
    fun findByCountry(): List<Country>
}