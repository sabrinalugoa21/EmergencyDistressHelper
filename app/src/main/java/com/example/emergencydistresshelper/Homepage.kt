package com.example.emergencydistresshelper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar


class Homepage : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var Latitude : Double = 0.0
    private var Longitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val sosButton = findViewById<Button>(R.id.sos_button)
        val contactsButton = findViewById<Button>(R.id.btn_contacts_page)

        //needed for okhttp for Twilio Text Message
        StrictMode.setThreadPolicy(ThreadPolicy.Builder().permitAll().build())

        findGPS()

        sosButton.setOnLongClickListener {
            val sosMessage = TextMessage.sendTextMessage(Latitude, Longitude)
            val sosSnackbar = Snackbar.make(sosButton, sosMessage, Snackbar.LENGTH_SHORT)
            sosSnackbar.show()

            return@setOnLongClickListener true
        }

        contactsButton.setOnClickListener {
            val intent = Intent(this, CreateContact::class.java)
            startActivity(intent)
        }
    }

    private fun findGPS(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("-----findGPS", "Location permission NOT granted")
            return
        }
        Log.d("-----findGPS", "Location permission granted!")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    Latitude = location.latitude
                    Longitude = location.longitude
                    Log.d("-----findGPS", "Lat and long found")
                    Log.d("-----findGPS", "Latitude = $Latitude")
                    Log.d("-----findGPS", "Longitude = $Longitude")
                } else{
                    Log.d("-----findGPS", "returned NULL")
                }
            }
    }
}