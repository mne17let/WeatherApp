package com.example.weatherapp.Model.api.weatherModels

import com.example.weatherapp.Model.api.weatherModels.current.CurrentModel
import com.example.weatherapp.Model.api.weatherModels.forecast.ForecastModel
import com.example.weatherapp.Model.api.weatherModels.location.LocationModel
import com.google.gson.annotations.SerializedName

data class ResponseForecast(
    @SerializedName("location")
    val location: LocationModel,
    @SerializedName("current")
    val current: CurrentModel,
    @SerializedName("forecast")
    val forecast: ForecastModel
)