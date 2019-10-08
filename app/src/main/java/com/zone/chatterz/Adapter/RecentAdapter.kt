package com.zone.chatterz.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.ChatMessageActivity
import com.zone.chatterz.Model.User
import com.zone.chatterz.R

class RecentAdapter(context: Context,list: List<User>) : RecyclerView.Adapter<RecentAdapter.Viewholder>() {

    private val mUsers = list
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

        val user : User = mUsers.get(position)
        holder.userName.text = user.username
        if(user.imageUrl.equals("null")) {
            holder.profileImage.setImageResource(R.mipmap.ic_launcher_round)
        }else{
            Glide.with(mContext).load(user.imageUrl).into(holder.profileImage)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext,ChatMessageActivity::class.java)
            intent.putExtra("UserId",user.id)
            mContext.startActivity(intent)
        }

    }


    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userName = itemView.findViewById<TextView>(R.id.userName)
        var userlastMessage =itemView.findViewById<TextView>(R.id.messageUser)
        var statusLastOnline  = itemView.findViewById<TextView>(R.id.statusLastOnline)
        var profileImage = itemView.findViewById<ImageView>(R.id.user_profileImage)

    }
}