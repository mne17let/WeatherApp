package com.example.weatherapp.Model.api.currentWeather

import com.google.gson.annotations.SerializedName

data class CurrentWeatherLocationModel(
    @SerializedName("name")
    val name: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("country")
    val country: String
)