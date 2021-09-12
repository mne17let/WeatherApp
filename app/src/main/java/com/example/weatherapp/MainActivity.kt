package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.View.WeatherFragment
import com.example.weatherapp.View.savelist_recyclerview.MyInterfaceForListAdapter
import com.example.weatherapp.View.savelist_recyclerview.SaveListAdapter
import com.example.weatherapp.View.search.SearchFragment
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity: AppCompatActivity() {

    private var cacheLocationString: String? = null

    private val TAG_ACTIVITY = "MyMainActivity"
    private val KEY_FOR_WEATHER_FRAGMENT = "CacheOrNull"
    private val KEY_FOR_WEATHER_FRAGMENT_IS_OPEN_FROM_TEXT_SEARCH = "IsOpenFromTextSearch"
    private val TAG_WEATHER_FRAGMENT = "WeatherFragment"

    private lateinit var sharedPreferences: SharedPreferences

    lateinit var activityViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setCache()
        init()
        setStartFragment()
    }

    private fun setCache(){
        sharedPreferences = getSharedPreferences(TAG_ACTIVITY, MODE_PRIVATE)
        cacheLocationString = sharedPreferences.getString(TAG_ACTIVITY, null)
        Log.d(TAG_ACTIVITY, "В активити при запуске получен кэш: $cacheLocationString")
    }

    private fun init(){
        activityViewModel = (application as MyApplication).viewModel
        setWeatherLiveData()
    }

    private fun setStartFragment(){
        val weatherFragment = WeatherFragment()
        val bundle = Bundle()
        bundle.putString(KEY_FOR_WEATHER_FRAGMENT, cacheLocationString)

        Log.d(TAG_ACTIVITY, "В активити при запуске запускается стартовый фрагмент с данными: $cacheLocationString")

        weatherFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.id_fragment_container, weatherFragment, TAG_WEATHER_FRAGMENT)
            .commit()
    }

    fun openSearchFragment(){
        val TAG_SEARCH_FRAGMENT = "SearchFragment"

        supportFragmentManager.beginTransaction()
            .replace(R.id.id_fragment_container, SearchFragment()).addToBackStack(TAG_SEARCH_FRAGMENT).commit()
    }

    fun openNewSearchLocationFragment(coordinatesOrLocationName: String, view: View){

        val newFragment = WeatherFragment()
        val bundle = Bundle()
        val isOpenFromTextSearch = true

        cacheLocationString = coordinatesOrLocationName

        bundle.putString(KEY_FOR_WEATHER_FRAGMENT, coordinatesOrLocationName)
        bundle.putBoolean(KEY_FOR_WEATHER_FRAGMENT_IS_OPEN_FROM_TEXT_SEARCH, isOpenFromTextSearch)
        newFragment.arguments = bundle

        hideKeyboardFrom(view)

        //supportFragmentManager.beginTransaction().remove(newOrExistsFragment).commit()

        //supportFragmentManager.popBackStack()

        Log.d(TAG_ACTIVITY, "Активити. Размер бэкстека: ${supportFragmentManager.backStackEntryCount}")

        Log.d(TAG_ACTIVITY, "Активити. Стартовал новый фрагмент с данными: ${cacheLocationString}, $isOpenFromTextSearch")

        supportFragmentManager.beginTransaction()
            .replace(R.id.id_fragment_container, newFragment, TAG_WEATHER_FRAGMENT)
            .commit()
    }

    private fun hideKeyboardFrom(view: View) {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0);
    }

    private fun setWeatherLiveData(){
        activityViewModel.cloudLiveData.observe(this){
            if(it is WeatherViewModel.LiveDataState.Weather){
                val cityName = it.currentWeather.location.name
                val region = it.currentWeather.location.region
                val country = it.currentWeather.location.country

                cacheLocationString = "$cityName, $region, $country"
                Log.d(TAG_ACTIVITY, "Обновление лайвдаты с погодой. Кэш в активити: $cityName, $region, $country")
            }
        }
    }

    override fun onStop() {
        super.onStop()

        val editor = sharedPreferences.edit()
        editor.putString(TAG_ACTIVITY, cacheLocationString)
        editor.apply()
    }
}