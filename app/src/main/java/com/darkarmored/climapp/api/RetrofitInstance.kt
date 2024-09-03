package com.darkarmored.climapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val baseUrl = "https://api.openweathermap.org/data/2.5/"

    private fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    val weatherApi: WeatherAPI = getInstance().create(WeatherAPI::class.java)
}

//https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}

//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}