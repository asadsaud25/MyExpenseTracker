package com.example.myexpensetracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class Welcomepage : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 2000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_page)

        Handler().postDelayed({
            val intent = Intent(this@Welcomepage, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT)
    }
}
