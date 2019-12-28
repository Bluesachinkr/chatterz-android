package com.zone.chatterz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.GroupRecentAdapter
import com.zone.chatterz.FirebaseConnection.Connection
import com.zone.chatterz.Model.Group

class GroupChatsActivity : Fragment() {

    private lateinit var groupChatView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var mGroupList: MutableList<Group>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_group_chats_view, container, false)
        mAuth = FirebaseAuth.getInstance()
        groupChatView = view.findViewById(R.id.groupChatRecent_RecyclerView)
        progressBar = view.findViewById(R.id.groupChatRecentProgressBar)
        mGroupList = mutableListOf()
        val groupList = createGroupList()
        loadGroupChatView(groupList)
        return view
    }

    private fun createGroupList(): MutableList<String> {
        firebaseUser = mAuth.currentUser!!
        val groupList = mutableListOf<String>()
        val layoutManager = LinearLayoutManager(context)
        groupChatView.layoutManager = layoutManager
        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.groupJoinedRef)
            .child(firebaseUser.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (values in p0.children) {
                    val groupId = values.key.toString()
                    groupList.add(groupId)
                }
            }
        })
        return groupList
    }

    private fun loadGroupChatView(groupList: List<String>) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.groupsRef)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val values = data.getValue(Group::class.java)
                    if (values != null && groupList.contains(values.id)) {
                        mGroupList.add(values)
                    }
                }
                context?.let {
                    val adapter = GroupRecentAdapter(it, mGroupList)
                    groupChatView.adapter = adapter
                    progressBar.visibility = View.GONE
                    groupChatView.visibility = View.VISIBLE
                }
            }
        })
    }

}
