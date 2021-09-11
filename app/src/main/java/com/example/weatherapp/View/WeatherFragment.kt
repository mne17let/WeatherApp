package com.example.weatherapp.View

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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
import com.example.weatherapp.View.search.SearchAdapter
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar

class WeatherFragment : Fragment(R.layout.fragment_weather), LocationListener, SaveListAdapter.SaveClickListener {

    private val TAG_WEATHER_FRAGMENT = "MyWeatherFragment"
    private val KEY_FOR_WEATHER_FRAGMENT = "CacheOrNull"

    private lateinit var drawer: DrawerLayout

    //RecyclerView in Drawer
    private lateinit var drawerRecyclerView: RecyclerView
    private lateinit var drawerAdapter: SaveListAdapter

    // Drawer Buttons
    private lateinit var searchButtonDrawer: Button
    private lateinit var findMeButton: Button

    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGrantedMap ->

            var result = true

            for (key in isGrantedMap.keys) {
                if (isGrantedMap[key] == false) {
                    result = false
                }
            }

            if(result){
                workWithPermission()
            }
        }

    private lateinit var locationManager: LocationManager


    private lateinit var burger: ImageButton
    private lateinit var textViewCityName: TextView
    private lateinit var textViewCurrentTemperature: TextView
    private lateinit var imageViewWeatherIcon: ImageView
    private lateinit var currentDescriptionTextView: TextView
    private lateinit var saveDeleteButton: ImageButton
    private lateinit var textViewSaveOrDelete: TextView

    private lateinit var fullLayout: LinearLayout

    private lateinit var tryAgainButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingFragmentLayout: LinearLayout

    private lateinit var emptyLayout: LinearLayout
    private lateinit var emptyLayoutButton: Button

    private lateinit var constraintMenuSaveDelete: ConstraintLayout



    // RecyclerView
    private lateinit var recyclerViewForecast: RecyclerView
    private lateinit var forecastAdapter: ForecastAdapter

    private lateinit var viewModel: WeatherViewModel

    private var cacheLocationName: String? = null

    private var shouldAskLocation: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cacheLocationName = arguments?.getString(KEY_FOR_WEATHER_FRAGMENT, null)

        shouldAskLocation = cacheLocationName == null

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(shouldAskLocation){
            workWithPermission()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        burger = view.findViewById(R.id.id_burger)
        textViewCityName = view.findViewById(R.id.id_textview_cityname)
        textViewCurrentTemperature = view.findViewById(R.id.id_textview_temperature)
        imageViewWeatherIcon = view.findViewById(R.id.id_imageview_weather_icon)
        currentDescriptionTextView = view.findViewById(R.id.id_textview_current_weather_description)
        saveDeleteButton = view.findViewById(R.id.id_save_delete_button)
        textViewSaveOrDelete = view.findViewById(R.id.id_textview_save_or_delete)

        fullLayout = view.findViewById(R.id.id_full_frame_weather_fragment)

        tryAgainButton = view.findViewById(R.id.id_button_try_again)
        progressBar = view.findViewById(R.id.id_progress_bar)
        loadingFragmentLayout = view.findViewById(R.id.id_loading_fragment)

        emptyLayout = view.findViewById(R.id.id_empty_fragment)
        emptyLayoutButton = view.findViewById(R.id.id_empty_weather_fragment_add_new_location)

        constraintMenuSaveDelete = view.findViewById(R.id.id_constraint_menu_savedelete)


        recyclerViewForecast = view.findViewById(R.id.id_recyclerview_forecast)

        //drawer variables init
        drawer = view.findViewById(R.id.id_layout_drawer)
        drawerRecyclerView = view.findViewById(R.id.id_drawer_recyclerview)
        searchButtonDrawer = view.findViewById(R.id.id_button_add_new_location)
        findMeButton = view.findViewById(R.id.id_button_my_location)

        init()
    }

    private fun init() {
        viewModel = (activity as MainActivity).activityViewModel

        setDrawerRecyclerView()
        setRecyclerViewForecast()
        setBurger()
        setSaveButton()
        setAddNewLocationButtonOnEmptyFragment()
        setCloudLiveData()
        setCacheLiveData()
        setDrawerLiveData()
        setBackPress()
        setStartDrawerList()
        setSearchDrawerButton()

        if(cacheLocationName != null){
            getCurrentWeather()
            Log.d(TAG_WEATHER_FRAGMENT, "Получаю погоду")
        } else{
            setEmptyLayout()
            Log.d(TAG_WEATHER_FRAGMENT, "Засетил пустой фрагмент")
        }
    }

    private fun setDrawerRecyclerView(){
        drawerAdapter = SaveListAdapter(MyInterfaceForListAdapter(), requireContext(), this)
        val layoutManager = LinearLayoutManager(requireContext())
        drawerRecyclerView.layoutManager = layoutManager
        drawerRecyclerView.adapter = drawerAdapter
    }

    private fun setRecyclerViewForecast() {
        val layoutManager = LinearLayoutManager(requireContext())
        forecastAdapter = ForecastAdapter()

        recyclerViewForecast.layoutManager = layoutManager
        recyclerViewForecast.adapter = forecastAdapter
    }

    private fun setBurger() {
        burger.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
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

    private fun setAddNewLocationButtonOnEmptyFragment(){
        emptyLayoutButton.setOnClickListener {
            (activity as MainActivity).openSearchFragment()
        }
    }

    private fun setCloudLiveData() {
        viewModel.cloudLiveData.observe(viewLifecycleOwner, {
            Log.d(TAG_WEATHER_FRAGMENT, "Во фрагменте получен ответ: $it")

            when (it) {
                is WeatherViewModel.LiveDataState.Weather -> {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                    progressBar.isIndeterminate = false
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

                    fullLayout.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE

                    val newListForAdapter = mutableListOf<ForecastDayModel>()

                    for (i in it.currentWeather.forecast) {
                        newListForAdapter.add(i)
                    }

                    forecastAdapter.setList(newListForAdapter)
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

    private fun setBackPress(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if(drawer.isDrawerOpen(GravityCompat.START)){
                        drawer.closeDrawer(GravityCompat.START)
                    } else{
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    private fun getCurrentWeather(){
        emptyLayout.visibility = View.GONE
        fullLayout.visibility = View.GONE
        loadingFragmentLayout.visibility = View.VISIBLE

        progressBar.isIndeterminate = false

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        cacheLocationName?.let { viewModel.getWeather(it) }
    }

    private fun setEmptyLayout(){
        fullLayout.visibility = View.GONE
        loadingFragmentLayout.visibility = View.GONE

        emptyLayout.visibility = View.VISIBLE

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setDrawerLiveData(){
        viewModel.savedListLiveData.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                drawerAdapter.submitList(it)
                Toast.makeText(requireContext(), "Ничего не сохранено", Toast.LENGTH_SHORT).show()
            } else{
                drawerAdapter.submitList(it)
            }
        }
    }

    private fun setStartDrawerList(){
        viewModel.getSaveList()
    }

    private fun setSearchDrawerButton(){
        searchButtonDrawer.setOnClickListener {
            drawer.closeDrawer(GravityCompat.START)
            (activity as MainActivity).openSearchFragment()
        }
    }

    @SuppressLint("MissingPermission")
    private fun workWithPermission() {
        if (checkPermissions()) {
            when(checkProvidersStatus()){
                2 -> {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0f, this)
                }
                1 -> {
                    Snackbar.make(drawer, getString(R.string.gps_off), Snackbar.LENGTH_SHORT).show()
                }
                0 -> {
                    Snackbar.make(drawer, getString(R.string.network_off), Snackbar.LENGTH_SHORT).show()
                }
            }
        } else {
            askPermissions()
        }
    }

    private fun checkPermissions(): Boolean{
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun askPermissions(){
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun checkProvidersStatus(): Int{
        val isGPSEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetWorkEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val result = if(isGPSEnabled && isNetWorkEnabled){
            2
        } else if(!isGPSEnabled){
            1
        } else{
            0
        }
        return result
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG_WEATHER_FRAGMENT, "Вызван метод изменения местоположения")
        val shirota = location.latitude.toString()
        val dolgota = location.longitude.toString()

        locationManager.removeUpdates(this)

        (activity as MainActivity).openNewLocationFragment("$shirota,$dolgota")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(TAG_WEATHER_FRAGMENT, "on Provider Disabled")
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(TAG_WEATHER_FRAGMENT, "on Provider Enabled")
    }

    private fun checkShouldShowEducation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                Snackbar.make(drawer, getString(R.string.need_location_permission), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    fun setTryAgainButton(){

    }

    override fun onClick(string: String) {
        drawer.closeDrawer(GravityCompat.START)

        viewModel.getWeather(string)
    }
}