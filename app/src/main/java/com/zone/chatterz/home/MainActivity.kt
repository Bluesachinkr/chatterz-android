package com.zone.chatterz.home

import android.content.Context
import com.zone.chatterz.inferfaces.DrawerLocker
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.R
import com.zone.chatterz.adapter.HomeAdapter
import com.zone.chatterz.camera.CameraChatterz
import com.zone.chatterz.data.UserData
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.group.GroupChatsActivity
import com.zone.chatterz.inferfaces.ChatControlListener
import com.zone.chatterz.model.User
import com.zone.chatterz.preActivities.WelcomeActivity
import com.zone.chatterz.settings.EditProfileActivity
import com.zone.chatterz.common.Timings
import com.zone.chatterz.chats.ChatActivity
import com.zone.chatterz.chats.NewMessageFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DrawerLocker, HomeActivity.NavigationControls,
    ChatControlListener {

    private val mContext = this
    private lateinit var drawer: DrawerLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var content: RelativeLayout
    private lateinit var navigationDrawerMenu: NavigationView
    private lateinit var bottomnav_main_screen: BottomNavigationView
    private lateinit var main_activity_appbar: RelativeLayout
    private lateinit var profile_main_layout: RelativeLayout
    private lateinit var search_view_main: RelativeLayout

    private var commentReadTextOpen = false
    private var postId: String = ""

    private var isGroupEnabled = false
    private var isChatsEnabled = true

    //add coment
    private lateinit var add_comment_post: ImageView
    private lateinit var profile_image_main_frag: CircularImageView
    private lateinit var comment_edittext_comment_add: EditText
    private lateinit var comment_layout_home: RelativeLayout
    private lateinit var camera_btn_layout_main: RelativeLayout
    private lateinit var camera_btn_main_frag: ImageView

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
        this.camera_btn_layout_main = findViewById(R.id.camera_main_activity)
        this.camera_btn_main_frag = findViewById(R.id.camera_btn_main_frag)
        this.profile_image_main_frag = findViewById(R.id.profile_image_main_frag)
        add_comment_post = findViewById(R.id.add_comment_post)
        comment_edittext_comment_add = findViewById(R.id.comment_edittext_add_comment)
        comment_layout_home = findViewById(R.id.comment_layout_home)
        this.search_view_main = findViewById(R.id.search_view_main)
        this.profile_main_layout = findViewById(R.id.profile_main_layout)
        this.main_activity_appbar = findViewById(R.id.main_activity_appbar)
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

        //setonClickListener
        profile_main_layout.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("for", "myOwn")
            intent.putExtra("user", Connection.user)
            startActivity(intent)
        }
        camera_btn_layout_main.setOnClickListener {
            startActivity(Intent(this, CameraChatterz::class.java))
        }

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

        this.search_view_main.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        setNavigationInitially()

        bottomnav_main_screen.setOnNavigationItemSelectedListener { menuItem ->
            bottomnav_main_screen.menu.getItem(0).setIcon(R.drawable.ic_outline_home_bottom_nav)
            bottomnav_main_screen.menu.getItem(1).setIcon(R.drawable.ic_explore_icon_bottom_main)
            bottomnav_main_screen.menu.getItem(2).setIcon(R.drawable.ic_create)
            bottomnav_main_screen.menu.getItem(3).setIcon(R.drawable.ic_heart)
            bottomnav_main_screen.menu.getItem(4).setIcon(R.drawable.ic_chat)
            when (menuItem.itemId) {

                R.id.home -> {
                    val homeFragment = HomeActivity(this, this)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main, homeFragment, "HomeFragment")
                        .addToBackStack(null).commit()
                    menuItem.setIcon(R.drawable.ic_home_bottom_nav)
                    main_activity_appbar.visibility = View.VISIBLE
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.explore_bottombar -> {
                    val exploreActivity = ExploreActivity(this)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main, exploreActivity)
                        .addToBackStack(null).commit()
                    menuItem.setIcon(R.drawable.ic_explore_icon_bottom_main)
                    main_activity_appbar.visibility = View.GONE
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.create_new_post -> {
                    val intent = Intent(this, CreatePostActivity::class.java)
                    intent.putExtra("from", "main")
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.notifications_bottombar -> {

                    main_activity_appbar.visibility = View.GONE
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.chat_bottombar -> {
                    if (!isGroupEnabled && isChatsEnabled) {
                        openChats()
                    }
                    if (isGroupEnabled && !isChatsEnabled) {
                        openGroup()
                    }
                    menuItem.setIcon(R.drawable.ic_chat)
                    main_activity_appbar.visibility = View.GONE
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        navigationDrawerMenu.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    mAuth.signOut()
                    return@setNavigationItemSelectedListener true
                }
                R.id.settings_icon -> {
                    val intent = Intent(this, EditProfileActivity::class.java)
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    return@setNavigationItemSelectedListener false
                }
            }
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
            if (supportFragmentManager.backStackEntryCount == 0) {
                super.onBackPressed()
            } else {
                supportFragmentManager.popBackStack()
            }
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

    override fun openGroup() {
        val chatActivity = GroupChatsActivity(this, this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_main, chatActivity)
            .addToBackStack(null).commit()
        isChatsEnabled = false
        isGroupEnabled = true
    }

    override fun openChats() {
        val chatActivity = ChatActivity(this, this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_main, chatActivity)
            .addToBackStack(null).commit()
        isChatsEnabled = true
        isGroupEnabled = false
    }

    override fun newMessage() {
        val activity = NewMessageFragment(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_main, activity)
            .addToBackStack(null).commit()
    }

    override fun onBack() {
        onBackPressed()
    }

}


