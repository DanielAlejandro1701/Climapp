package com.darkarmored.climapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherAPI {

    @GET("weather")
    suspend fun getWeather(
        @Query("appid") appid: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units:String,
        @Query("lang") lang: String
    ): Response<WeatherModel>


    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("appid") appid: String,
        @Query("q") city: String,
        @Query("units") units:String,
        @Query("lang") lang: String
    ): Response<WeatherModel>

}

///https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}

//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}