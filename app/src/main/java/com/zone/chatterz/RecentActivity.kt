package com.zone.chatterz

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.RecentAdapter
import com.zone.chatterz.Model.User


open class RecentActivity : Fragment() {

    private lateinit var status_recyclerView: RecyclerView
    private lateinit var message_recyclerView: RecyclerView

    private lateinit var userList: MutableList<User>
    private lateinit var recentAdapter: RecentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_recent, container, false)

        status_recyclerView = view.findViewById(R.id.userStatus_RecyclerView)
        message_recyclerView = view.findViewById(R.id.recent_RecyclerView)
        message_recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this.context)
        message_recyclerView.layoutManager = linearLayoutManager
        userList = mutableListOf()
        readUser()

        return view
    }

    private fun readUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for (dataSet in p0.children) {
                    val user = dataSet.getValue(User::class.java)
                    if (firebaseUser != null && user != null) {
                        if (!user.id.equals(firebaseUser.uid)) {
                            userList.add(user)
                        }
                    }
                }
                val getcontext = context
                if(getcontext!=null) {
                    recentAdapter = RecentAdapter(getcontext, userList)
                    message_recyclerView.adapter = recentAdapter
                }
            }

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }
}
