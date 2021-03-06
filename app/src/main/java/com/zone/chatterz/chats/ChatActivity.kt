package com.zone.chatterz.chats

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.zone.chatterz.adapter.ChatRecentAdapter
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.model.User
import com.zone.chatterz.notification.Token
import com.zone.chatterz.R
import com.zone.chatterz.adapter.OnlineFriendAdapter
import com.zone.chatterz.inferfaces.ChatControlListener
import java.io.File

open class ChatActivity(mContext : Context,listener : ChatControlListener) : Fragment(), View.OnClickListener {

    private val mContext =  mContext
    private val listener = listener
    private lateinit var message_recyclerView: RecyclerView
    private lateinit var open_group_FloatingButton : FloatingActionButton
    private lateinit var databaseReference: DatabaseReference
    private lateinit var chatRecentAdapter: ChatRecentAdapter
    private lateinit var usersList: MutableList<String>
    private lateinit var drawerOnline: DrawerLayout
    private lateinit var naviagtionOnline: NavigationView
    private lateinit var content: LinearLayout
    private lateinit var new_chat_to_users : ImageView
    private lateinit var onlineRecyclerView: RecyclerView
    private lateinit var headerView: View
    private lateinit var mOnlineUser: MutableList<User>
    private lateinit var onlineAdapter : OnlineFriendAdapter

    private lateinit var mRefreshLayoutChat : SwipeRefreshLayout
    private lateinit var mSwipeRefreshListener : SwipeRefreshLayout.OnRefreshListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_chat,container,false)
        message_recyclerView = view.findViewById(R.id.recent_RecyclerView)
        content = view.findViewById(R.id.contentOnline)
        new_chat_to_users = view.findViewById(R.id.new_chat_to_users)
        open_group_FloatingButton = view.findViewById(R.id.open_group_FloatingButton)

        mRefreshLayoutChat = view.findViewById(R.id.swipe_refresh_recent_chats)
        mSwipeRefreshListener = SwipeRefreshLayout.OnRefreshListener {
            readRecentChats()
        }
        mRefreshLayoutChat.setOnRefreshListener(mSwipeRefreshListener)

        onlineRecyclerView = view.findViewById(R.id.onlineRecyclerView)

        mSwipeRefreshListener.onRefresh()
        new_chat_to_users.setOnClickListener(this)
        open_group_FloatingButton.setOnClickListener(this)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
           R.id.chats_refresh->{
               mRefreshLayoutChat.isRefreshing = true
               readRecentChats()
               return true
           }
            else->{
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun readRecentChats() {
        this.usersList = mutableListOf()
        message_recyclerView.adapter = null
        message_recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(mContext)
        message_recyclerView.layoutManager = linearLayoutManager
        FirebaseMethods.addValueEvent(Connection.userChats+ File.separator+Connection.user, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                usersList.clear()
                for (dataSet in dataSnapshot.children) {
                    val str = dataSet.key.toString()
                    usersList.add(str)
                }
                chatRecentAdapter = ChatRecentAdapter(mContext, usersList)
                message_recyclerView.adapter = chatRecentAdapter
                FirebaseInstanceId.getInstance().getToken()?.let { updateToken(it) }
                mRefreshLayoutChat.isRefreshing = false
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
            new_chat_to_users -> {
                listener.newMessage()
            }
            open_group_FloatingButton->{
                listener.openGroup()
            }
            else -> {
                return
            }
        }
    }

}
