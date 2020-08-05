package com.zone.chatterz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.R
import com.zone.chatterz.model.User

class OnlineFriendAdapter(mContext : Context,onlineList : MutableList<User>) : RecyclerView.Adapter<OnlineFriendAdapter.Viewholder>()  {

    private val mContext = mContext
    private val onlineList = onlineList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.followers_item,parent,false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return this.onlineList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val user = onlineList.get(position)

        if (user.imageUrl.equals("null")) {
            holder.friendProfieImg.setImageResource(R.drawable.google_logo)
        } else {
            Glide.with(mContext).load(user.imageUrl).into(holder.friendProfieImg)
        }
       /* if(user.status.equals("online")){
            holder.friendOnlineStatus.visibility = View.VISIBLE
        }else{
            holder.friendOnlineStatus.visibility = View.GONE
        }*/
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val friendProfieImg = itemView.findViewById<CircularImageView>(R.id.friendsProfileImg)
        //val friendOnlineStatus = itemView.findViewById<CircularImageView>(R.id.friendOnlineStatus)
    }
}