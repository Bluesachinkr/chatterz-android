package com.zone.chatterz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout

class Following_FollowersActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following__followers)

        tabLayout = findViewById(R.id.tabBarLayout_FollowingActivity)

        tabLayout.addTab(tabLayout.newTab().setText("Followers"))
        tabLayout.addTab(tabLayout.newTab().setText("Following"))

    }
}
