package com.example.weatherapp.Model.api.currentWeather

import com.example.weatherapp.Model.api.currentWeather.erorrs.ServerErrorModel
import com.google.gson.annotations.SerializedName

data class ServerErrorResponseModel(
    @SerializedName("error")
    val serverErrorModel: ServerErrorModel
    )