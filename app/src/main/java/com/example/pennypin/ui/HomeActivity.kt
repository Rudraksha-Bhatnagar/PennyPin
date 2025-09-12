package com.example.pennypin.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.pennypin.R

class HomeActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val loginbutton: Button = findViewById(R.id.btnLoginEmail)
        val signupbutton: Button = findViewById(R.id.btnSignup)
        loginbutton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        signupbutton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}