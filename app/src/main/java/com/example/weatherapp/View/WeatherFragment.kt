package com.example.weatherapp.View

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.MainActivity
import com.example.weatherapp.Model.api.weatherModels.forecast.ForecastDayModel
import com.example.weatherapp.R
import com.example.weatherapp.View.forecast_recyclerview.ForecastAdapter
import com.example.weatherapp.View.savelist_recyclerview.MyInterfaceForListAdapter
import com.example.weatherapp.View.savelist_recyclerview.SaveListAdapter
import com.example.weatherapp.ViewModel.WeatherViewModel
import kotlin.math.hypot

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private val TAG_WEATHER_FRAGMENT = "MyWeatherFragment"

    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGrantedMap ->

            for (key in isGrantedMap.keys) {
                if (isGrantedMap[key] == false) {

                }
            }
        }

    private lateinit var burger: ImageButton
    private lateinit var textViewCityName: TextView
    private lateinit var textViewCurrentTemperature: TextView
    private lateinit var imageViewWeatherIcon: ImageView

    //private lateinit var maxTempTextView: TextView
    //private lateinit var minTempTextView: TextView
    private lateinit var currentDescriptionTextView: TextView
    private lateinit var saveDeleteButton: ImageButton
    private lateinit var textViewSaveOrDelete: TextView



    // RecyclerView
    private lateinit var recyclerViewForecast: RecyclerView
    private lateinit var adapter: ForecastAdapter

    private lateinit var viewModel: WeatherViewModel

/*

    private lateinit var locationManager: LocationManager

    setViewModel()
    getContract()
    init()
    setLiveData()


    }


    fun init(){


        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun setLiveData(){
        searchViewModel.mutableLiveData.observe(this){
            Log.d(TAG_ACTIVITY, "В активити получен результат: $it")

            textViewCityName.text = it[0].toString()
            textViewCurrentTemperature.text = it[1].toString()
            val stringUrl = it[2].toString()
            val url = "https:$stringUrl"
            Log.d(TAG_ACTIVITY, "url = $url")
            Glide.with(imageViewWeatherIcon).load(url).into(imageViewWeatherIcon)
            //imageViewWeatherIcon.setBackgroundColor(resources.getColor(R.color.black))
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG_ACTIVITY, "Вызван метод изменения местоположения")
        shirotaTextView.text = location.latitude.toString()
        dolgotaTextView.text = location.longitude.toString()

        locationManager.removeUpdates(this)

        searchViewModel.search("${location.latitude},${location.longitude}")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(TAG_ACTIVITY, "on Provider Disabled")
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(TAG_ACTIVITY, "on Provider Enabled")
    }
    */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        burger = view.findViewById(R.id.id_burger)
        textViewCityName = view.findViewById(R.id.id_textview_cityname)
        textViewCurrentTemperature = view.findViewById(R.id.id_textview_temperature)
        imageViewWeatherIcon = view.findViewById(R.id.id_imageview_weather_icon)
        //maxTempTextView = view.findViewById(R.id.id_textview_max_temperature_of_current_day)
        //minTempTextView = view.findViewById(R.id.id_textview_min_temperature_of_current_day)
        currentDescriptionTextView = view.findViewById(R.id.id_textview_current_weather_description)
        saveDeleteButton = view.findViewById(R.id.id_save_delete_button)
        textViewSaveOrDelete = view.findViewById(R.id.id_textview_save_or_delete)


        recyclerViewForecast = view.findViewById(R.id.id_recyclerview_forecast)


        init()
        setCloudLiveData()
        setCacheLiveData()
        viewModel.search("Москва")
    }

    private fun init() {
        viewModel = (activity as MainActivity).activityViewModel

        setRecyclerViewForecast()
        setBurger()
        setSaveButton()
    }

    private fun setBurger() {
        burger.setOnClickListener {
            (activity as MainActivity).showDrawer()
        }
    }

    private fun setSaveButton() {
        saveDeleteButton.setOnClickListener {
            viewModel.saveOrDeleteCurrentLocation()
        }

        textViewSaveOrDelete.setOnClickListener {
            viewModel.saveOrDeleteCurrentLocation()
        }
    }

    private fun setRecyclerViewForecast() {
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = ForecastAdapter()

        recyclerViewForecast.layoutManager = layoutManager
        recyclerViewForecast.adapter = adapter
    }

    private fun setCloudLiveData() {
        viewModel.cloudLiveData.observe(viewLifecycleOwner, {
            Log.d(TAG_WEATHER_FRAGMENT, "Во фрагменте получен ответ: $it")

            when (it) {
                is WeatherViewModel.LiveDataState.Weather -> {
                    textViewCityName.text = it.currentWeather.location.name
                    textViewCurrentTemperature.text = it.currentWeather.current.temp_c.toString()
                    currentDescriptionTextView.text =
                        it.currentWeather.current.condition.weather_text

                    val stringUrl = it.currentWeather.current.condition.icon_url.toString()
                    val url = "https:$stringUrl"
                    Log.d(TAG_WEATHER_FRAGMENT, "url = $url")
                    Glide.with(imageViewWeatherIcon).load(url).into(imageViewWeatherIcon)

                    if (it.currentWeather.isSaved) {
                        saveDeleteButton.setImageResource(R.drawable.ic_delete)
                        textViewSaveOrDelete.text = getString(R.string.delete)
                        Toast.makeText(
                            requireContext(),
                            "Местоположение сохранено",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    val newListForAdapter = mutableListOf<ForecastDayModel>()

                    for (i in it.currentWeather.forecast) {
                        newListForAdapter.add(i)
                    }

                    setNewListForForecastAdapter(newListForAdapter)
                }

                is WeatherViewModel.LiveDataState.Error -> {
                    textViewCityName.text = it.cloudError.message
                }
            }
        })
    }

    private fun setCacheLiveData() {
        viewModel.cacheLiveData.observe(viewLifecycleOwner) {

            if (it.isError) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                when (it.message) {
                    "Местоположение сохранено" -> {
                        saveDeleteButton.setImageResource(R.drawable.ic_delete)
                        textViewSaveOrDelete.text = getString(R.string.delete)
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    "Местоположение удалено" -> {
                        saveDeleteButton.setImageResource(R.drawable.ic_save)
                        textViewSaveOrDelete.text = getString(R.string.save)
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setNewListForForecastAdapter(forecastList: List<ForecastDayModel>) {
        adapter.setList(forecastList)
    }

    private fun workWithPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(requireContext(), "Даны разрешения", Toast.LENGTH_SHORT).show()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                Toast.makeText(
                    requireContext(),
                    "Для этого действия необходимы разрешения",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

    }
}