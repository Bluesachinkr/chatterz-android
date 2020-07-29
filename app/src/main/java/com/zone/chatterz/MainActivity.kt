package com.zone.chatterz

import com.zone.chatterz.inferfaces.DrawerLocker
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
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.User
import com.zone.chatterz.preActivities.WelcomeActivity
import com.zone.chatterz.settings.GeneralSettings
import com.zone.chatterz.mainFragment.CreatePostActivity
import com.zone.chatterz.mainFragment.HomeActivity
import com.zone.chatterz.mainFragment.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DrawerLocker{

    private lateinit var drawer: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var content: RelativeLayout
    private lateinit var navigationDrawerMenu: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.mAuth = FirebaseAuth.getInstance()
        this.drawer = findViewById(R.id.drawerLayout)
        this.content = findViewById(R.id.contentLayout)
        this.navigationDrawerMenu = findViewById(R.id.NavigationDrawerMenu)
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
            bottomNavigationBar.menu.getItem(0).setIcon(R.drawable.ic_outline_home_bottom_nav)
            bottomNavigationBar.menu.getItem(1).setIcon(R.drawable.ic_create)
            bottomNavigationBar.menu.getItem(2).setIcon(R.drawable.search_light_icon)
            when (menuItem.itemId) {

                R.id.home -> {
                    val homeFragment = HomeActivity(this)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main,homeFragment)
                        .addToBackStack(null).commit()
                    menuItem.setIcon(R.drawable.ic_home_bottom_nav)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.create -> {
                    startActivity(Intent(this,CreatePostActivity::class.java))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile_bottombar -> {
                    val searchActivity = SearchActivity()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main, searchActivity)
                        .addToBackStack(null).commit()
                    menuItem.setIcon(R.drawable.search_dark_icon)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        NavigationDrawerMenu.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.logout -> {
                    mAuth.signOut()
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
        bottomNavigationBar.selectedItemId = R.id.home
        val homeFragment = HomeActivity(this)
        supportFragmentManager.beginTransaction().add(R.id.container_main,homeFragment).commit()
        bottomNavigationBar.menu.getItem(0).setIcon(R.drawable.ic_home_bottom_nav)
    }

    private fun updateOnlineStatus() {
        FirebaseMethods.singleValueEventChild(Connection.userRef,object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                var user = dataSnapshot.getValue(User::class.java)
                user.let {
                    val hashMap = HashMap<String, Any>()
                    hashMap.put("status", "online")
                    dataSnapshot.ref.updateChildren(hashMap)
                    dataSnapshot.ref.child("status").onDisconnect().setValue("offline")
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


