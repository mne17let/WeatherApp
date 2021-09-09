package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.example.weatherapp.View.WeatherFragment

class MainActivity: AppCompatActivity() {

    private val TAG_ACTIVITY = "MyMainActivity"

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setStartFragment()
    }

    fun init(){
        drawer = findViewById(R.id.id_layout_drawer)
    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        } else{
            super.onBackPressed()
        }
    }

    fun showDrawer(){
        drawer.openDrawer(GravityCompat.START)
    }

    fun setStartFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.id_fragment_container, WeatherFragment())
            .commit()
    }
}