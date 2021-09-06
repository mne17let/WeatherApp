package com.example.weatherapp.Model.api.currentWeather

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponseModel(
    @SerializedName("location")
    val location: CurrentWeatherLocationModel,
    @SerializedName("current")
    val current: CurrentWeatherModel
)