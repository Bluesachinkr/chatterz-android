package com.zone.chatterz.PreActivities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.zone.chatterz.MainActivity
import com.zone.chatterz.R
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        mAuth=FirebaseAuth.getInstance()

        loginButton.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        registerButton.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onStart() {
        super.onStart()

        val firebaseUser = mAuth.currentUser

        if(firebaseUser!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
 }
