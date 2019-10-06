package com.zone.chatterz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zone.chatterz.mainFragment.ChatActivity
import com.zone.chatterz.mainFragment.ProfileActivity
import com.zone.chatterz.mainFragment.SearchActivity
import com.zone.chatterz.mainFragment.StatusActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chatsFragment = ChatActivity()
        supportFragmentManager.beginTransaction()
            .add(R.id.container_main,chatsFragment)
            .addToBackStack(null).commit()

        bottomNavigationBar.setOnNavigationItemSelectedListener { menuItem ->

            when(menuItem.itemId){

                R.id.chats ->{
                    val chatsFragment = ChatActivity()
                    supportFragmentManager.beginTransaction()
                        .add(R.id.container_main,chatsFragment)
                        .addToBackStack(null).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.search_bottombar ->{
                    val searchActivity = SearchActivity()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main,searchActivity)
                        .addToBackStack(null).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.status_bottombar ->{
                    val statusActivity = StatusActivity()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main,statusActivity)
                        .addToBackStack(null).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile_bottombar ->{
                    val profileActivity = ProfileActivity()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main,profileActivity)
                        .addToBackStack(null).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }
}


