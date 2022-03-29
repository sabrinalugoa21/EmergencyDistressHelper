package com.example.emergencydistresshelper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginToreg = findViewById<Button>(R.id.loginToreg)

        loginToreg.setOnClickListener  {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}