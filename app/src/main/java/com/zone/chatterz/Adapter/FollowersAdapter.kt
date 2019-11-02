package com.zone.chatterz.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.Model.User
import com.zone.chatterz.R

class FollowersAdapter(context : Context,followerList : List<String>,followingList: List<String>,type : String) : RecyclerView.Adapter<FollowersAdapter.ViewHolder>() {

    private val mContext = context
    private val followerList = followerList
    private val followingList = followingList
    private val type = type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(mContext).inflate(R.layout.followers_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (type.equals("Followers")){
            return followerList.size
        }else{
            return followingList.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(type.equals("Followers")){
            val userId = followerList.get(position)
            setFollowerItem(holder,userId)
            if(followingList.contains(userId)){
                holder.isFollow.visibility = View.GONE
            }else{
                holder.isFollow.visibility = View.VISIBLE
            }
        }else{
            val userId = followingList.get(position)
            setFollowerItem(holder,userId)
        }
    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

        val userProfileImg = itemView.findViewById<CircularImageView>(R.id.profileImg_Follower)
        val userName = itemView.findViewById<TextView>(R.id.userNameFollower)
        val isFollow = itemView.findViewById<RelativeLayout>(R.id.followBox)

    }

    private fun setFollowerItem(holder : ViewHolder,id : String){
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children){
                    val user = data.getValue(User::class.java)
                    if(user!=null){
                        if(user.id.equals(id)){
                            holder.userName.text = user.username
                            if(!user.imageUrl.equals("nulll")){
                                Glide.with(mContext).load(user.imageUrl).into(holder.userProfileImg)
                            }
                            break
                        }
                    }
                }
            }

        })

    }

}