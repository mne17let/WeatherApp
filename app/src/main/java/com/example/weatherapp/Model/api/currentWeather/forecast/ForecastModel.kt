package com.example.weatherapp.Model.api.currentWeather.forecast

import com.google.gson.annotations.SerializedName

data class ForecastModel(
    @SerializedName("forecastday")
    val forecastday: List<DaysForecastModel>
)