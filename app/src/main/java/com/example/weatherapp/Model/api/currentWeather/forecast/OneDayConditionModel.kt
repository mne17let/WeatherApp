package com.example.weatherapp.Model.api.currentWeather.forecast

import com.google.gson.annotations.SerializedName

data class OneDayConditionModel(
    @SerializedName("text")
    val weather_text: String,
    @SerializedName("icon")
    val icon_url: String
)

/*
"text": "Patchy rain possible",
"icon": "//cdn.weatherapi.com/weather/64x64/day/176.png",
"code": 1063
    */