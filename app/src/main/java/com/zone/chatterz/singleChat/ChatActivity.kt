package com.zone.chatterz.singleChat

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.zone.chatterz.adapter.FriendsAdapter
import com.zone.chatterz.adapter.ChatRecentAdapter
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Chat
import com.zone.chatterz.model.User
import com.zone.chatterz.notification.Token
import com.zone.chatterz.R
import com.zone.chatterz.adapter.OnlineFriendAdapter

open class ChatActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var message_recyclerView: RecyclerView
    private lateinit var recentProgressBar: ProgressBar
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var mUsers: MutableList<User>
    private lateinit var chatRecentAdapter: ChatRecentAdapter
    private lateinit var usersList: MutableList<String>
    private lateinit var drawerOnline: DrawerLayout
    private lateinit var naviagtionOnline: NavigationView
    private lateinit var content: LinearLayout
    private lateinit var onlineBtn: ImageView
    private lateinit var back_chat: ImageView
    private lateinit var onlineRecyclerView: RecyclerView
    private lateinit var headerView: View
    private lateinit var mOnlineUser: MutableList<User>
    private lateinit var onlineAdapter : OnlineFriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recentProgressBar = findViewById(R.id.recentProgressBar)
        message_recyclerView = findViewById(R.id.recent_RecyclerView)
        content = findViewById(R.id.contentOnline)
        onlineBtn = findViewById(R.id.onlinestatus)
        back_chat = findViewById(R.id.back_chat)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

      /*  onlineRecyclerView = headerView.findViewById(R.id.onlineRecyclerView)*/

       /* setDrawerHalf()*/

      /*  drawerOnline.setScrimColor(Color.TRANSPARENT)
        drawerOnline.addDrawerListener(object : ActionBarDrawerToggle(
            this
            , drawerOnline,
            R.string.openDrawer,
            R.string.closeDrawer
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slidex = drawerView.width * slideOffset
                content.translationX = -slidex
            }
        })*/
        /*onlineBtn.setOnClickListener {
            drawerOnline.openDrawer(Gravity.RIGHT)
            startOnlineView()

        }*/
        back_chat.setOnClickListener(this)
        readRecentChats()
    }

    private fun readRecentChats() {
        this.usersList = mutableListOf()
        message_recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        message_recyclerView.layoutManager = linearLayoutManager
        FirebaseMethods.addValueEventChild(Connection.userChats, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                recentProgressBar.visibility = View.VISIBLE
                usersList.clear()
                for (dataSet in dataSnapshot.children) {
                    val chat = dataSet.getValue(Chat::class.java)
                    if (chat != null) {
                        if (chat.sender.equals(firebaseUser.uid)) {
                            usersList.add(chat.receiver)
                        }
                        if (chat.receiver.equals(firebaseUser.uid)) {
                            usersList.add(chat.sender)
                        }
                    }
                }
                readUser()
                FirebaseInstanceId.getInstance().getToken()?.let { updateToken(it) }
            }
        })
    }

    /* private fun startOnlineView() {
         onlineRecyclerView.setHasFixedSize(true)
         val linearLayoutManager = LinearLayoutManager(this)
         onlineRecyclerView.layoutManager = linearLayoutManager
         mOnlineUser = mutableListOf()
         readFriendsOnline()
     }*/

    private fun setDrawerHalf() {
        val width = resources.displayMetrics.widthPixels / 2
        var params = naviagtionOnline.layoutParams as DrawerLayout.LayoutParams
        params.width = width
        naviagtionOnline.layoutParams = params
    }


    private fun readUser() {
        mUsers = mutableListOf()
        FirebaseMethods.addValueEvent(Connection.userRef, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (dataSet in dataSnapshot.children) {
                    val user = dataSet.getValue(User::class.java)
                    if (user != null) {
                        for (userId in usersList) {
                            if (userId.equals(user.id)) {
                                if (!mUsers.contains(user)) {
                                    mUsers.add(user)
                                }
                            }
                        }
                    }
                }
                val getContext = this@ChatActivity
                if (getContext != null) {
                    chatRecentAdapter = ChatRecentAdapter(getContext, mUsers)
                    message_recyclerView.adapter = chatRecentAdapter
                }
                recentProgressBar.visibility = View.GONE
                message_recyclerView.visibility = View.VISIBLE
            }
        })
    }

     private fun readFriendsOnline() {
         val list = mutableListOf<String>()
         FirebaseMethods.addValueEventChild(Connection.friendRef,object :RequestCallback(){
             override fun onDataChanged(dataSnapshot: DataSnapshot) {
                 for (data in dataSnapshot.children) {
                     val friends = data.key!!
                     list.add(friends)
                 }
                 FirebaseMethods.addValueEvent(Connection.userRef,object : RequestCallback(){
                     override fun onDataChanged(dataSnapshot: DataSnapshot) {
                         for (data in dataSnapshot.children) {
                             val user = data.getValue(User::class.java)
                             if (user != null && list.contains(user.id) && user.status.equals("online")) {
                                 mUsers.add(user)
                             }
                         }
                         val getContext = this@ChatActivity
                         onlineAdapter = OnlineFriendAdapter(getContext, mUsers)
                         onlineRecyclerView.adapter = onlineAdapter
                     }
                 })
             }
         })
     }

    private fun updateToken(token: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        val updateToken = Token(token)
        databaseReference.child(Connection.user).setValue(updateToken)
    }

    /* private fun TabLayout.setTabswithCustomWidth(tabPosition: Int) {
         //Take LinearLayout of last tab and change there weight to the .4f
         val linearLayout = (tabBarLayout.getChildAt(0) as LinearLayout).getChildAt(tabPosition) as LinearLayout
         val customParams = linearLayout.layoutParams as LinearLayout.LayoutParams
         customParams.weight = .4f
         linearLayout.layoutParams = customParams

     }*/

    override fun onBackPressed() {
        finish();
    }

    override fun onClick(v: View?) {
        when (v) {
            back_chat -> {
                onBackPressed()
            }
            else -> {
                return
            }
        }
    }

}
