package com.zone.chatterz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.singleChat.ChatMessageActivity
import com.zone.chatterz.model.User
import com.zone.chatterz.R

class FriendsAdapter(context: Context, list: List<User>) :
    RecyclerView.Adapter<FriendsAdapter.Viewholder>() {

    private val mContext = context
    private val mUser = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.group_members_item, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val user = mUser.get(position)
        if (user.imageUrl.equals("null")) {
            holder.profileImage.setImageResource(R.drawable.google_logo)
        } else {
            Glide.with(mContext).load(user.imageUrl).into(holder.profileImage)
        }
        holder.userName.text = user.username
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ChatMessageActivity::class.java)
            intent.putExtra("UserId", user.id)
            mContext.startActivity(intent)
        }
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileImage: CircularImageView = itemView.findViewById(R.id.profileImg_Friends)
        var userName: TextView = itemView.findViewById(R.id.userNameFriends)

    }
}