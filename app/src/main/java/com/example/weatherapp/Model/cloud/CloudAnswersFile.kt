package com.example.weatherapp.Model.cloud

import com.example.weatherapp.Model.api.weatherModels.ResponseForecast

sealed class CloudAnswer {
    data class Error(val errorType: CloudError, val message: String) : CloudAnswer()
    data class Success(val data: ResponseForecast) : CloudAnswer()
}

enum class CloudError {
    API_KEY_NOT_PROVIDED,
    WRONG_Q,
    API_REQUEST_IS_INVALID,
    NO_LOCATION_FOUND,
    API_KEY_PROVIDED_IS_INVALID,
    API_KEY_HAS_EXCEEDED_CALLS_PER_MONTH_QUOTA,
    API_KEY_HAS_BEEN_DISABLED,
    INTERNAL_ERROR,
    NO_TYPE_ERROR
}