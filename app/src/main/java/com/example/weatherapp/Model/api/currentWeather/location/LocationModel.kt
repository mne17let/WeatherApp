package com.example.weatherapp.Model.api.currentWeather.location

import com.google.gson.annotations.SerializedName

data class LocationModel(
    @SerializedName("name")
    val name: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("country")
    val country: String
)


/*
"name": "Новосибирск",
"region": "Novosibirsk",
"country": "Россия",
"lat": 55.04,
"lon": 82.93,
"tz_id": "Asia/Novosibirsk",
"localtime_epoch": 1631210989,
"localtime": "2021-09-10 1:09"
*/
