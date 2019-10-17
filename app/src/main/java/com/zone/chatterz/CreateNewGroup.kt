package com.zone.chatterz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_create_new_group.*

class CreateNewGroup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_group)

        backArrow_CreateNewGroup.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("fromReturn","createGroup")
            startActivity(intent)
        }

        val supportManager = supportFragmentManager
    }
}
