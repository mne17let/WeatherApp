package com.example.weatherapp.Model.api.weatherModels.forecast

import com.google.gson.annotations.SerializedName

data class ForecastModel(
    @SerializedName("forecastday")
    val forecastday: List<ForecastDayModel>
)