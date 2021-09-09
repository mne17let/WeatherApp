package com.example.weatherapp.Model

import com.example.weatherapp.Model.api.weatherModels.current.CurrentModel
import com.example.weatherapp.Model.api.weatherModels.forecast.DaysForecastModel
import com.example.weatherapp.Model.api.weatherModels.location.LocationModel
import com.example.weatherapp.Model.cloud.Cloud

class Repository() {
    private val cloud = Cloud()

    private val TAG_REPOSITORY = "MyRepository"

    suspend fun getWeatherFromRepository(searchText: String): RepositoryResult{
        val searchResult = cloud.getWeather(searchText)
        when(searchResult){
            is Cloud.CloudAnswer.Error ->
                return RepositoryResult.ErrorRepositoryResult(searchResult.message, searchResult.errorType)
            is Cloud.CloudAnswer.Success ->{
                val current = searchResult.data.current
                val location = searchResult.data.location
                val forecast = searchResult.data.forecast.forecastday


                return  RepositoryResult.SuccessRepositoryResult(location, current, forecast)
            }
        }
    }

    sealed class RepositoryResult{
        data class SuccessRepositoryResult(
            val location: LocationModel,
            val current: CurrentModel,
            val forecast: List<DaysForecastModel>
            ): RepositoryResult()

        data class ErrorRepositoryResult(
            val message: String,
            val type: Cloud.CloudError
        ): RepositoryResult()
    }

}