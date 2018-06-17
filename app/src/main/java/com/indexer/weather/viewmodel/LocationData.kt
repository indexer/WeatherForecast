package com.indexer.weather.viewmodel

import android.annotation.SuppressLint
import android.os.Bundle
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.location.LocationServices

class LocationData(private var context: Context) :
    LiveData<Location>(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

  private val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(context, this, this)
      .addApi(LocationServices.API)
      .build()

  lateinit var locationRequest: LocationRequest

  private lateinit var locationCallback: LocationCallback

  @SuppressLint("MissingPermission")
  override fun onConnected(p0: Bundle?) {

    LocationServices.getFusedLocationProviderClient(context)
        .lastLocation.addOnSuccessListener {
      value = it
    }
    // Request updates if there’s someone observing
    if (hasActiveObservers()) {
      locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
          locationResult ?: return
          for (location in locationResult.locations) {
            value = location
          }
        }
      }

      locationRequest = LocationRequest.create()
          .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
          .setInterval(1000 * 10)
          .setFastestInterval(1000 * 20)

      LocationServices.getFusedLocationProviderClient(context)
          .requestLocationUpdates(
              locationRequest,
              locationCallback,
              null
          )
    }
  }

  override fun onActive() {
    googleApiClient.connect()
  }

  override fun onInactive() {
    if (googleApiClient.isConnected) {
      locationCallback = object : LocationCallback() {}
      LocationServices.getFusedLocationProviderClient(context)
          .removeLocationUpdates(locationCallback)
    }
    googleApiClient.disconnect()
  }

  override fun onLocationChanged(location: Location) {
    // Deliver the location changes
    Log.e("current location", "" + location.latitude)
    value = location
  }

  override fun onConnectionSuspended(cause: Int) {
    // Cry softly, hope it comes back on its own
  }

  override fun onConnectionFailed(
    @NonNull connectionResult: ConnectionResult
  ) {

  }
}