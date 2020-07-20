package com.zone.chatterz

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
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
import com.zone.chatterz.Adapter.FriendsAdapter
import com.zone.chatterz.Adapter.RecentAdapter
import com.zone.chatterz.FirebaseConnection.Connection
import com.zone.chatterz.FirebaseConnection.FirebaseMethods
import com.zone.chatterz.FirebaseConnection.RequestCallback
import com.zone.chatterz.Model.Chat
import com.zone.chatterz.Model.User
import com.zone.chatterz.Notification.Token

open class ChatActivity : AppCompatActivity() {

    private lateinit var message_recyclerView: RecyclerView
    private lateinit var recentProgressBar: ProgressBar
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var mUsers: MutableList<User>
    private lateinit var recentAdapter: RecentAdapter
    private lateinit var usersList: MutableList<String>
    private lateinit var drawerOnline: DrawerLayout
    private lateinit var naviagtionOnline: NavigationView
    private lateinit var content: LinearLayout
    private lateinit var onlineBtn: ImageView
    private lateinit var onlineRecyclerView: RecyclerView
    private lateinit var headerView: View
    private lateinit var mOnlineUser: MutableList<User>
    private lateinit var friendsAdapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recentProgressBar = findViewById(R.id.recentProgressBar)
        message_recyclerView = findViewById(R.id.recent_RecyclerView)
        drawerOnline = findViewById(R.id.drawerChats)
        naviagtionOnline = findViewById(R.id.activeOnlineMenu)
        content = findViewById(R.id.contentOnline)
        onlineBtn = findViewById(R.id.onlinestatus)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        headerView = naviagtionOnline.getHeaderView(0)
        onlineRecyclerView = headerView.findViewById(R.id.followerRecyclerView)

        setDrawerHalf()

        drawerOnline.setScrimColor(Color.TRANSPARENT)
        drawerOnline.addDrawerListener(object : ActionBarDrawerToggle(
            this
            , drawerOnline, R.string.openDrawer, R.string.closeDrawer
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slidex = drawerView.width * slideOffset
                content.translationX = -slidex
            }
        })
        /*onlineBtn.setOnClickListener {
            drawerOnline.openDrawer(Gravity.RIGHT)
            startOnlineView()

        }*/
        readRecentChats()
    }

    private fun readRecentChats() {
        this.usersList = mutableListOf()
        message_recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        message_recyclerView.layoutManager = linearLayoutManager
        FirebaseMethods.addValueEventChild(Connection.userChats,object : RequestCallback(){
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
        FirebaseMethods.addValueEvent(Connection.userRef,object : RequestCallback(){
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
                    recentAdapter = RecentAdapter(getContext, mUsers)
                    message_recyclerView.adapter = recentAdapter
                }
                recentProgressBar.visibility = View.GONE
                message_recyclerView.visibility = View.VISIBLE
            }
        })
    }

   /* private fun readFriendsOnline() {
        val list = mutableListOf<String>()
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Friends").child(Connection.user)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val friends = data.key!!
                    list.add(friends)
                }
                val dataRef = FirebaseDatabase.getInstance().getReference("Users")
                dataRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (data in p0.children) {
                            val user = data.getValue(User::class.java)
                            if (user != null && list.contains(user.id) && user.status.equals("online")) {
                                mUsers.add(user)
                            }
                        }
                        val getContext = this@ChatActivity
                        friendsAdapter = FriendsAdapter(getContext, mUsers)
                        onlineRecyclerView.adapter = friendsAdapter
                    }
                })
            }
        })
    }*/

    private fun updateToken(token : String){
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

}
