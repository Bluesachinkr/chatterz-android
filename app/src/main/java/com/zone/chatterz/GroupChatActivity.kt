package com.zone.chatterz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

open class GroupChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)
    }

    override fun onBackPressed() {
        val int = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
