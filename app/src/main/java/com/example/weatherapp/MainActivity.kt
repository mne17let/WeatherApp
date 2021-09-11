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
    }

    private fun init(){
        activityViewModel = (application as MyApplication).viewModel
        setWeatherLiveData()
    }

    private fun setStartFragment(){
        val weatherFragment = WeatherFragment()
        val bundle = Bundle()
        bundle.putString(KEY_FOR_WEATHER_FRAGMENT, cacheLocationString)

        weatherFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.id_fragment_container, weatherFragment)
            .commit()
    }

    fun openSearchFragment(){
        val TAG_SEARCH_FRAGMENT = "SearchFragment"

        supportFragmentManager.beginTransaction()
            .replace(R.id.id_fragment_container, SearchFragment()).addToBackStack(TAG_SEARCH_FRAGMENT).commit()
    }

    fun openNewFoundLocationFragment(coordinatesOrLocationName: String){
        val weatherFragment = WeatherFragment()
        val bundle = Bundle()
        cacheLocationString = coordinatesOrLocationName
        bundle.putString(KEY_FOR_WEATHER_FRAGMENT, coordinatesOrLocationName)

        weatherFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.id_fragment_container, weatherFragment)
            .commit()
    }

    fun openNewSearchLocationFragment(coordinatesOrLocationName: String, view: View){
        val weatherFragment = WeatherFragment()
        val bundle = Bundle()
        cacheLocationString = coordinatesOrLocationName
        bundle.putString(KEY_FOR_WEATHER_FRAGMENT, coordinatesOrLocationName)

        weatherFragment.arguments = bundle

        hideKeyboardFrom(view)

        supportFragmentManager.beginTransaction()
            .replace(R.id.id_fragment_container, weatherFragment)
            .commit()
    }

    private fun hideKeyboardFrom(view: View) {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0);
    }

    private fun setWeatherLiveData(){
        activityViewModel.cloudLiveData.observe(this){
            if(it is WeatherViewModel.LiveDataState.Weather){
                cacheLocationString = it.currentWeather.location.name
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