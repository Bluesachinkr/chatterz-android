package com.zone.chatterz.groupChats

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.adapter.FriendsAdapter
import com.zone.chatterz.adapter.GroupAdapter
import com.zone.chatterz.adapter.GroupChatAdapter
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.inferfaces.GroupchatControl
import com.zone.chatterz.model.Group
import com.zone.chatterz.model.GroupChats
import com.zone.chatterz.model.User
import com.zone.chatterz.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

open class GroupChatMessageActivity : AppCompatActivity(), View.OnClickListener, GroupchatControl {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var drawer: DrawerLayout
    private lateinit var leftNavigationView: NavigationView
    private lateinit var openDrawerBtn: ImageView
    private lateinit var groupName: TextView
    private lateinit var content: RelativeLayout
    private lateinit var rightNavigationView: NavigationView
    private lateinit var groupListView: RecyclerView
    private lateinit var chatsRecyclerview: RecyclerView
    private lateinit var membersRecyclerView: RecyclerView
    private lateinit var onlineMembersRecyclerView : RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendMsgBtn: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var mGroupList: MutableList<String>
    private lateinit var activeGroup: String

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat_message)
        //getIntent  for groupId and groupName
        val intent = intent
        val arr = intent.getStringArrayListExtra("groupInfo")
        activeGroup = arr.get(0)
        //mAuth for firebase auth instance
        mAuth = FirebaseAuth.getInstance()
        drawer = findViewById(R.id.drawerGroups)
        leftNavigationView = findViewById(R.id.drawerOpen)
        rightNavigationView = findViewById(R.id.drawerMembers)
        openDrawerBtn = findViewById(R.id.drawerOpenBtn)
        groupName = findViewById(R.id.groupName)
        content = findViewById(R.id.contentGroupChats)
        toolbar = findViewById(R.id.groupToolbar)
        chatsRecyclerview = findViewById(R.id.groupChatsRecyclerview)
        messageEditText = findViewById(R.id.editextGrpMessage)
        sendMsgBtn = findViewById(R.id.sendMessageGrpButton)

        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.group_menu_appbar)
        groupName.text = arr.get(1)
        mGroupList = mutableListOf()
        createGroupList()

        drawer.setScrimColor(Color.TRANSPARENT)

        setDrawerWidth()
        drawer.addDrawerListener(object : ActionBarDrawerToggle(
            this,
            drawer,
            R.string.openDrawer,
            R.string.closeDrawer
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val width = drawerView.width * slideOffset
                if (drawerView != rightNavigationView) {
                    content.translationX = width
                }
            }
        })

        openDrawerBtn.setOnClickListener(this)
        sendMsgBtn.setOnClickListener(this)

        if (drawer.isDrawerOpen(leftNavigationView)) {
            showGroups()
        }
        if(drawer.isDrawerOpen(rightNavigationView)){
            showGroupMemberActivity()
        }
        loadGroupChats(activeGroup)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_menu_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.MembersGroup -> {
                drawer.openDrawer(rightNavigationView)
                membersRecyclerView = rightNavigationView.findViewById(R.id.groupMembersOnlineView)
                onlineMembersRecyclerView = rightNavigationView.findViewById(R.id.groupMembersView)
                showGroupMemberActivity()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            openDrawerBtn -> {
                drawer.openDrawer(leftNavigationView)
                groupListView = leftNavigationView.findViewById(R.id.GroupsList)
                groupListView.setHasFixedSize(true)
                val layoutManager = LinearLayoutManager(this)
                groupListView.layoutManager = layoutManager
                showGroups()
            }
            sendMsgBtn -> {
                val message: String = messageEditText.text.toString()
                sendMessageGroup(message)
            }
        }
    }

    private fun showGroupMemberActivity() {
        val mlist = mutableListOf<String>()
        val mOnlineMembers = mutableListOf<User>()
        val mGroupMembers = mutableListOf<User>()
        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.groupMemRef).child(activeGroup)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                mlist.clear()
                for(values in p0.children){
                    mlist.add(values.key.toString())
                }
                val reference = FirebaseDatabase.getInstance().getReference(Connection.userRef)
                reference.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        mGroupMembers.clear()
                        mOnlineMembers.clear()
                        for (values in p0.children){
                            val user = values.getValue(User::class.java)
                            if(user!=null && mlist.contains(user.id)){
                                mOnlineMembers.add(user)
                            }
                        }
                        showMembers(mGroupMembers)
                        onlineMembers(mOnlineMembers)
                    }
                })
            }
        })
    }

    private fun showGroups() {
        firebaseUser = mAuth.currentUser!!
        val grpList = mutableListOf<Group>()
        val addgrp = Group()
        addgrp.groupImgUrl = "add"
        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.groupsRef)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (values in p0.children) {
                    if (mGroupList.contains(values.ref.key.toString())) {
                        val data = values.getValue(Group::class.java)
                        data?.let { grpList.add(data) }
                    }
                }
                grpList.add(addgrp)

                val adapter = GroupAdapter(
                    this@GroupChatMessageActivity,
                    grpList,
                    this@GroupChatMessageActivity
                )
                groupListView.adapter = adapter

            }
        })
    }

    private fun sendMessageGroup(message: String) {
        firebaseUser = mAuth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.groupChats)
            .child(activeGroup)
        val hashMap = HashMap<String, Any>()
        hashMap.put("sender", firebaseUser.uid)
        hashMap.put("message", message)
        hashMap.put("dateTime", getTime())

        messageEditText.setText("")
        databaseReference.push().setValue(hashMap)
    }

    private fun getTime(): String {
        val sd = SimpleDateFormat("hh:mm yyyy-MM-dd z")
        sd.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val date = Date()
        val cur_date = sd.format(date)
        return cur_date
    }

    override fun loadGroupChats(id: String) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatsRecyclerview.layoutManager = layoutManager
        val mGroupChat = mutableListOf<GroupChats>()
        databaseReference =
            FirebaseDatabase.getInstance().getReference(Connection.groupChats).child(id)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                mGroupChat.clear()
                for (data in p0.children) {
                    val values = data.getValue(GroupChats::class.java)
                    if (values != null) {
                        mGroupChat.add(values)
                    }
                }
                val adapter = GroupChatAdapter(this@GroupChatMessageActivity, mGroupChat)
                chatsRecyclerview.adapter = adapter
            }
        })
    }

    private fun createGroupList() {
        firebaseUser = mAuth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.groupJoinedRef)
            .child(firebaseUser.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                mGroupList.clear()
                for (values in p0.children) {
                    val groupId = values.key.toString()
                    mGroupList.add(groupId)
                }
            }
        })
    }

    private fun setDrawerWidth() {
        val width = resources.displayMetrics.widthPixels / 4
        var params = leftNavigationView.layoutParams as DrawerLayout.LayoutParams
        params.width = width
        leftNavigationView.layoutParams = params
    }

    private fun showMembers(members : List<User>) {
        val layoutManager = LinearLayoutManager(this)
        membersRecyclerView.layoutManager = layoutManager
        val adapter = FriendsAdapter(this,members)
        membersRecyclerView.adapter = adapter
    }

    private fun onlineMembers(members : List<User>){
        val layout: LinearLayout = rightNavigationView.findViewById(R.id.OnlineLayoutView)
        if(members.size>0) {
            layout.visibility = View.VISIBLE
            val count : TextView = rightNavigationView.findViewById(R.id.onlineMembersCount)
            count.text = members.size.toString()
            val layoutManager = LinearLayoutManager(this)
            onlineMembersRecyclerView.layoutManager = layoutManager
            val adapter = FriendsAdapter(this,members)
            membersRecyclerView.adapter = adapter
        }else{
            layout.visibility= View.GONE
        }
    }

    override fun setActive(id: String) {
        this.activeGroup = id
    }

}
