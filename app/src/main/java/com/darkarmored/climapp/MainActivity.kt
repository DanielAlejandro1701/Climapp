package com.darkarmored.climapp

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.darkarmored.climapp.ui.theme.ClimappTheme

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()

    /**
     * Solicitud de permisos de localizacion
     */
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                LocationManager.Builder.create(this@MainActivity)
                    .request(onUpdateLocation = { latitude: Double, longitude: Double ->

                        LocationManager.removeCallback(this@MainActivity)
                        weatherViewModel.latitude.value = latitude
                        weatherViewModel.longitude.value = longitude
                    })
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) ->{
                LocationManager.Builder.create(this@MainActivity)
                    .request(onUpdateLocation = { latitude: Double, longitude: Double ->

                        LocationManager.removeCallback(this@MainActivity)
                        weatherViewModel.latitude .value = latitude
                        weatherViewModel.longitude.value = longitude
                    })
            }
            else -> {
                LocationManager.goSettingScreen(this)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClimappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    locationPermissionRequest.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                    MainPage(weatherViewModel)
                }
            }
        }
    }
}

