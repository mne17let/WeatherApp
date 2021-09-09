package com.example.weatherapp.Model.api.weatherModels.forecast

import com.google.gson.annotations.SerializedName

data class DaysForecastModel(
    @SerializedName("date")
    val date: String,
    @SerializedName("day")
    val day: OneDayWeatherModel
)

/*
"date": "2021-09-10",
                "date_epoch": 1631232000,
                "day": { }
*/