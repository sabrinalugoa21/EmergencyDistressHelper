package com.example.emergencydistresshelper

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val sosButton = findViewById<Button>(R.id.sos_button)

        //needed for okhttp for Twilio Text Message
        StrictMode.setThreadPolicy(ThreadPolicy.Builder().permitAll().build())

        sosButton.setOnLongClickListener {
            val sosMessage = "Sending Text Message"
            val sosSnackbar = Snackbar.make(sosButton, sosMessage, Snackbar.LENGTH_SHORT)
            sosSnackbar.show()

            TextMessage.sendTextMessage()
            return@setOnLongClickListener true
        }
    }
}