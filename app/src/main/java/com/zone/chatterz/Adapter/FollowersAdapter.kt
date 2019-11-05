package com.zone.chatterz.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.Model.User
import com.zone.chatterz.R

class FollowersAdapter(
    context: Context,
    friendList : List<User>
) : RecyclerView.Adapter<FollowersAdapter.ViewHolder>() {

    private val mContext = context
    private val frndList  = friendList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.follow_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return frndList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = frndList.get(position)
        holder.frndProfileName.text = user.username
        if(user.imageUrl.equals("null")){
            holder.frndProfieImg.setImageResource(R.drawable.google_logo)
        }else{
            Glide.with(mContext).load(user.imageUrl).into(holder.frndProfieImg)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var frndProfieImg = itemView.findViewById<CircularImageView>(R.id.friendsProfileImg)
        var frndProfileName = itemView.findViewById<TextView>(R.id.friendProfileName)

    }


}