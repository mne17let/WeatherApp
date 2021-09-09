package com.example.weatherapp.Model.api

import com.example.weatherapp.Model.api.currentWeather.ResponseForecast
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("https://api.weatherapi.com/v1/current.json?key=239195032ccc4c2ba7b155431210509")
    fun getCurrentWeather(@Query("q") cityNameOrLongLat: String): Response<ResponseForecast>

    @GET("https://api.weatherapi.com/v1/forecast.json?key=239195032ccc4c2ba7b155431210509&days=7")
    fun getForecast(@Query("q") cityNameOrLongLat: String): Response<ResponseForecast>
}