package com.example.weatherapp.Model.api.currentWeather

import com.google.gson.annotations.SerializedName

data class CurrentWeatherConditionModel(
    @SerializedName("text")
    val weather_text: String,
    @SerializedName("icon")
    val icon_url: String
)