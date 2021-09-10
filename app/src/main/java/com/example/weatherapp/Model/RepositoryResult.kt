package com.example.weatherapp.Model

import com.example.weatherapp.Model.api.weatherModels.current.CurrentModel
import com.example.weatherapp.Model.api.weatherModels.forecast.ForecastDayModel
import com.example.weatherapp.Model.api.weatherModels.location.LocationModel
import com.example.weatherapp.Model.cache.RealmLocationModel
import com.example.weatherapp.Model.cloud.CloudError

sealed class RepositoryResult{
    data class CloudSuccessRepositoryResult(
        val location: LocationModel,
        val current: CurrentModel,
        val forecast: List<ForecastDayModel>,
        val isSaved: Boolean
    ): RepositoryResult()

    data class CloudErrorRepositoryResult(
        val message: String,
        val type: CloudError
    ): RepositoryResult()

    data class CacheRepositoryResult(
        val message: String,
        val isError: Boolean,
        val newList: List<String>
    ): RepositoryResult()

    data class GetCacheListRepositoryAnswer(
        val message: String,
        val data: List<String>
    ): RepositoryResult()
}