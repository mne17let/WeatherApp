package com.example.weatherapp.Model.api.currentWeather

import com.example.weatherapp.Model.api.currentWeather.current.CurrentModel
import com.example.weatherapp.Model.api.currentWeather.forecast.DaysForecastModel
import com.example.weatherapp.Model.api.currentWeather.forecast.OneDayWeatherModel
import com.example.weatherapp.Model.api.currentWeather.location.LocationModel
import com.google.gson.annotations.SerializedName

data class ResponseForecast(
    @SerializedName("location")
    val location: LocationModel,
    @SerializedName("current")
    val current: CurrentModel,
    @SerializedName("forecast")
    val forecast: List<DaysForecastModel>
)