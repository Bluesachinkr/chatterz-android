package com.zone.chatterz

import com.zone.chatterz.Interfaces.DrawerLocker
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zone.chatterz.Model.User
import com.zone.chatterz.PreActivities.WelcomeActivity
import com.zone.chatterz.Settings.GeneralSettings
import com.zone.chatterz.mainFragment.ChatActivity
import com.zone.chatterz.mainFragment.ProfileActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DrawerLocker {

    private lateinit var drawer: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var content: RelativeLayout
    private lateinit var navigationDrawerMenu : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        drawer = findViewById(R.id.drawerLayout)
        content = findViewById(R.id.contentLayout)
        navigationDrawerMenu = findViewById(R.id.NavigationDrawerMenu)
        drawer.setScrimColor(Color.TRANSPARENT)
        drawer.addDrawerListener(object :
            ActionBarDrawerToggle(this, drawer, R.string.openDrawer, R.string.closeDrawer) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slidex = drawerView.width * slideOffset
                content.translationX = -slidex
            }
        })

        firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                val i = Intent(this, WelcomeActivity::class.java)
                startActivity(i)
                finish()
                return@AuthStateListener
            }
        }

        setNavigationInitially()

        bottomNavigationBar.setOnNavigationItemSelectedListener { menuItem ->
            bottomNavigationBar.menu.getItem(0).setIcon(R.drawable.ic_multiple_users_silhouette)
            bottomNavigationBar.menu.getItem(1).setIcon(R.drawable.chats_light_icon)
            bottomNavigationBar.menu.getItem(2).setIcon(R.drawable.proifle_light_icon)
            when (menuItem.itemId) {

                R.id.chats -> {
                    val chatFragment = ChatActivity()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main, chatFragment)
                        .addToBackStack(null).commit()
                    menuItem.setIcon(R.drawable.chat_dark_icon)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.groups_bottombar -> {
                    val intent = Intent(this, GroupChatActivity::class.java)
                    startActivity(intent)
                    menuItem.setIcon(R.drawable.ic_multiple_users_silhouette)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile_bottombar -> {
                    val profileActivity = ProfileActivity()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main, profileActivity)
                        .addToBackStack(null).commit()
                    menuItem.setIcon(R.drawable.profile_dark_icon)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        NavigationDrawerMenu.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.logout -> {
                    val firebaseAuth = FirebaseAuth.getInstance().signOut()
                    return@setNavigationItemSelectedListener true
                }
                R.id.settings_icon -> {
                    val intent = Intent(this, GeneralSettings::class.java)
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    return@setNavigationItemSelectedListener false
                }
            }
            return@setNavigationItemSelectedListener true
        }

        updateOnlineStatus()
    }

    private fun setNavigationInitially() {
        bottomNavigationBar.selectedItemId = R.id.chats
        val chatsFragment = ChatActivity()
        supportFragmentManager.beginTransaction()
            .add(R.id.container_main, chatsFragment)
            .addToBackStack(null).commit()
        bottomNavigationBar.menu.getItem(1).setIcon(R.drawable.chat_dark_icon)
    }

    private fun updateOnlineStatus() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var user = p0.getValue(User::class.java)
                if (user != null) {
                    val hashMap = HashMap<String, Any>()
                    hashMap.put("status", "online")
                    p0.ref.updateChildren(hashMap)
                    p0.ref.child("status").onDisconnect().setValue("offline")
                }
            }
        })
    }

    override fun setDrawerLockerEnabled(enabled: Boolean) {
        if (enabled) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun openDrawer() {
        drawer.openDrawer(Gravity.RIGHT)
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthListener)
    }

}


