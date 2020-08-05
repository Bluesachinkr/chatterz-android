package com.zone.chatterz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.singleChat.ChatMessageActivity
import com.zone.chatterz.model.User
import com.zone.chatterz.R
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback

class SearchAdapter(context: Context, mlist: List<User>) :
    RecyclerView.Adapter<SearchAdapter.Viewholder>() {

    private val mlist = mlist
    private val mContext = context
    private val mFriends = mutableListOf<String>()
    private val mAuth = FirebaseAuth.getInstance()
    private val firebaseUser = mAuth.currentUser!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.search_user_item, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return mlist.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val user = mlist.get(position)

        FirebaseMethods.addValueEventChild(Connection.friendRef, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                mFriends.clear()
                for (data in dataSnapshot.children) {
                    data.key?.let { mFriends.add(it) }
                }
            }
        })

        holder.userName.text = user.username

        if (user.imageUrl.equals("null")) {
            holder.profileImg.setImageResource(R.drawable.google_logo)
        } else {
            Glide.with(mContext).load(user.imageUrl).into(holder.profileImg)
        }

        if (mFriends.contains(user.id)) {
            setVisibility(holder.friendButton, holder.unfriendButton)
        } else {
            setVisibility(holder.unfriendButton, holder.friendButton)
        }

        holder.friendButton.setOnClickListener {
            setFollow(user.id, holder)
            setVisibility(holder.friendButton, holder.unfriendButton)
        }

        holder.unfriendButton.setOnClickListener {
            removeFollow(user.id, holder)
            setVisibility(holder.unfriendButton, holder.friendButton)
        }
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val friendButton: LinearLayout = itemView.findViewById(R.id.friendButton)
        val userName: TextView = itemView.findViewById(R.id.search_userName)
        val profileImg: CircularImageView = itemView.findViewById(R.id.search_profileImg)
        val unfriendButton: LinearLayout = itemView.findViewById(R.id.unfriendButton)
        val search_mutualFriend_count: TextView =
            itemView.findViewById(R.id.search_mutualFriend_count)
    }

    private fun setFollow(userId: String, holder: Viewholder) {
        val databaseReference = FirebaseDatabase.getInstance().getReference(Connection.followingRef)
            .child(firebaseUser.uid)
        databaseReference.child(userId).setValue(true)
        val followerRef =
            FirebaseDatabase.getInstance().getReference(Connection.followersRef).child(userId)
        followerRef.child(Connection.user).setValue(true)
        setVisibility(holder.friendButton, holder.unfriendButton)
    }

    private fun removeFollow(userId: String, holder: Viewholder) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference(Connection.followingRef)
                .child(firebaseUser.uid)
        databaseReference.child(userId).removeValue()
        val followerRef =
            FirebaseDatabase.getInstance().getReference(Connection.followersRef).child(userId)
        followerRef.child(Connection.user).removeValue()
        setVisibility(holder.unfriendButton, holder.friendButton)
    }

    private fun setVisibility(l1: LinearLayout, l2: LinearLayout) {
        l1.visibility = View.GONE
        l2.visibility = View.VISIBLE
    }

}