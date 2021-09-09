package com.example.weatherapp.Model.api

import com.example.weatherapp.Model.api.currentWeather.CurrentWeatherResponseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("https://api.weatherapi.com/v1/current.json?key=239195032ccc4c2ba7b155431210509")
    fun getCurrentWeather(@Query("q") cityName: String): Call<CurrentWeatherResponseModel>
}