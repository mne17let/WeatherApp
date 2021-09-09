package com.example.weatherapp.Model.api.currentWeather.forecast

import com.google.gson.annotations.SerializedName

data class OneDayWeatherModel(
    @SerializedName("maxtemp_c")
    val maxtemp_c: Double,
    @SerializedName("mintemp_c")
    val mintemp_c: Double,
    @SerializedName("avgtemp_c")
    val avgtemp_c: Double,
    @SerializedName("condition")
    val one_day_forecast_condition: OneDayConditionModel
)

/*"maxtemp_c": 17.8,
                    "maxtemp_f": 64.0,
                    "mintemp_c": 4.5,
                    "mintemp_f": 40.1,
                    "avgtemp_c": 10.9,
                    "avgtemp_f": 51.6,
                    "maxwind_mph": 14.3,
                    "maxwind_kph": 23.0,
                    "totalprecip_mm": 0.5,
                    "totalprecip_in": 0.02,
                    "avgvis_km": 8.9,
                    "avgvis_miles": 5.0,
                    "avghumidity": 67.0,
                    "daily_will_it_rain": 1,
                    "daily_chance_of_rain": 88,
                    "daily_will_it_snow": 0,
                    "daily_chance_of_snow": 0,
                    "condition": {
                        "text": "Patchy rain possible",
                        "icon": "//cdn.weatherapi.com/weather/64x64/day/176.png",
                        "code": 1063
                    },
                    "uv": 3.0
                    */