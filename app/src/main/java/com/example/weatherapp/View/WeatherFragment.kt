package com.example.weatherapp.View

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.R

class WeatherFragment: Fragment(R.layout.fragment_weather) {

    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                isGrantedMap ->

            for (key in isGrantedMap.keys){
                if(isGrantedMap[key] == false){

                }
            }
        }

/*    private lateinit var searchViewModel: SearchViewModel
    private lateinit var textViewCityName: TextView
    private lateinit var textViewCurrentTemperature: TextView
    private lateinit var imageViewWeatherIcon: ImageView

    private lateinit var shirotaTextView: TextView
    private lateinit var dolgotaTextView: TextView

    private lateinit var locationManager: LocationManager

    setViewModel()
    getContract()
    init()
    setLiveData()


    }

    Log.d(TAG_ACTIVITY, "Пройдена строка с локэйшнМенеджером")

    //searchViewModel.search("Певек")

    private fun getPermission() {
        activityResultLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun setViewModel(){
        val myApplication: MyApplication = application as MyApplication
        searchViewModel = myApplication.searchViewModel
    }

    fun init(){
        textViewCityName = findViewById(R.id.id_textview_cityname)
        textViewCurrentTemperature = findViewById(R.id.id_textview_temperature)
        imageViewWeatherIcon = findViewById(R.id.id_imageview_weather_icon)

        shirotaTextView = findViewById(R.id.id_textview_shirota)
        dolgotaTextView = findViewById(R.id.id_textview_dolgota)

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

    fun workWithPermission(){
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){

            Toast.makeText(requireContext(), "Даны разрешения", Toast.LENGTH_SHORT).show()
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toast.makeText(requireContext(), "Для этого действия необходимы разрешения", Toast.LENGTH_SHORT).show()
            } else{
                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        } else{
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }

    }
}