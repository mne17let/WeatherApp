package com.example.weatherapp.Model.api.weatherModels

import com.google.gson.annotations.SerializedName

data class ConditionModel(
    @SerializedName("text")
    val weather_text: String,
    @SerializedName("icon")
    val icon_url: String
)

/*
"text": "Clear",
"icon": "//cdn.weatherapi.com/weather/64x64/night/113.png",
"code": 1000
            */