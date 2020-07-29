package com.zone.chatterz.mainFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.adapter.FollowersAdapter
import com.zone.chatterz.FollowersActivity
import com.zone.chatterz.model.User
import com.zone.chatterz.R
import kotlin.collections.HashMap

open class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var profileImg: CircularImageView
    private lateinit var userName: TextView
    private lateinit var textStatus: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var editStatusButton: ImageView
    private lateinit var editStatusDone: ImageView
    private lateinit var statusEditBox: EditText
    private lateinit var RecyclerFollowers: RecyclerView
    private lateinit var viewallFriends: TextView
    private lateinit var mFrindList: MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()

        profileImg = findViewById(R.id.ProfileImage)
        userName = findViewById(R.id.userName_Profile)
        textStatus = findViewById(R.id.userProfileTextStatus)
        toolbar = findViewById(R.id.toolbarProfile)
        editStatusButton = findViewById(R.id.statusEdit)
        editStatusDone = findViewById(R.id.statusDone)
        statusEditBox = findViewById(R.id.userStatusEditBox)
        RecyclerFollowers = findViewById(R.id.Recycler_followers)
        viewallFriends = findViewById(R.id.friendViewall)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        RecyclerFollowers.layoutManager = layoutManager
        mFrindList = mutableListOf()
        setFriendView(this)

        loadProfileData()

        viewallFriends.setOnClickListener(this)

    }

    private fun setFriendView(c: Context) {
        val list = mutableListOf<String>()
        val firebaseUser = mAuth.currentUser!!
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val friendId = data.key!!
                    list.add(friendId)
                }
                val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (data in p0.children) {
                            val user = data.getValue(User::class.java)
                            if (user != null && list.contains(user.id)) {
                                mFrindList.add(user)
                            }

                        }
                        val adapter = FollowersAdapter(c, mFrindList, "profile")
                        RecyclerFollowers.adapter = adapter
                    }

                })
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater!!.inflate(R.menu.profile_menu_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return false
    }

    private fun loadProfileData() {
        firebaseUser = mAuth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                user?.let { setProfileLayout(user) }
            }
        })
    }

    private fun setProfileBio(content: String) {
        firebaseUser = mAuth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val user = data.getValue(User::class.java)
                    if (user != null) {
                        if (user.id.equals(firebaseUser.uid)) {
                            val hashMap = HashMap<String, Any>()
                            hashMap.put("bio", content)
                            data.ref.updateChildren(hashMap)
                        }
                    }
                }
            }

        })

    }

    private fun setProfileLayout(user: User) {
        userName.text = user.username
        if (!textStatus.text.equals("null")) {
            textStatus.text = user.bio
        }

        if (!user.imageUrl.equals("null")) {
            Glide.with(this).load(user.imageUrl).into(profileImg)
        } else {
            profileImg.setImageResource(R.drawable.google_logo)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewallFriends -> {
                val intent = Intent(this, FollowersActivity::class.java)
                startActivity(intent)
            }
        }
    }

}
