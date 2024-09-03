package com.darkarmored.climapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.lang.ref.WeakReference

/**
 * Obtiene la localizacion segun Latitud y Longitud en tiempo real
 */
object LocationManager  {

    private lateinit var activity: WeakReference<ComponentActivity>
    private lateinit var locationRequest: LocationRequest

    private var onUpdateLocation: WeakReference<(latitude: Double, longitude: Double) -> Unit>? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)

            if (!locationAvailability.isLocationAvailable) {

                activity.get()?.let {
                    goSettingScreen(it)

                    removeCallback(it)
                }
            }
        }

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            onUpdateLocation?.get()?.invoke(
                locationResult.lastLocation?.latitude ?:0.0,
                locationResult.lastLocation?.longitude ?: 0.0
                )
        }
    }

    object Builder {

        fun build(): Builder {
            return this
        }

        fun create(activity: ComponentActivity): LocationManager {
            LocationManager.activity = WeakReference(activity)
            locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).apply {
                setMinUpdateIntervalMillis(1000)
                setMaxUpdateAgeMillis(0)
                setMinUpdateDistanceMeters(1.0f)
                setGranularity(Granularity.GRANULARITY_FINE)
                setWaitForAccurateLocation(true)
            }.build()

            return LocationManager
        }
    }

    fun goSettingScreen(activity: Activity){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        activity.startActivity(intent)
    }



    @SuppressLint("MissingPermission")
    fun request(onUpdateLocation: ((latitude:Double, longitude: Double) -> Unit)? = null ){

        LocationManager.onUpdateLocation = WeakReference(onUpdateLocation)

        activity.get()?.let {
            LocationServices.getFusedLocationProviderClient(it).requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }

    }

    fun removeCallback(activity: Activity){
        LocationServices.getFusedLocationProviderClient(activity).flushLocations()
        LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(
            locationCallback
        )
    }
}