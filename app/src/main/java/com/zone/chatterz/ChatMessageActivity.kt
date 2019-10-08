package com.zone.chatterz

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.ChatsAdapter
import com.zone.chatterz.Model.Chat
import com.zone.chatterz.Model.User
import kotlinx.android.synthetic.main.activity_chatmessage.*

class ChatMessageActivity : AppCompatActivity() {

    lateinit var firebaseUser: FirebaseUser
    lateinit var databaseReference: DatabaseReference

    lateinit var mChat: MutableList<Chat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatmessage)

        //getIntent to getUserid of userchats
        val intent = intent
        val id: String = intent.getStringExtra("UserId")

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id)

        //RecyclerView for Chats setting linearlayoutManager

        chatsRecyclerview.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        chatsRecyclerview.layoutManager = linearLayoutManager

        // databaseReference set the adapter of recycler vuew of chats
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                setProfileNameAndImgAppBar(p0)
                readMessage(id, firebaseUser.uid)
            }

        })

        //OnClick of send Message button calls the method sendMessage()

        sendMessageButton.setOnClickListener {
            val message = editextMessage.text.toString()
            if (!message.equals("")) {
                val sender = firebaseUser.uid
                val reciever = id
                sendMessage(message, sender, reciever)
            } else {
                Toast.makeText(this, "Can't send empty Message", Toast.LENGTH_SHORT).show()
            }
        }

        backArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun sendMessage(message: String, sender: String, reciever: String) {
        databaseReference = FirebaseDatabase.getInstance().reference
        val hashMap = hashMapOf<String, Any>()
        hashMap.put("message", message)
        hashMap.put("sender", sender)
        hashMap.put("receiver", reciever)

        //Clear Edittext and make ready to take next chat
        editextMessage.setText("")
        databaseReference.child("Chats").push().setValue(hashMap)
    }

    private fun readMessage(id: String, userId: String) {
        mChat = mutableListOf()

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                mChat.clear()
                for (dataset in p0.children) {
                    val chat = dataset.getValue(Chat::class.java)
                    if (chat != null) {
                        if ((chat.sender.equals(id) && chat.receiver.equals(userId)) || (chat.receiver.equals(
                                id
                            ) && chat.sender.equals(userId))
                        ) {
                            mChat.add(chat)
                        }
                    }
                }
                val chatsAdapter = ChatsAdapter(applicationContext, mChat)
                chatsRecyclerview.adapter = chatsAdapter
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setProfileNameAndImgAppBar(dataSnapshot: DataSnapshot) {
        val user = dataSnapshot.getValue(User::class.java)
        if (user != null) {
            if (user.imageUrl.equals("null")) {
                profileImg_chatBar.setImageResource(R.mipmap.ic_launcher_round)
            } else {
                Glide.with(applicationContext).load(user.imageUrl).into(profileImg_chatBar)
            }
            userNameChatBox.text = user.username
        }
    }
}