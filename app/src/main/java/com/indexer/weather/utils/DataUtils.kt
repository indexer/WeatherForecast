package com.indexer.weather.utils

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.indexer.weather.model.Country
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList

class DataUtils(private val mContext: Context) {
    private val gson = Gson()

    fun loadAllCountries(): ArrayList<Country> {
        val countries = ArrayList<Country>()
        try {
            val json = mContext.assets.open("country_list.json")
            val parser = JsonParser()
            val reader = JsonReader(InputStreamReader(json))
            reader.isLenient = true

            val data = parser.parse(reader)
                    .asJsonArray

            for (element in data) {
                val country = gson.fromJson(element, Country::class.java)
                countries.add(country)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return countries
    }


    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: DataUtils? = null

        private fun getInstance(mContext: Context): DataUtils {
            if (instance == null) {
                instance = DataUtils(mContext)
            }
            return instance as DataUtils
        }
    }
}
