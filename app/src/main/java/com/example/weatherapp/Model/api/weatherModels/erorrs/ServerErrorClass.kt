package com.example.weatherapp.Model.api.weatherModels

import com.example.weatherapp.Model.api.weatherModels.erorrs.ServerErrorModel
import com.google.gson.annotations.SerializedName

data class ServerErrorResponseModel(
    @SerializedName("error")
    val serverErrorModel: ServerErrorModel
    )