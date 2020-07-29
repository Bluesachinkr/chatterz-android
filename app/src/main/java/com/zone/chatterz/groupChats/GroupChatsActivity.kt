package com.zone.chatterz.groupChats

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.adapter.GroupRecentAdapter
import com.zone.chatterz.CreateNewGroup
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.model.Group
import com.zone.chatterz.R

class GroupChatsActivity : Fragment() {

    private lateinit var groupChatView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var mGroupList: MutableList<Group>
    private lateinit var toolbarGroup: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_group_chats_view, container, false)
        groupChatView = view.findViewById(R.id.groupChatRecent_RecyclerView)
        progressBar = view.findViewById(R.id.groupChatRecentProgressBar)
        toolbarGroup = view.findViewById(R.id.toolbarGroupChatsView)
        //setiing mAuth as FirebaseAuth instance in the activity
        mAuth = FirebaseAuth.getInstance()
        //toolbar as actionBar
        (activity as AppCompatActivity).setSupportActionBar(toolbarGroup)
        toolbarGroup.setTitle("")
        setHasOptionsMenu(true)
        //Showing list view of all recent group chats
        mGroupList = mutableListOf()
        val layoutManager = LinearLayoutManager(context)
        groupChatView.layoutManager = layoutManager
        createGroupList()

        return view
    }

    /*override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.let { it.inflate(R.menu.appbar_group_chats_view,menu) }
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
    }*/

    private fun createGroupList() {
        firebaseUser = mAuth.currentUser!!
        val groupList = mutableListOf<String>()
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
                loadGroupChatView(groupList)
            }
        })
    }

    private fun loadGroupChatView(groupList: List<String>) {
        progressBar.visibility = View.VISIBLE
        if (groupList.size != 0) {
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
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(this.context, "No Groups Joined", Toast.LENGTH_LONG).show()
        }
    }

}
