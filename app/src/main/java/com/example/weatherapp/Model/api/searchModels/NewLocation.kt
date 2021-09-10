package com.example.weatherapp.Model.api.searchModels

import com.google.gson.annotations.SerializedName

data class NewLocation(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("country")
    val country: String,
)


/*
* "id": 2447558,
        "name": "Kurtalan, Siirt, Turkey",
        "region": "Siirt",
        "country": "Turkey",
        "lat": 37.92,
        "lon": 41.7,
        "url": "kurtalan-siirt-turkey"*/