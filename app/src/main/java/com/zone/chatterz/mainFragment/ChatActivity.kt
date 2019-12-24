package com.zone.chatterz.mainFragment

import android.graphics.Color
import com.zone.chatterz.Interfaces.DrawerLocker
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.FriendsAdapter
import com.zone.chatterz.Adapter.RecentAdapter
import com.zone.chatterz.Model.Chat
import com.zone.chatterz.Model.User
import com.zone.chatterz.R

open class ChatActivity : Fragment() {

    private lateinit var status_recyclerView: RecyclerView
    private lateinit var message_recyclerView: RecyclerView
    private lateinit var recentProgressBar: ProgressBar
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var mUsers: MutableList<User>
    private lateinit var recentAdapter: RecentAdapter
    private lateinit var usersList: MutableList<String>
    private lateinit var drawerOnline: DrawerLayout
    private lateinit var naviagtionOnline: NavigationView
    private lateinit var content: RelativeLayout
    private lateinit var onlineBtn: ImageView
    private lateinit var onlineRecyclerView: RecyclerView
    private lateinit var headerView: View
    private lateinit var mOnlineUser: MutableList<User>
    private lateinit var friendsAdapter: FriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View = inflater.inflate(R.layout.fragment_chat, container, false)

        (activity as DrawerLocker).setDrawerLockerEnabled(false)

        recentProgressBar = view.findViewById(R.id.recentProgressBar)
        status_recyclerView = view.findViewById(R.id.userStatus_RecyclerView)
        message_recyclerView = view.findViewById(R.id.recent_RecyclerView)
        drawerOnline = view.findViewById(R.id.drawerChats)
        naviagtionOnline = view.findViewById(R.id.activeOnlineMenu)
        content = view.findViewById(R.id.contentOnline)
        onlineBtn = view.findViewById(R.id.onlinestatus)

        headerView = naviagtionOnline.getHeaderView(0)
        onlineRecyclerView = headerView.findViewById(R.id.followerRecyclerView)

        setDrawerHalf()

        drawerOnline.setScrimColor(Color.TRANSPARENT)
        drawerOnline.addDrawerListener(object : ActionBarDrawerToggle(
            this.activity
            , drawerOnline, R.string.openDrawer, R.string.closeDrawer
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slidex = drawerView.width * slideOffset
                content.translationX = -slidex
            }
        })
        onlineBtn.setOnClickListener {
            drawerOnline.openDrawer(Gravity.RIGHT)
            startOnlineView()

        }
        readRecentChats()
        return view
    }

    private fun readRecentChats(){
        usersList = mutableListOf()
        message_recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this.context)
        message_recyclerView.layoutManager = linearLayoutManager
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                recentProgressBar.visibility = View.VISIBLE
                usersList.clear()
                for (dataSet in p0.children) {
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
            }
        })
    }

    private fun startOnlineView() {
        onlineRecyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this.context)
        onlineRecyclerView.layoutManager = linearLayoutManager
        mOnlineUser = mutableListOf()
        readFriendsOnline()
    }

    private fun setDrawerHalf() {
        val width = resources.displayMetrics.widthPixels / 2
        var params = naviagtionOnline.layoutParams as DrawerLayout.LayoutParams
        params.width = width
        naviagtionOnline.layoutParams = params
    }


    private fun readUser() {
        mUsers = mutableListOf()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                mUsers.clear()
                for (dataSet in p0.children) {
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
                val getContext = context
                if (getContext != null) {
                    recentAdapter = RecentAdapter(getContext, mUsers)
                    message_recyclerView.adapter = recentAdapter
                }
                recentProgressBar.visibility = View.GONE
                message_recyclerView.visibility = View.VISIBLE
            }
        })
    }

    private fun readFriendsOnline() {
        val list = mutableListOf<String>()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.uid)
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
                        val getContext = context!!
                        friendsAdapter = FriendsAdapter(getContext, mUsers)
                        onlineRecyclerView.adapter = friendsAdapter
                    }
                })
            }
        })
    }

    /* private fun TabLayout.setTabswithCustomWidth(tabPosition: Int) {
         //Take LinearLayout of last tab and change there weight to the .4f
         val linearLayout = (tabBarLayout.getChildAt(0) as LinearLayout).getChildAt(tabPosition) as LinearLayout
         val customParams = linearLayout.layoutParams as LinearLayout.LayoutParams
         customParams.weight = .4f
         linearLayout.layoutParams = customParams

     }*/

}
