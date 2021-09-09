package com.example.weatherapp.Model.api.weatherModels.current

import com.example.weatherapp.Model.api.weatherModels.ConditionModel
import com.google.gson.annotations.SerializedName

data class CurrentModel(
    @SerializedName("temp_c")
    val temp_c: Double,
    @SerializedName("condition")
    val condition: ConditionModel
)

/*"last_updated_epoch": 1631206800,
        "last_updated": "2021-09-10 00:00",
        "temp_c": 8.0,
        "temp_f": 46.4,
        "is_day": 0,
        "condition": {
            "text": "Clear",
            "icon": "//cdn.weatherapi.com/weather/64x64/night/113.png",
            "code": 1000
            "wind_mph": 11.9,
        "wind_kph": 19.1,
        "wind_degree": 240,
        "wind_dir": "WSW",
        "pressure_mb": 1010.0,
        "pressure_in": 29.83,
        "precip_mm": 0.0,
        "precip_in": 0.0,
        "humidity": 93,
        "cloud": 0,
        "feelslike_c": 5.3,
        "feelslike_f": 41.5,
        "vis_km": 10.0,
        "vis_miles": 6.0,
        "uv": 1.0,
        "gust_mph": 16.6,
        "gust_kph": 26.6
            */