package com.example.weatherapp.Model.cache

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmLocationModel: RealmObject() {
    @PrimaryKey
    var idCityRegionCountry: String = ""
    var cityName: String = ""
}