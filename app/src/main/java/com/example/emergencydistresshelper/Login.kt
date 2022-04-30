package com.example.emergencydistresshelper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity(), View.OnClickListener {
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    private var signinUser: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var progressbar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        getSupportActionBar()?.hide();

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginToreg = findViewById<Button>(R.id.loginToreg)

        loginToreg.setOnClickListener  {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        mAuth = FirebaseAuth.getInstance()
        signinUser = findViewById<View>(R.id.signIn) as Button
        signinUser!!.setOnClickListener(this)
        editTextEmail = findViewById<View>(R.id.email) as EditText
        editTextPassword = findViewById<View>(R.id.password) as EditText
        progressbar = findViewById<View>(R.id.progressBar) as ProgressBar

        checkLocation()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.signIn -> userLogin()
        }
    }

    private fun userLogin() {
        val email = editTextEmail!!.text.toString().trim { it <= ' ' }
        val password = editTextPassword!!.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            editTextEmail!!.error = "Email is required"
            editTextEmail!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = "Please enter a valid email!"
            editTextEmail!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editTextPassword!!.error = "Password is required"
            editTextPassword!!.requestFocus()
            return
        }
        if (password.length < 6) {
            editTextPassword!!.error = "Min password length is 6 characters!"
            editTextPassword!!.requestFocus()
            return
        }
        progressbar!!.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@Login, Homepage::class.java))
                } else {
                    Toast.makeText(
                        this@Login,
                        "Failed to login! Please check your credentials",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun checkLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("-----checkLocation", "Location permission NOT granted")
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 10)
        }
        else {
            Log.d("-----checkLocation", "Location permission granted!")
        }
    }
}