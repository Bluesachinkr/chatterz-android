package com.zone.chatterz.mainFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zone.chatterz.singleChat.ChatActivity
import com.zone.chatterz.R
import com.zone.chatterz.adapter.HomeAdapter
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Post
import com.zone.chatterz.model.User
import com.zone.chatterz.requirements.Timings
import java.util.*
import kotlin.Comparator

class HomeActivity(context: Context) : Fragment(), View.OnClickListener {

    private val mContext = context

    private lateinit var recyclerView_home: RecyclerView
    private lateinit var profile_image_home_frag: ImageView
    private lateinit var chat_btn_home_frag: ImageView

    private lateinit var reloadProgressBar_home : ProgressBar

    private lateinit var friendsList: MutableList<String>
    private lateinit var postList: MutableList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        //Initialization of elements of Home fragment
        profile_image_home_frag = view.findViewById(R.id.profile_image_home_frag)
        chat_btn_home_frag = view.findViewById(R.id.chat_btn_home_frag)
        recyclerView_home = view.findViewById(R.id.recyclerView_home)
        reloadProgressBar_home = view.findViewById(R.id.reloadProgressBar_home)

        //Setting on click listener of buttons in home fragment
        chat_btn_home_frag.setOnClickListener(this)
        profile_image_home_frag.setOnClickListener(this)

        getFriendsList()
        reloadPosts()
        setProfilePic()
        return view
    }

    private fun getFriendsList() {
        this.friendsList = mutableListOf()
        FirebaseMethods.addValueEventChild(Connection.friendRef, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val key = data.key.toString()
                    friendsList.add(key)
                }
            }
        })
    }

    private fun reloadPosts() {
        reloadProgressBar_home.visibility = View.VISIBLE
        getPostList()
    }

    /*
    * Initialize user information
    */

    private fun getPostList() {
        this.postList = mutableListOf()
        friendsList.add(Connection.user)
        for (friend in friendsList) {
            val databaseReference =
                FirebaseDatabase.getInstance().getReference(Connection.postRef).child(friend)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (data in p0.children) {
                        val post = data.getValue(Post::class.java)
                        post?.let {
                            postList.add(post)
                        }
                    }
                    sort(postList)
                    val linearLayout = LinearLayoutManager(mContext)
                    recyclerView_home.layoutManager = linearLayout
                    val adapter = HomeAdapter(mContext, postList)
                    recyclerView_home.adapter = adapter
                    recyclerView_home.visibility = View.VISIBLE
                    reloadProgressBar_home.visibility = View.GONE
                }

            })
            /* FirebaseMethods.addValueEventChild(Connection.postRef, object : RequestCallback() {
                 override fun onDataChanged(dataSnapshot: DataSnapshot) {
                     for (data in dataSnapshot.children) {
                         val post = data.getValue(Post::class.java)
                         post?.let {
                             postList.add(post)
                         }
                     }
                 }
             })*/
        }
    }

    private fun sort(postList: MutableList<Post>) {
        Collections.sort(postList, object : Comparator<Post> {
            override fun compare(o1: Post?, o2: Post?): Int {
                var date1 = o1?.postTime.toString()
                var date2 = o2?.postTime.toString()
                if (Timings.getYearOfTime(date1) == Timings.getYearOfTime(date2)) {
                    return -1
                } else if (Timings.getYearOfTime(date1) < Timings.getYearOfTime(date2)) {
                    return 1
                } else {
                    if (Timings.getMonthOfTime(date1) > Timings.getMonthOfTime(date2)) {
                        return -1
                    } else if (Timings.getMonthOfTime(date1) > Timings.getMonthOfTime(date2)) {
                        return 1
                    } else {
                        if (Timings.getDateOfTime(date1) > Timings.getDateOfTime(date2)) {
                            return -1
                        } else if (Timings.getDateOfTime(date1) > Timings.getDateOfTime(date2)) {
                            return 1
                        } else {
                            if (Timings.getHourofTime(date1) > Timings.getHourofTime(date2)) {
                                return -1
                            } else if (Timings.getHourofTime(date1) > Timings.getHourofTime(date2)) {
                                return 1
                            } else {
                                if (Timings.getMinutesofTime(date1) > Timings.getMinutesofTime(date2)) {
                                    return -1
                                } else if (Timings.getMinutesofTime(date1) > Timings.getMinutesofTime(
                                        date2
                                    )
                                ) {
                                    return 1
                                } else {
                                    return 1
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun setProfilePic() {
        FirebaseMethods.singleValueEventChild(Connection.userRef, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    if (!it.imageUrl.equals("null")) {
                        Glide.with(mContext).load(it.imageUrl).into(profile_image_home_frag)
                    } else {
                        profile_image_home_frag.setImageResource(R.drawable.google_logo)
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            chat_btn_home_frag -> {
                //chat button
                startActivity(Intent(mContext, ChatActivity::class.java))
            }
            profile_image_home_frag -> {
                //profile button
                startActivity(Intent(mContext, ProfileActivity::class.java))
            }
            else -> {
                return
            }
        }
    }
}