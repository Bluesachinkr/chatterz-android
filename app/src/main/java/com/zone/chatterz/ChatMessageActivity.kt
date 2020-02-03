package com.zone.chatterz

import android.content.Intent
import android.net.DnsResolver
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.ChatsAdapter
import com.zone.chatterz.FirebaseConnection.Connection
import com.zone.chatterz.Interfaces.APIService
import com.zone.chatterz.Model.Chat
import com.zone.chatterz.Model.User
import com.zone.chatterz.Notification.*
import kotlinx.android.synthetic.main.activity_chatmessage.*
import retrofit2.Call
import javax.security.auth.callback.Callback

class ChatMessageActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mChat: MutableList<Chat>
    private var isActive: String = "active"
    private lateinit var userId: String
    private lateinit var apiService : APIService
    private var notify = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatmessage)

        //getIntent to getUserid of userchats
        val intent = intent
        val id: String = intent.getStringExtra("UserId")

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id)

        apiService  = (Client.getClient("https://fcm.googleapis.com/")?.create(APIService::class.java)) as APIService

        //RecyclerView for Chats setting linearlayoutManager
        chatsRecyclerview.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        chatsRecyclerview.layoutManager = linearLayoutManager

        // databaseReference set the adapter of recycler view of chats
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                setProfileNameAndImgAppBar(p0)
                readMessage()
            }
        })

        //OnClick of send Message button calls the method sendMessage()
        //sendMessage() sends the load the message on data with sendser and receiver info
        sendMessageButton.setOnClickListener {
            notify = true
            val message = editextMessage.text.toString()
            if (!message.equals("")) {
                val sender = firebaseUser.uid
                val reciever = id
                sendMessage(message, sender, reciever)
            } else {
                Toast.makeText(this, "Can't send empty Message", Toast.LENGTH_SHORT).show()
            }
        }

        //set backArrow of ChatMessage Screen
        backArrow.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        seenMessage(id)
    }

    private fun sendMessage(message: String, sender: String, reciever: String) {
        databaseReference = FirebaseDatabase.getInstance().reference
        val hashMap = hashMapOf<String, Any>()
        hashMap.put("message", message)
        hashMap.put("sender",sender)
        hashMap.put("receiver",reciever)
        hashMap.put("isSeen", false)

        //Clear Edittext and make ready to take next chat
        editextMessage.setText("")
        databaseReference.child("Chats").child(sender).push().setValue(hashMap)
        databaseReference.child("Chats").child(reciever).push().setValue(hashMap)

        val mes = message

        val reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if(user!=null){
                    sendNotification(mes,reciever,user.username)
                    notify = false
                }
            }

        })
    }

    private fun sendNotification(mes: String, reciever: String, username: String) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        val query = databaseReference.orderByKey().equalTo(reciever)
        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children){
                    val token = data.getValue(Token::class.java)
                    if(token!=null) {
                        val data = Data(
                            firebaseUser.uid,
                            R.mipmap.ic_launcher_round.toString(),
                            username,
                            "New Message",
                            userId
                        )
                        val sender = Sender(data, token.token)

                        apiService.sendNotification(sender)
                            .enqueue(object : retrofit2.Callback<Response>{
                                override fun onFailure(call: Call<Response>, t: Throwable) {
                                }

                                override fun onResponse(
                                    call: Call<Response>,
                                    response: retrofit2.Response<Response>
                                ) {
                                   if(response.code() ==200 ){
                                       if(response.body()?.sucess !=1){
                                           Toast.makeText(this@ChatMessageActivity,"Failed",Toast.LENGTH_SHORT).show()
                                       }
                                   }
                                }

                            })
                    }
                }
            }
        })
    }

    private fun readMessage() {

        mChat = mutableListOf()

        databaseReference = FirebaseDatabase.getInstance().getReference(Connection.userChats).child(firebaseUser.uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                mChat.clear()
                for (dataset in p0.children) {
                    val chat = dataset.getValue(Chat::class.java)
                    if (chat != null) {
                            mChat.add(chat)
                    }
                }
                val chatsAdapter = ChatsAdapter(applicationContext, mChat)
                chatsRecyclerview.adapter = chatsAdapter
            }
        })
    }

    private fun setProfileNameAndImgAppBar(dataSnapshot: DataSnapshot) {
        //Set the Username and Profile image of the user on the appbar of the chatMessage Activity
        //It also shows the status of the user whom the cureent user is want to send message is Online/Offline as status
        val user = dataSnapshot.getValue(User::class.java)
        if (user != null) {
            if (user.imageUrl.equals("null")) {
                profileImg_chatBar.setImageResource(R.drawable.google_logo)
            } else {
                Glide.with(applicationContext).load(user.imageUrl).into(profileImg_chatBar)
            }
            userNameChatBox.text = user.username
            lastOnline.text = user.status
        }
    }

    private fun seenMessage(userId: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        if (isActive.equals("active")) {
            val hashMap = HashMap<String, Any>()
            hashMap.put("isSeen", true)
            databaseReference.child(userId).updateChildren(hashMap)
            databaseReference.child(firebaseUser.uid).updateChildren(hashMap)
        }
    }

    override fun onStart() {
        super.onStart()
        isActive = "active"
    }

    override fun onStop() {
        super.onStop()
        isActive = "inactive"
    }

}