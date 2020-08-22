package com.zone.chatterz.chats

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.R
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.inferfaces.ChatControlListener
import com.zone.chatterz.model.User
import java.io.File

class NewMessageFragment(mContext: Context) : Fragment() {

    private val mContext = mContext
    private lateinit var back_from_new_message: ImageView
    private lateinit var user_display_name_new_message: TextView
    private lateinit var user_new_message_item: RecyclerView
    private lateinit var friendList : MutableList<String>
    private lateinit var adapter : ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_message, container, false)

        user_new_message_item = view.findViewById(R.id.user_new_message_item)
        back_from_new_message = view.findViewById(R.id.back_from_new_message)
        user_display_name_new_message = view.findViewById(R.id.user_display_name_new_message)

        back_from_new_message.setOnClickListener {
            (activity as ChatControlListener).onBack()
        }

        FirebaseMethods.singleValueEvent(Connection.userRef+File.separator+Connection.user,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    user_display_name_new_message.text = user.displayName
                }
            }
        })

        val layoutManager = LinearLayoutManager(mContext)
        user_new_message_item.layoutManager = layoutManager
        friendList = mutableListOf()
        adapter  = ItemAdapter(mContext,friendList);
        user_new_message_item.adapter = adapter
        LoadFollowing()
        return view
    }

    private fun LoadFollowing() {
        FirebaseMethods.singleValueEvent(Connection.followingRef+File.separator+Connection.user,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                friendList.clear()
                for (data in dataSnapshot.children){
                    data?.let {
                        friendList.add(it.key.toString())
                    }
                }
               adapter.notifyDataSetChanged()
            }
        })
    }

    private class ItemAdapter(mContext: Context, list: MutableList<String>) :
        RecyclerView.Adapter<ItemAdapter.Viewholder>() {

        private val mContext = mContext
        private val followingList = list

        class Viewholder(item: View) : RecyclerView.ViewHolder(item) {
            val user_profileImage: CircularImageView = item.findViewById(R.id.user_profileImage)
            val onlinestatus: CircularImageView = item.findViewById(R.id.onlinestatus)
            val userName: TextView = item.findViewById(R.id.userName)
            val bio: TextView = item.findViewById(R.id.messageUser)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
            return Viewholder(view)
        }

        override fun onBindViewHolder(holder: Viewholder, position: Int) {
            val userId: String = followingList[position]
            FirebaseMethods.singleValueEvent(Connection.userRef + File.separator + userId,
                object : RequestCallback() {
                    override fun onDataChanged(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let {
                            //user profile image
                            if (user.imageUrl.equals("null")) {
                                if (user.gender.equals("Male")) {
                                    holder.user_profileImage.setImageResource(R.drawable.ic_male_gender_profile)
                                } else {
                                    holder.user_profileImage.setImageResource(R.drawable.ic_female_gender_profile)
                                }
                            } else {
                                Glide.with(mContext).load(user.imageUrl)
                                    .into(holder.user_profileImage)
                            }

                            if(user.status.equals("online")){
                                holder.onlinestatus.visibility = View.VISIBLE
                            }else{
                                holder.onlinestatus.visibility = View.GONE
                            }

                            //username and displayName
                            holder.userName.text = user.displayName
                            holder.bio.text = user.username
                        }
                    }
                })

            holder.itemView.setOnClickListener {
                val intent = Intent(mContext, ChatMessageActivity::class.java)
                intent.putExtra("UserId", userId)
                mContext.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return followingList.size
        }
    }
}