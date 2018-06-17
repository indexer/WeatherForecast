package com.indexer.weather.base

import android.content.Context

import com.indexer.weather.R
import java.text.SimpleDateFormat
import java.util.Date

import java.util.Locale

object Utils {
  val DATE_FORMAT = "yyyyMMdd"

  fun formatTemperature(temperature: Double?): String {
    val s = "°F"
    val c = "°C"
    val f = (temperature!! - 273) * 9 / 5 + 32
    val degrees = temperature - 273.16
    val formatted = String.format(Locale.ENGLISH, "%.2f%S", degrees, c) + "/" + String.format(
        Locale.ENGLISH, "%.2f%S", f, s
    )

    return formatted
  }

  fun parseDate(date: String): Date {

    val inputFormat = "HH:mm a"
    val inputParser = SimpleDateFormat(inputFormat, Locale.getDefault())
    return try {
      inputParser.parse(date)
    } catch (e: java.text.ParseException) {
      Date(0)
    }
  }

  fun icon(weather: String?): Int {
    if (weather == "Clouds" || weather == "Haze") {
      return R.drawable.ic_cloud
    } else if (weather == "Rain") {
      return R.drawable.ic_rain
    } else if (weather == "Drizzle" || weather == "Thunderstorm") {
      return R.drawable.ic_hail
    }
    return R.drawable.ic_sun
  }

  fun getFormattedWind(
    context: Context,
    windSpeed: Double?,
    degrees: Float
  ): String {
    val windFormat: Int
    windFormat = R.string.format_wind_kmh
    // From wind direction in degrees, determine compass direction as a string (e.g NW)
    // You know what's fun, writing really long if/else statements with tons of possible
    // conditions.  Seriously, try it!
    var direction = "Unknown"
    if (degrees >= 337.5 || degrees < 22.5) {
      direction = "N"
    } else if (degrees >= 22.5 && degrees < 67.5) {
      direction = "NE"
    } else if (degrees >= 67.5 && degrees < 112.5) {
      direction = "E"
    } else if (degrees >= 112.5 && degrees < 157.5) {
      direction = "SE"
    } else if (degrees >= 157.5 && degrees < 202.5) {
      direction = "S"
    } else if (degrees >= 202.5 && degrees < 247.5) {
      direction = "SW"
    } else if (degrees >= 247.5 && degrees < 292.5) {
      direction = "W"
    } else if (degrees >= 292.5 || degrees < 22.5) {
      direction = "NW"
    }
    return String.format(context.getString(windFormat), windSpeed, direction)
  }
}
