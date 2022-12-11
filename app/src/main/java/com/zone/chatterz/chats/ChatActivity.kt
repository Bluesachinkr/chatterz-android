package com.zone.chatterz.chats

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.zone.chatterz.R
import com.zone.chatterz.adapter.ChatRecentAdapter
import com.zone.chatterz.adapter.OnlineFriendAdapter
import com.zone.chatterz.base.BaseFragment
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.databinding.ActivityChatBinding
import com.zone.chatterz.inferfaces.ChatControlListener
import com.zone.chatterz.model.User
import com.zone.chatterz.notification.Token
import java.io.File

open class ChatActivity(mContext: Context, listener: ChatControlListener) :
    BaseFragment<ActivityChatBinding>(), View.OnClickListener {

    private val mContext = mContext
    private val listener = listener
    private lateinit var databaseReference: DatabaseReference
    private lateinit var chatRecentAdapter: ChatRecentAdapter
    private lateinit var usersList: MutableList<String>
    private lateinit var drawerOnline: DrawerLayout
    private lateinit var naviagtionOnline: NavigationView
    private lateinit var headerView: View
    private lateinit var mOnlineUser: MutableList<User>
    private lateinit var onlineAdapter: OnlineFriendAdapter

    private lateinit var mSwipeRefreshListener: SwipeRefreshLayout.OnRefreshListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSwipeRefreshListener = SwipeRefreshLayout.OnRefreshListener {
            readRecentChats()
        }
        binding.swipeRefreshRecentChats.setOnRefreshListener(mSwipeRefreshListener)
        mSwipeRefreshListener.onRefresh()
        binding.newChatToUsers.setOnClickListener(this)
        binding.openGroupFloatingButton.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.chats_refresh -> {
                binding.swipeRefreshRecentChats.isRefreshing = true
                readRecentChats()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun readRecentChats() {
        with(binding) {
            usersList = mutableListOf()
            recentRecyclerView.adapter = null
            recentRecyclerView.setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(mContext)
            recentRecyclerView.layoutManager = linearLayoutManager
            FirebaseMethods.addValueEvent(
                Connection.userChats + File.separator + Connection.user,
                object : RequestCallback() {
                    override fun onDataChanged(dataSnapshot: DataSnapshot) {
                        usersList.clear()
                        for (dataSet in dataSnapshot.children) {
                            val str = dataSet.key.toString()
                            usersList.add(str)
                        }
                        chatRecentAdapter = ChatRecentAdapter(mContext, usersList)
                        recentRecyclerView.adapter = chatRecentAdapter
                        FirebaseInstanceId.getInstance().getToken()?.let { updateToken(it) }
                        binding.swipeRefreshRecentChats.isRefreshing = false
                    }
                })
        }
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

    /*private fun readFriendsOnline() {
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
    }*/

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

    override fun onClick(v: View?) {
        when (v) {
            binding.newChatToUsers -> {
                listener.newMessage()
            }
            binding.openGroupFloatingButton -> {
                listener.openGroup()
            }
            else -> {
                return
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_chat

}
