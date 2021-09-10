package com.example.weatherapp.Model

import android.util.Log
import com.example.weatherapp.Model.api.weatherModels.ResponseForecast
import com.example.weatherapp.Model.cache.CacheAnswer
import com.example.weatherapp.Model.cache.CacheDataSource
import com.example.weatherapp.Model.cache.DataBaseAnswer
import com.example.weatherapp.Model.cache.RealmLocationModel
import com.example.weatherapp.Model.cloud.Cloud
import com.example.weatherapp.Model.cloud.CloudAnswer

class Repository(private val cloud: Cloud, private val cache: CacheDataSource) {
    //private val cloud = Cloud()

    private val TAG_REPOSITORY = "MyRepository"

    private var cachedCityWeather: ResponseForecast? = null

    suspend fun getWeatherFromRepository(searchText: String): RepositoryResult{
        val searchResult = cloud.getWeather(searchText)
        when(searchResult){
            is CloudAnswer.Error ->{
                cachedCityWeather = null
                return RepositoryResult.CloudErrorRepositoryResult(searchResult.message, searchResult.errorType)
            }
            is CloudAnswer.Success ->{
                cachedCityWeather = searchResult.data
                val current = searchResult.data.current
                val location = searchResult.data.location
                val forecast = searchResult.data.forecast.forecastday
                val isSaved = cache.checkSave(getLocationId())

                return  RepositoryResult.CloudSuccessRepositoryResult(location, current, forecast, isSaved)
            }
        }
    }

    suspend fun addOrRemoveLocation(): RepositoryResult {
        val realmObject = RealmLocationModel()
        val cityName = cachedCityWeather?.location?.name.toString()

        realmObject.cityName = cityName
        realmObject.idCityRegionCountry = getLocationId()

        val cacheAnswer = cache.addOrRemoveObject(realmObject)

        val repositoryResult: RepositoryResult

        when(cacheAnswer){
            is CacheAnswer.SaveOrDeleteSuccess -> {
                repositoryResult =
                    RepositoryResult.CacheRepositoryResult(cacheAnswer.message, false, cacheAnswer.list)
            }
            is CacheAnswer.SaveOrDeleteError -> {
                repositoryResult = RepositoryResult.CacheRepositoryResult(cacheAnswer.message, true, cacheAnswer.list)
            }
        }

        return repositoryResult
    }

    suspend fun getLocationsList(): RepositoryResult.GetCacheListRepositoryAnswer{
        val answer = cache.getDataBaseData()

        val repositoryAnswer: RepositoryResult.GetCacheListRepositoryAnswer

        when(answer){
            is DataBaseAnswer.EmptyData ->{
                repositoryAnswer = RepositoryResult.GetCacheListRepositoryAnswer(answer.message, emptyList())
            }

            is DataBaseAnswer.SuccessGetData ->{
                val cacheList = answer.list

                repositoryAnswer = RepositoryResult.GetCacheListRepositoryAnswer(answer.message, cacheList)
            }
        }
        return repositoryAnswer
    }

    suspend fun repositorySearch(text: String): MutableList<String>{
        val result = cloud.searchOnApi(text)

        Log.d(TAG_REPOSITORY, "Во репозитории выполнен поиск с текстом: $text")
        Log.d(TAG_REPOSITORY, "Во репозитории получен результат: $result")

        val mutableList: MutableList<String> = mutableListOf()

        if(result.isNotEmpty()){
            for(i in result){
                mutableList.add(i.name)
            }
        }

     return mutableList
    }

    private fun getLocationId(): String{
        val cityName = cachedCityWeather?.location?.name.toString()
        val region = cachedCityWeather?.location?.region.toString().lowercase()
        val country = cachedCityWeather?.location?.country.toString().lowercase()


        return "${cityName.lowercase()}$region$country"
    }



}