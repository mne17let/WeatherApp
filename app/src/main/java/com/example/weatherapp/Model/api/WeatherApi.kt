package com.example.weatherapp.Model.api

import com.example.weatherapp.Model.api.searchModels.NewLocation
import com.example.weatherapp.Model.api.weatherModels.ResponseForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("https://api.weatherapi.com/v1/current.json?key=239195032ccc4c2ba7b155431210509")
    suspend fun getCurrentWeather(@Query("q") cityNameOrLongLat: String): Response<ResponseForecast>

    @GET("https://api.weatherapi.com/v1/forecast.json?key=239195032ccc4c2ba7b155431210509&days=3")
    suspend fun getForecast(@Query("q") cityNameOrLongLat: String): Response<ResponseForecast>

    @GET("https://api.weatherapi.com/v1/search.json?key=239195032ccc4c2ba7b155431210509")
    suspend fun search(@Query("q") text: String): Response<List<NewLocation>>
}