package com.zone.chatterz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class StartActivity : AppCompatActivity() {

    private val SPLASH_DELAY_LENGTH : Long =2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Handler().postDelayed({

            var intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()

        }, SPLASH_DELAY_LENGTH)

    }
}