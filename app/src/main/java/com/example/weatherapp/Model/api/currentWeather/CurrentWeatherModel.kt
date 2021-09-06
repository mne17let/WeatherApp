package com.example.weatherapp.Model.api.currentWeather

import com.google.gson.annotations.SerializedName

data class CurrentWeatherModel(
    @SerializedName("last_updated_epoch")
    val last_updated_epoch: Long,
    @SerializedName("last_updated")
    val last_updated: String,
    @SerializedName("temp_c")
    val temp_c: Double,
    @SerializedName("temp_f")
    val temp_f: Double,
    @SerializedName("is_day")
    val is_day: Int,
    @SerializedName("condition")
    val condition: CurrentWeatherConditionModel
)