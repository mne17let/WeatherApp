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
import java.util.*
import kotlin.math.roundToInt

class WeatherFragment : Fragment(R.layout.fragment_weather), LocationListener, SavedLocationsAdapter.SaveClickListener {

    private enum class WasOpened{
        OPEN_DEFAULT,
        OPEN_START,
        OPEN_WITH_CACHE,
        OPEN_WITH_SEARCH_TEXT_CACHE,
        OPEN_WITH_SEARCH_MY_LOCATION
    }

    private var wasOpened = WasOpened.OPEN_DEFAULT

    private val TAG_WEATHER_FRAGMENT = "MyWeatherFragment"
    private val KEY_FOR_WEATHER_FRAGMENT = "CacheOrNull"
    private val KEY_FOR_WEATHER_FRAGMENT_IS_OPEN_FROM_TEXT_SEARCH = "IsOpenFromTextSearch"

    private var isOpenFromTextSearch: Boolean? = null


    private var cacheCity = ""
    private var cacheRegion = ""
    private var cacheCountry = ""

    private var cacheLocationName: String? = null
    private var isDrawerClicked = false
    private var currentDrawerClicked: String? = null

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

    // Full layout
    private lateinit var burger: ImageButton
    private lateinit var textViewCityName: TextView
    private lateinit var textViewCurrentTemperature: TextView
    private lateinit var imageViewWeatherIcon: ImageView
    private lateinit var currentFullLocationTextView: TextView
    private lateinit var saveDeleteButton: ImageButton
    private lateinit var textViewSaveOrDelete: TextView

    private lateinit var fullLayout: LinearLayout

    // Loading layout
    private lateinit var tryAgainButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingFragmentLayout: LinearLayout

    // Empty layout
    private lateinit var emptyLayout: LinearLayout
    private lateinit var emptyLayoutButton: Button

    // RecyclerView
    private lateinit var recyclerViewForecast: RecyclerView
    private lateinit var forecastAdapter: ForecastAdapter

    private lateinit var viewModel: WeatherViewModel

    private var currentTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTime = Calendar.getInstance().time.time

        cacheLocationName = arguments?.getString(KEY_FOR_WEATHER_FRAGMENT, null)

        isOpenFromTextSearch = arguments?.getBoolean(KEY_FOR_WEATHER_FRAGMENT_IS_OPEN_FROM_TEXT_SEARCH, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент. В onViewCreated данные: ${cacheLocationName}, ")

        if(cacheLocationName != null) {
            if(isOpenFromTextSearch == true){
                wasOpened = WasOpened.OPEN_WITH_SEARCH_TEXT_CACHE
                currentDrawerClicked = cacheLocationName

            }else{
                wasOpened = WasOpened.OPEN_WITH_CACHE
            }
        } else{
            wasOpened = WasOpened.OPEN_START
        }

        Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент. Кэш: ${cacheLocationName}.\n" +
                "Нажатый итем: $currentDrawerClicked")

        Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент. Номер фрагмента: ${currentTime}")

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        emptyLayout = view.findViewById(R.id.id_empty_fragment)
        fullLayout = view.findViewById(R.id.id_full_frame_weather_fragment)

        drawer = view.findViewById(R.id.id_layout_drawer)

        initEmptyLayout()
        initFullLayout()
        initLoadingLayout()

        setLoadingLayout()

        init()

        if(cacheLocationName == null){
            workWithPermission()
        } else {
            wasOpened = WasOpened.OPEN_WITH_CACHE
            Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент. Вызов вьюмодели из: иф-елс в onViewCreated с данными: $cacheLocationName")
            viewModel.getWeather(cacheLocationName!!)
        }
    }

    private fun initLoadingLayout() {
        // Loading layout
        tryAgainButton = requireView().findViewById(R.id.id_button_try_again)
        progressBar = requireView().findViewById(R.id.id_progress_bar)
        loadingFragmentLayout = requireView().findViewById(R.id.id_loading_fragment)
    }

    private fun initEmptyLayout(){
        // Empty layout
        emptyLayoutButton = requireView().findViewById(R.id.id_empty_weather_fragment_add_new_location)
    }

    private fun initFullLayout(){
        // Full layout
        burger = requireView().findViewById(R.id.id_burger)
        textViewCityName = requireView().findViewById(R.id.id_textview_cityname)
        textViewCurrentTemperature = requireView().findViewById(R.id.id_textview_temperature)
        imageViewWeatherIcon = requireView().findViewById(R.id.id_imageview_weather_icon)
        currentFullLocationTextView = requireView().findViewById(R.id.id_textview_current_full_location)
        saveDeleteButton = requireView().findViewById(R.id.id_save_delete_button)
        textViewSaveOrDelete = requireView().findViewById(R.id.id_textview_save_or_delete)

        recyclerViewForecast = requireView().findViewById(R.id.id_recyclerview_forecast)

        //drawer variables init
        drawerRecyclerView = requireView().findViewById(R.id.id_drawer_recyclerview)
        searchButtonDrawer = requireView().findViewById(R.id.id_button_add_new_location)
        findMeButton = requireView().findViewById(R.id.id_button_my_location)
    }

    private fun init() {
        viewModel = (activity as MainActivity).activityViewModel

        if(cacheLocationName == null){
            setAddNewLocationButtonOnEmptyFragment()
        } else{
            setViewsWorkForFullLayout()
        }
    }

    private fun setViewsWorkForFullLayout(){
        if(wasOpened != WasOpened.OPEN_WITH_SEARCH_MY_LOCATION){
            Log.d(TAG_WEATHER_FRAGMENT, "Вызван метод настройки полного экрана")
            setDrawerRecyclerView()
            setRecyclerViewForecast()
            setBurger()
            setSaveButton()
            setCloudLiveData()
            setCacheLiveData()
            setDrawerLiveData()
            setBackPress()
            setSearchDrawerButton()
            setFindMeButton()
        }

        setStartDrawerList()
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
            Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент с погодой. Удаление/сохранение. Вызов вьюмодели")
            currentDrawerClicked = cacheLocationName
            viewModel.saveOrDeleteCurrentLocation(cacheCity, cacheRegion, cacheCountry)
        }

        textViewSaveOrDelete.setOnClickListener {
            currentDrawerClicked = cacheLocationName
            Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент с погодой. Удаление/сохранение. Вызов вьюмодели")
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
                    if(wasOpened == WasOpened.OPEN_WITH_SEARCH_TEXT_CACHE){
                        val location = it.currentWeather.location
                        val newFullLocation = "${location.name}, ${location.region}, ${location.country}"

                        if(cacheLocationName == newFullLocation){
                            wasOpened = WasOpened.OPEN_WITH_CACHE
                            setSuccessUi()
                            setDataIntoUi(it.currentWeather)
                        }
                    } else{
                        setSuccessUi()
                        setDataIntoUi(it.currentWeather)
                    }
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

        cacheLocationName = newCache
        currentDrawerClicked = newCache

        cacheCity = location.name
        cacheRegion = location.region
        cacheCountry = location.country

        currentFullLocationTextView.text = newCache

        val stringUrl = "https:${current.condition.icon_url}"
        Glide.with(imageViewWeatherIcon).load(stringUrl).into(imageViewWeatherIcon)

        if (isSaved) {
            Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент. Текущая локация сохранена: ${isSaved}")
            saveDeleteButton.setImageResource(R.drawable.ic_delete)
            textViewSaveOrDelete.text = getString(R.string.delete)
            isDrawerClicked = true
            drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)
            Log.d(TAG_WEATHER_FRAGMENT, "Кэш во фрагменте: $newCache")
            Toast.makeText(requireContext(), "Сохранённое", Toast.LENGTH_SHORT).show()
            Log.d(TAG_WEATHER_FRAGMENT, "Засетил в адаптер данные: $currentDrawerClicked, $isDrawerClicked")

        } else{
            Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент. Текущая локация сохранена: ${isSaved}")
            saveDeleteButton.setImageResource(R.drawable.ic_save)
            drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)
            isDrawerClicked = false
            Log.d(TAG_WEATHER_FRAGMENT, "Засетил в адаптер данные: $currentDrawerClicked, $isDrawerClicked")
            textViewSaveOrDelete.text = getString(R.string.save)
        }

        val newListForAdapter = mutableListOf<ForecastDayModel>()
        for (i in forecast) { newListForAdapter.add(i)}
        forecastAdapter.setList(newListForAdapter)
    }

    fun setSuccessUi(){
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        progressBar.isIndeterminate = false
        fullLayout.visibility = View.VISIBLE
        loadingFragmentLayout.visibility = View.GONE
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
        Log.d(TAG_WEATHER_FRAGMENT, "Сетится лайаут загрузки")

        loadingFragmentLayout.visibility = View.VISIBLE
        emptyLayout.visibility = View.GONE
        fullLayout.visibility = View.GONE

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
                drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)

                Toast.makeText(requireContext(), "Ничего не сохранено", Toast.LENGTH_SHORT).show()

                Log.d(TAG_WEATHER_FRAGMENT, "Засетил в адаптер данные: $currentDrawerClicked, $isDrawerClicked")

            } else{
                drawerAdapter.setList(it)
                if(it.contains(currentDrawerClicked)){
                    isDrawerClicked = true
                    drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)

                    Log.d(TAG_WEATHER_FRAGMENT, "Засетил в адаптер данные: $currentDrawerClicked, $isDrawerClicked")
                }
                Log.d(TAG_WEATHER_FRAGMENT, "Во фрагмент пришли сохранённые: $it")
                Log.d(TAG_WEATHER_FRAGMENT, "Размер листа сохранённых локаций: ${it.size}")

                Log.d(TAG_WEATHER_FRAGMENT, "Засетил в адаптер данные: $currentDrawerClicked, $isDrawerClicked")
            }
        }
    }

    private fun setStartDrawerList(){
        Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент с погодой. Вызов у вьюмодели метода getSaveList")
        viewModel.getSaveList()
    }

    private fun setSearchDrawerButton(){
        searchButtonDrawer.setOnClickListener {
            Log.d(TAG_WEATHER_FRAGMENT, "НАЖАТА КНОПКА ДОБАВЛЕНИЯ И ПОИСКА НОВОЙ ЛОКАЦИИ")
            drawer.closeDrawer(GravityCompat.START)
            (activity as MainActivity).openSearchFragment()
        }
    }

    fun setFindMeButton(){
        findMeButton.setOnClickListener {
            Log.d(TAG_WEATHER_FRAGMENT, "Нажата кнопка найти местоположение")
            drawer.closeDrawer(GravityCompat.START)
            setLoadingLayout()

            isDrawerClicked = false
            currentDrawerClicked = null

            wasOpened = WasOpened.OPEN_WITH_SEARCH_MY_LOCATION

            drawerAdapter.setClicked(isDrawerClicked, currentDrawerClicked)

            workWithPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun workWithPermission() {
        Log.d(TAG_WEATHER_FRAGMENT, "Вызван метод работы с разрешениями")

        if (checkPermissions()) {
            when(checkProvidersStatus()){
                2 -> {
                    if(cacheLocationName == null){
                        setViewsWorkForFullLayout()
                    }
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

        Log.d(TAG_WEATHER_FRAGMENT, "Вызван метод работы с разрешениями")
        Log.d(TAG_WEATHER_FRAGMENT, "Местоположение найдено")
        val shirota = location.latitude.toString()
        val dolgota = location.longitude.toString()

        locationManager.removeUpdates(this)

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент с погодой. Вызов вьюмодели после изменения локации")

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

            Log.d(TAG_WEATHER_FRAGMENT, "Новый кэш: $cacheLocationName")

            Log.d(TAG_WEATHER_FRAGMENT, "В drawer нажат: $string")

            Log.d(TAG_WEATHER_FRAGMENT, "Фрагмент с погодой. Вызов вьюмодели после нажатия в дровере")
            viewModel.getWeather(string)
        } else{
            drawer.closeDrawer(GravityCompat.START)
        }
    }
}