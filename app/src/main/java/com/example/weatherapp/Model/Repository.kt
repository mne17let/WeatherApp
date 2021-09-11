package com.example.weatherapp.Model

import android.util.Log
import com.example.weatherapp.Model.api.weatherModels.ResponseForecast
import com.example.weatherapp.Model.cache.CacheAnswer
import com.example.weatherapp.Model.cache.CacheDataSource
import com.example.weatherapp.Model.cache.DataBaseAnswer
import com.example.weatherapp.Model.cache.RealmLocationModel
import com.example.weatherapp.Model.cloud.Cloud
import com.example.weatherapp.Model.cloud.CloudAnswer
import com.example.weatherapp.Model.cloud.CloudError

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
                val isSaved = cache.checkSave(location.name, location.region, location.country)

                Log.d(TAG_REPOSITORY, "В репозитории сохранено: ${isSaved}")

                return  RepositoryResult.CloudSuccessRepositoryResult(location, current, forecast, isSaved)
            }
            else -> return RepositoryResult.CloudErrorRepositoryResult("Непонятный ответ от репозитория", CloudError.NO_TYPE_ERROR)
        }
    }

    suspend fun addOrRemoveLocation(): RepositoryResult {
        val realmObject = RealmLocationModel()
        val var_cityName = cachedCityWeather?.location?.name.toString()
        val var_region = cachedCityWeather?.location?.region.toString()
        val var_country = cachedCityWeather?.location?.country.toString()

        realmObject.cityName = var_cityName
        realmObject.region = var_region
        realmObject.country = var_country

        Log.d(TAG_REPOSITORY, "В репозитории удаляю: ${cachedCityWeather}")

        val cacheAnswer = cache.addOrRemoveObject(realmObject)

        val repositoryResult: RepositoryResult

        when(cacheAnswer){
            is CacheAnswer.SaveOrDeleteSuccess -> {
                repositoryResult =
                    RepositoryResult.CacheRepositoryResult(cacheAnswer.message, false, cacheAnswer.list)

                Log.d(TAG_REPOSITORY, "В репозитории список сохранённых: ${cacheAnswer.list}")
                Log.d(TAG_REPOSITORY, "В репозитории кэш: $cachedCityWeather")
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