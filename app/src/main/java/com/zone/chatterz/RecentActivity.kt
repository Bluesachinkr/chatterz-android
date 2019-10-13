package com.zone.chatterz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.RecentAdapter
import com.zone.chatterz.Model.Chat
import com.zone.chatterz.Model.User


open class RecentActivity : Fragment() {

    private lateinit var status_recyclerView: RecyclerView
    private lateinit var message_recyclerView: RecyclerView

    private lateinit var databaseReference : DatabaseReference
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var mUsers: MutableList<User>
    private lateinit var recentAdapter: RecentAdapter

    private lateinit var usersList: MutableList<String>
    private lateinit var lastMessages : MutableList<String>

    private lateinit var lastMessagelistener : ValueEventListener

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

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
         usersList = mutableListOf()

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        databaseReference.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
               usersList.clear()
                for (dataSet in p0.children) {
                    val chat = dataSet.getValue(Chat::class.java)
                    if (chat != null) {
                        if (chat.sender.equals(firebaseUser.uid)){
                            usersList.add(chat.receiver)
                        }
                        if(chat.receiver.equals(firebaseUser.uid)){
                            usersList.add(chat.sender)
                        }
                    }
                }
                readUser()
            }
        })


        return view
    }

    private fun readUser() {
       mUsers = mutableListOf()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                mUsers.clear()
                for (dataSet in p0.children){
                    val user = dataSet.getValue(User::class.java)
                    if(user != null){
                        for ( userId in usersList){
                            if(userId.equals(user.id)){
                                    if(!mUsers.contains(user)){
                                        mUsers.add(user)
                                    }
                            }
                        }
                    }
                }
                val getContext = context
                if(getContext !=null){
                    recentAdapter = RecentAdapter(getContext,mUsers)
                    message_recyclerView.adapter = recentAdapter
                }
            }
        })
    }
}
