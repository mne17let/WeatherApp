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
import com.example.weatherapp.Model.RepositoryResult
import com.example.weatherapp.Model.api.weatherModels.forecast.ForecastDayModel
import com.example.weatherapp.R
import com.example.weatherapp.View.forecast_recyclerview.ForecastAdapter
import com.example.weatherapp.View.savelist_recyclerview.SavedLocationsAdapter
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

class WeatherFragment : Fragment(R.layout.fragment_weather), LocationListener, SavedLocationsAdapter.SaveClickListener {

    private val TAG_WEATHER_FRAGMENT = "MyWeatherFragment"
    private val KEY_FOR_WEATHER_FRAGMENT = "CacheOrNull"

    private var cacheCity = ""
    private var cacheRegion = ""
    private var cacheCountry = ""

    private var cacheLocationName: String? = null
    private var isDrawerClicked = false
    private var currentDrawerClicked: String? = null
    private var wasOpened = false

    private lateinit var drawer: DrawerLayout

    //RecyclerView in Drawer
    private lateinit var drawerRecyclerView: RecyclerView
    private lateinit var drawerAdapter: SavedLocationsAdapter

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
            } else{
                if(cacheLocationName == null){
                    setEmptyLayout()
                } else{
                    setUsualLayout()
                }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cacheLocationName = arguments?.getString(KEY_FOR_WEATHER_FRAGMENT, null)

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

        setLoadingLayout()

        init()

        if(cacheLocationName == null){
            workWithPermission()
        } else if(wasOpened == false) {
            viewModel.getWeather(cacheLocationName!!)
        }
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
        setFindMeButton()
    }

    private fun setDrawerRecyclerView(){
        drawerAdapter = SavedLocationsAdapter(requireContext(), this)
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
            Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент с погодой. Удаление/сохранение")
            viewModel.saveOrDeleteCurrentLocation(cacheCity, cacheRegion, cacheCountry)
        }

        textViewSaveOrDelete.setOnClickListener {
            viewModel.saveOrDeleteCurrentLocation(cacheCity, cacheRegion, cacheCountry)
        }
    }

    private fun setAddNewLocationButtonOnEmptyFragment(){
        emptyLayoutButton.setOnClickListener {
            (activity as MainActivity).openSearchFragment()
        }
    }

    private fun setCloudLiveData() {
        viewModel.cloudLiveData.observe(viewLifecycleOwner, {
            Log.d(TAG_WEATHER_FRAGMENT, "Обновление лайвдаты. Получена погода: $it")

            when (it) {
                is WeatherViewModel.LiveDataState.Weather -> {
                    setSuccessUi()
                    setDataIntoUi(it.currentWeather)
                }
                is WeatherViewModel.LiveDataState.Error -> {
                    textViewCityName.text = it.cloudError.message
                }
            }
        })
    }

    private fun setDataIntoUi(currentWeather: RepositoryResult.CloudSuccessRepositoryResult) {
        val location = currentWeather.location
        val current = currentWeather.current
        val forecast = currentWeather.forecast
        val isSaved = currentWeather.isSaved

        textViewCityName.text = location.name

        val roundTemp = current.temp_c.roundToInt()
        textViewCurrentTemperature.text = if(roundTemp > 0) "+$roundTemp" else roundTemp.toString()

        val newCache = "${location.name}, ${location.region}, ${location.country}"

        cacheCity = location.name
        cacheRegion = location.region
        cacheCountry = location.country

        currentDescriptionTextView.text = newCache

        val stringUrl = "https:${current.condition.icon_url}"
        Glide.with(imageViewWeatherIcon).load(stringUrl).into(imageViewWeatherIcon)

        if (isSaved) {
            Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент. Текущая локация сохранена: ${isSaved}")
            saveDeleteButton.setImageResource(R.drawable.ic_delete)
            textViewSaveOrDelete.text = getString(R.string.delete)
            isDrawerClicked = true
            currentDrawerClicked = newCache
            drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)
            Log.d(TAG_WEATHER_FRAGMENT, "Кэш во фрагменте: $newCache")
            Toast.makeText(requireContext(), "Сохранённое", Toast.LENGTH_SHORT).show()
        } else{
            isDrawerClicked = false
            currentDrawerClicked = null
            Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент. Текущая локация сохранена: ${isSaved}")
            saveDeleteButton.setImageResource(R.drawable.ic_save)
            drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)
            textViewSaveOrDelete.text = getString(R.string.save)
        }

        val newListForAdapter = mutableListOf<ForecastDayModel>()
        for (i in forecast) { newListForAdapter.add(i)}
        forecastAdapter.setList(newListForAdapter)
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

    fun setLoadingLayout(){
        emptyLayout.visibility = View.GONE
        fullLayout.visibility = View.GONE
        loadingFragmentLayout.visibility = View.VISIBLE
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        progressBar.isIndeterminate = true
    }

    private fun setEmptyLayout(){
        fullLayout.visibility = View.GONE
        loadingFragmentLayout.visibility = View.GONE

        emptyLayout.visibility = View.VISIBLE

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun setUsualLayout(){
        fullLayout.visibility = View.VISIBLE
        loadingFragmentLayout.visibility = View.GONE

        emptyLayout.visibility = View.GONE

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun setDrawerLiveData(){
        viewModel.savedListLiveData.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                Log.d(TAG_WEATHER_FRAGMENT, "Во фрагмент пришёл пустой лист сохранённых локаций: $it")
                drawerAdapter.setList(it)
                isDrawerClicked = false
                currentDrawerClicked = null
                Toast.makeText(requireContext(), "Ничего не сохранено", Toast.LENGTH_SHORT).show()
            } else{
                if(it.contains(currentDrawerClicked)){
                    isDrawerClicked = true
                    drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)
                }
                Log.d(TAG_WEATHER_FRAGMENT, "Во фрагмент пришли сохранённые: $it")
                Log.d(TAG_WEATHER_FRAGMENT, "Размер листа сохранённых локаций: ${it.size}")
                drawerAdapter.setList(it)
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

    fun setFindMeButton(){
        findMeButton.setOnClickListener {
            drawer.closeDrawer(GravityCompat.START)
            setLoadingLayout()
            isDrawerClicked = false
            currentDrawerClicked = null

            drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)

            workWithPermission()
        }
    }

    fun setSuccessUi(){
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        progressBar.isIndeterminate = false
        fullLayout.visibility = View.VISIBLE
        loadingFragmentLayout.visibility = View.GONE
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
                    if(cacheLocationName == null){
                        setEmptyLayout()
                    }
                }
                0 -> {
                    Snackbar.make(drawer, getString(R.string.network_off), Snackbar.LENGTH_SHORT).show()
                    if(cacheLocationName == null){
                        setEmptyLayout()
                    }
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
        setLoadingLayout()

        Log.d(TAG_WEATHER_FRAGMENT, "Местоположение найдено")
        val shirota = location.latitude.toString()
        val dolgota = location.longitude.toString()

        locationManager.removeUpdates(this)

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        setStartDrawerList()

        viewModel.getWeather("$shirota,$dolgota")
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

        isDrawerClicked = true

        if(string != currentDrawerClicked){

            setLoadingLayout()

            currentDrawerClicked = string
            Log.d(TAG_WEATHER_FRAGMENT, "Текущий drawerClicked: $currentDrawerClicked")

            Log.d(TAG_WEATHER_FRAGMENT, "Текущий кэш: $cacheLocationName")
            cacheLocationName = string
            drawer.closeDrawer(GravityCompat.START)

            drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)

            Log.d(TAG_WEATHER_FRAGMENT, "Новый кэш: $cacheLocationName")

            Log.d(TAG_WEATHER_FRAGMENT, "В drawer нажат: $string")
            viewModel.getWeather(string)
        } else{
            drawer.closeDrawer(GravityCompat.START)
        }
    }
}