package com.zone.chatterz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var arrayList: ArrayList<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val fragmentManager = supportFragmentManager
        arrayList = arrayListOf()
        addFrament(arrayList)
        val adapters = PagerAdapter(fragmentManager,arrayList)

        viewpager.adapter = adapters
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabBarLayout))

    }
    private fun addFrament(list: ArrayList<Fragment>){
        list.add(PrivateChats())
        list.add(GroupChat())
        list.add(FollowerActivity())
    }
}
