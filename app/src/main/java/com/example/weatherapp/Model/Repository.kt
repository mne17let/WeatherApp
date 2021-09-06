package com.example.weatherapp.Model

import android.util.Log
import com.example.weatherapp.Model.api.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val weatherApi: WeatherApi) {

    private val TAG_REPOSITORY = "MyRepository"

    suspend fun getCurrentWeatherFromRepository(cityName: String): MutableList<String>{

        val newList: MutableList<String> = mutableListOf()

        withContext(Dispatchers.IO){
            val result = weatherApi.getCurrentWeather(cityName).execute()

            val code = result.code()
            val body = result.body()

            if (body != null){
                val newCityName = body.location.name
                val newTemperature = body.current.temp_c
                val newIconUrl = body.current.condition.icon_url

                newList.add(newCityName)
                newList.add(newTemperature.toString())
                newList.add(newIconUrl)
            }

            Log.d(TAG_REPOSITORY, "Код ответа: ${code}")
            Log.d(TAG_REPOSITORY, "Получен ответ: ${result}")
        }

        return newList
    }

}