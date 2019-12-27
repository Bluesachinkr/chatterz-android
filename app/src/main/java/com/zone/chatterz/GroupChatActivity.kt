package com.zone.chatterz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.GroupAdapter
import com.zone.chatterz.FirebaseConnection.Connection
import com.zone.chatterz.Interfaces.DrawerLocker
import com.zone.chatterz.Model.Group
import kotlinx.android.synthetic.main.left_group_navigation_view.*

open class GroupChatActivity : Fragment(), View.OnClickListener {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var drawer: DrawerLayout
    private lateinit var leftNavigationView: NavigationView
    private lateinit var openDrawerBtn: ImageView
    private lateinit var openMembersDrawer: ImageView
    private lateinit var groupName: TextView
    private lateinit var content: RelativeLayout
    private lateinit var rightNavigationView: NavigationView
    private lateinit var groupListView: RecyclerView
    private lateinit var chatsRecyclerview : RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendMsgBtn : ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var mGroupList : MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.activity_group_chat, container, false)
        mAuth = FirebaseAuth.getInstance()

        drawer = view.findViewById(R.id.drawerGroups)
        leftNavigationView = view.findViewById(R.id.drawerOpen)
        rightNavigationView = view.findViewById(R.id.drawerMembers)
        openDrawerBtn = view.findViewById(R.id.drawerOpenBtn)
        openMembersDrawer = view.findViewById(R.id.membersBtn)
        groupName = view.findViewById(R.id.groupName)
        content = view.findViewById(R.id.contentGroupChats)
        toolbar = view.findViewById(R.id.groupToolbar)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as DrawerLocker).setDrawerLockerEnabled(false)

        createGroupList()

        drawer.setScrimColor(Color.TRANSPARENT)

        setDrawerWidth()
        drawer.addDrawerListener(object : ActionBarDrawerToggle(
            this.activity,
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
        openMembersDrawer.setOnClickListener(this)

        if(drawer.isDrawerOpen(leftNavigationView)){
            showGroups()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.group_menu_appbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.addNewgroup -> {
                val intetnt = Intent(this.activity, CreateNewGroup::class.java)
                startActivity(intetnt)
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
                val layoutManager = LinearLayoutManager(this.context)
                groupListView.layoutManager = layoutManager
                showGroups()
            }
            openMembersDrawer -> {
                drawer.openDrawer(rightNavigationView)
                //showMembers()
            }
        }
    }

    private fun showGroups() {
        firebaseUser = mAuth.currentUser!!
        val grpList = mutableListOf<Group>()
        val addgrp = Group()
        addgrp.groupImgUrl="add"
        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.groupsRef)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
               for ( values in p0.children){
                   if(mGroupList.contains(values.ref.key.toString())){
                       val data = values.getValue(Group::class.java)
                      data?.let { grpList.add(data) }
                   }
               }
                grpList.add(addgrp)
                context?.let {
                    val adapter = GroupAdapter(it,grpList)
                    groupListView.adapter = adapter
                }
            }
        })
    }

    private fun sendMessageGroup(message : String){
        firebaseUser = mAuth.currentUser!!

    }

    private fun loadGroupChats(){
        firebaseUser = mAuth.currentUser!!
        val mChats = mutableListOf<GroupChat>()
        databaseReference  = FirebaseDatabase.getInstance().getReference(Connection.groupChats).child(mGroupList.get(0))
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

            }
        })
    }

    private fun createGroupList(){
        mGroupList = mutableListOf()
        firebaseUser = mAuth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.groupJoinedRef).child(firebaseUser.uid)
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for ( values in p0.children){
                    val groupId = values.key.toString()
                    mGroupList.add(groupId)
                }
            }
        })
    }

    private fun setDrawerWidth() {
        val width = 160
        var params = leftNavigationView.layoutParams as DrawerLayout.LayoutParams
        params.width = width
        leftNavigationView.layoutParams = params
    }

    private fun showMembers() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

   /* private fun showGroupChats(groupId : String){
        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.groupChats).child(groupId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {

            }
        })
    }*/
}
