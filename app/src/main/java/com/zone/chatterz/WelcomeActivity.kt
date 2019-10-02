package com.zone.chatterz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        loginButton.setOnClickListener {

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)

        }

        registerButton.setOnClickListener {

            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)

        }
    }
}
