package com.example.weatherapp.Model.cache

sealed class CacheAnswer {
    data class SaveOrDeleteSuccess(val message: String, val list: List<String>): CacheAnswer(){}
    data class SaveOrDeleteError(val message: String, val list: List<String>): CacheAnswer(){}
}

sealed class DataBaseAnswer{
    data class SuccessGetData(val message: String, val list: List<String>): DataBaseAnswer(){}
    data class EmptyData(val message: String): DataBaseAnswer(){}
}