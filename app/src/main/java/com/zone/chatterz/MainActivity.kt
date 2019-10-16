package com.zone.chatterz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zone.chatterz.Model.User
import com.zone.chatterz.mainFragment.ChatActivity
import com.zone.chatterz.mainFragment.ProfileActivity
import com.zone.chatterz.mainFragment.SearchActivity
import com.zone.chatterz.mainFragment.StatusActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chat.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerToggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setNavigationInitially()

        bottomNavigationBar.setOnNavigationItemSelectedListener { menuItem ->
            bottomNavigationBar.menu.getItem(0).setIcon(R.drawable.chats_light_icon)
            bottomNavigationBar.menu.getItem(1).setIcon(R.drawable.search_light_icon)
            bottomNavigationBar.menu.getItem(2).setIcon(R.drawable.icon_status)
            bottomNavigationBar.menu.getItem(3).setIcon(R.drawable.proifle_light_icon)
            when(menuItem.itemId){

                R.id.chats ->{
                    val chatFragment = ChatActivity()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main,chatFragment)
                        .addToBackStack(null).commit()
                    menuItem.setIcon(R.drawable.chat_dark_icon)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.search_bottombar ->{
                    val searchActivity = SearchActivity()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main,searchActivity)
                        .addToBackStack(null).commit()
                    menuItem.setIcon(R.drawable.search_dark_icon)
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
                    menuItem.setIcon(R.drawable.profile_dark_icon)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        updateOnlineStatus()
    }
    private fun setNavigationInitially(){
        val chatsFragment = ChatActivity()
        supportFragmentManager.beginTransaction()
            .add(R.id.container_main,chatsFragment)
            .addToBackStack(null).commit()
        bottomNavigationBar.menu.getItem(0).setIcon(R.drawable.chat_dark_icon)
    }

    private fun updateOnlineStatus(){
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSet in p0.children) {
                    var user = dataSet.getValue(User::class.java)
                    if (user != null) {
                        if (user.id.equals(firebaseUser.uid)){
                            val hashMap =  HashMap<String,Any>()
                            hashMap.put("status","online")
                            dataSet.ref.updateChildren(hashMap)
                            dataSet.ref.child("status").onDisconnect().setValue("offline")
                        }
                    }
                }
            }
        })
    }
}


