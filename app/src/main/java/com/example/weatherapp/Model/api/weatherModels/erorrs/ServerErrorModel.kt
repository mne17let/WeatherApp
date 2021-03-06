package com.example.weatherapp.Model.api.weatherModels.erorrs

import com.google.gson.annotations.SerializedName

data class ServerErrorModel(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)