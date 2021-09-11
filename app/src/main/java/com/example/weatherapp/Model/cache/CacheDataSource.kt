package com.example.weatherapp.Model.cache

import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class CacheDataSource() {

    suspend fun addOrRemoveObject(realmLocationModel: RealmLocationModel): CacheAnswer{

        var resultAnswer: CacheAnswer

        withContext(Dispatchers.IO){

            try {
                val realm = Realm.getDefaultInstance()
                val listCity: List<RealmLocationModel> = realm.where(RealmLocationModel::class.java)
                    .equalTo("cityName", realmLocationModel.cityName).findAll()

                val listRegion = listCity.filter {
                    it.region == realmLocationModel.region
                }

                val oneModelByCountry = listRegion.find{
                    it.country == realmLocationModel.country
                }

                val alreadyExistsLocation = oneModelByCountry == null

                if(alreadyExistsLocation == true){
                    val transaction = object : Realm.Transaction{
                        override fun execute(realm: Realm) {
                            realm.insert(realmLocationModel)
                        }
                    }
                    realm.executeTransaction(transaction)

                    val updatedList = realm.where(RealmLocationModel::class.java).findAll()

                    if(updatedList.isEmpty()){
                        resultAnswer = CacheAnswer.SaveOrDeleteSuccess("Местоположение сохранено", emptyList())
                    } else{
                        val mutableList = mutableListOf<String>()

                        for(i in updatedList){
                            mutableList.add("${i.cityName}, ${i.region}, ${i.country}")
                        }

                        resultAnswer = CacheAnswer.SaveOrDeleteSuccess("Местоположение сохранено", mutableList)
                    }

                    realm.close()
                } else{
                    val transaction = object : Realm.Transaction{
                        override fun execute(realm: Realm) {
                            val listFlterByCity: List<RealmLocationModel> = realm.where(RealmLocationModel::class.java)
                                .equalTo("cityName", realmLocationModel.cityName).findAll()

                            val listFlterByRegion = listFlterByCity.filter {
                                it.region == realmLocationModel.region
                            }

                            val findNeccessaryObject = listFlterByRegion.find{
                                it.country == realmLocationModel.country
                            }

                            /*val newAlreadyExistsLocation =
                                realm.where(RealmLocationModel::class.java)
                                    .equalTo("cityName", realmLocationModel.cityName)
                                    .findFirst()*/

                            findNeccessaryObject?.deleteFromRealm()
                        }
                    }

                    realm.executeTransaction(transaction)

                    val updatedList = realm.where(RealmLocationModel::class.java).findAll()

                    if(updatedList.isEmpty()){
                        resultAnswer = CacheAnswer.SaveOrDeleteSuccess("Местоположение удалено", emptyList())
                    } else{
                        val mutableList = mutableListOf<String>()

                        for(i in updatedList){
                            mutableList.add("${i.cityName}, ${i.region}, ${i.country}")
                        }

                        resultAnswer = CacheAnswer.SaveOrDeleteSuccess("Местоположение удалено", mutableList)
                    }

                    realm.close()
                }
            } catch (e: Exception){
                val emptyData: List<String> = emptyList<String>()
                resultAnswer = CacheAnswer.SaveOrDeleteError(
                    "Ошибка сохранения или удаления. Выполнен код в блоке catch",
                    emptyData)

                print(e.stackTrace)
            }
        }

        return resultAnswer
    }

    suspend fun checkSave(city: String, region: String, country: String): Boolean{
        var resultAnswer: Boolean

        withContext(Dispatchers.IO){

            try {
                val realm = Realm.getDefaultInstance()

                val listFlterByCity: List<RealmLocationModel> = realm.where(RealmLocationModel::class.java)
                    .equalTo("cityName", city).findAll()

                val listFlterByRegion = listFlterByCity.filter {
                    it.region == region
                }

                val findObject = listFlterByRegion.find{
                    it.country == country
                }

                /*val alreadyExistsLocation: RealmLocationModel? = realm.where(RealmLocationModel::class.java)
                    .equalTo("idCityRegionCountry", id).findFirst()*/

                if(findObject == null){
                    resultAnswer = false
                    realm.close()
                } else{
                    resultAnswer = true
                    realm.close()
                }
            } catch (e: Exception){

                resultAnswer = false

                print(e.stackTrace)
            }
        }

        return resultAnswer
    }

    suspend fun getDataBaseData(): DataBaseAnswer{

        val result: DataBaseAnswer

        withContext(Dispatchers.IO){
            val locationsFromDB: RealmResults<RealmLocationModel> = Realm.getDefaultInstance()
                .where(RealmLocationModel::class.java).findAll()

            if(locationsFromDB.isEmpty()){
                result = DataBaseAnswer.EmptyData("База данных пуста")
            } else{
                val mutableList = mutableListOf<String>()

                for(i in locationsFromDB){
                    mutableList.add("${i.cityName}, ${i.region}, ${i.country}")
                }

                result = DataBaseAnswer.SuccessGetData("Успешно получены данные из базы данных", mutableList)
            }
        }

        return result
    }
}