package com.zone.chatterz

import android.content.Context
import com.zone.chatterz.inferfaces.DrawerLocker
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.zone.chatterz.adapter.HomeAdapter
import com.zone.chatterz.data.UserData
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.User
import com.zone.chatterz.preActivities.WelcomeActivity
import com.zone.chatterz.settings.GeneralSettings
import com.zone.chatterz.mainFragment.CreatePostActivity
import com.zone.chatterz.mainFragment.HomeActivity
import com.zone.chatterz.mainFragment.SearchActivity
import com.zone.chatterz.model.Comment
import com.zone.chatterz.requirements.Timings
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), DrawerLocker, HomeActivity.NavigationControls {

    private lateinit var drawer: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var content: RelativeLayout
    private lateinit var navigationDrawerMenu: NavigationView
    private lateinit var bottomnav_main_screen: BottomNavigationView

    private var commentReadTextOpen = false
    private var postId: String = ""

    //add coment
    private lateinit var add_comment_post: ImageView
    private lateinit var comment_edittext_comment_add: EditText
    private lateinit var comment_layout_home: RelativeLayout

    companion object {
        var is_changed = false
        var comment_viewholder_changed: HomeAdapter.Viewholder? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UserData.onUserInfo()

        this.mAuth = FirebaseAuth.getInstance()
        this.drawer = findViewById(R.id.drawerLayout)
        this.content = findViewById(R.id.contentLayout)
        add_comment_post = findViewById(R.id.add_comment_post)
        comment_edittext_comment_add = findViewById(R.id.comment_edittext_add_comment)
        comment_layout_home = findViewById(R.id.comment_layout_home)
        this.bottomnav_main_screen = findViewById(R.id.bottomNavigationBar)
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

        add_comment_post.setOnClickListener {
            addComment()
            closeCommentBox()
            refreshPostComments()
        }

        setNavigationInitially()

        bottomnav_main_screen.setOnNavigationItemSelectedListener { menuItem ->
            bottomNavigationBar.menu.getItem(0).setIcon(R.drawable.ic_outline_home_bottom_nav)
            bottomNavigationBar.menu.getItem(1).setIcon(R.drawable.ic_create)
            bottomNavigationBar.menu.getItem(2).setIcon(R.drawable.search_light_icon)
            when (menuItem.itemId) {

                R.id.home -> {
                    val homeFragment = HomeActivity(this, this)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main, homeFragment, "HomeFragment")
                        .addToBackStack(null).commit()
                    menuItem.setIcon(R.drawable.ic_home_bottom_nav)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.create -> {
                    startActivity(Intent(this, CreatePostActivity::class.java))
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
        bottomnav_main_screen.selectedItemId = R.id.home
        val homeFragment = HomeActivity(this, this)
        supportFragmentManager.beginTransaction()
            .add(R.id.container_main, homeFragment, "HomeFragment").commit()
        bottomNavigationBar.menu.getItem(0).setIcon(R.drawable.ic_home_bottom_nav)
    }

    private fun updateOnlineStatus() {
        FirebaseMethods.singleValueEventChild(Connection.userRef, object : RequestCallback() {
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

    override fun onBackPressed() {
        if (commentReadTextOpen) {
            openNavigation()
            closeCommentBox()

            if (is_changed) {
                comment_viewholder_changed?.let {
                    it.comment_box_post.visibility = View.VISIBLE
                    is_changed = false
                }
                comment_viewholder_changed = null
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun refreshPostComments() {
        comment_viewholder_changed?.let {
            var count = Integer.parseInt(it.no_of_comments_post.text.toString()) + 1
            it.no_of_comments_post.text = count.toString()
            it.comment_box_post.visibility = View.VISIBLE
            val inputManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        openNavigation()
        this.comment_edittext_comment_add.hint = "Write a comment..."
        this.comment_edittext_comment_add.text.clear()
    }

    private fun addComment() {
        val message: String = comment_edittext_comment_add.text.toString()
        val databaseReference =
            FirebaseDatabase.getInstance().getReference(Connection.commentsRef)
                .child(postId)
        val push = databaseReference.push()
        val str = push.key.toString()
        val hashMap = hashMapOf<String, Any>()
        hashMap.put("postId", postId)
        hashMap.put("message", message)
        hashMap.put("sender", Connection.user)
        hashMap.put("likes", 0)
        hashMap.put("heart", false)
        hashMap.put("time", Timings.getCurrentTime())
        hashMap.put("isComment", true)
        hashMap.put("toReply", "none")
        hashMap.put("parent", "none")
        hashMap.put("isReply", 0)
        hashMap.put("commentId", str)
        push.setValue(hashMap)
    }

    fun closeCommentBox() {
        comment_layout_home.visibility = View.GONE
    }

    override fun removeNavigation() {
        this.bottomnav_main_screen.visibility = View.GONE
        this.commentReadTextOpen = true
    }

    override fun openNavigation() {
        this.bottomnav_main_screen.visibility = View.VISIBLE
        this.commentReadTextOpen = false
    }

    override fun openCommentEditext(id: String) {
        postId = id
        removeNavigation()
        comment_layout_home.visibility = View.VISIBLE
    }
}


