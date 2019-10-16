package com.zone.chatterz.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.GroupChatActivity
import com.zone.chatterz.Model.Group
import com.zone.chatterz.R

class GroupAdapter(context: Context,list: List<Group>) : RecyclerView.Adapter<GroupAdapter.ViewHolder>(){

    private val mContext = context
    private val mGroup = list
    private var numberViews : Int = mGroup.size
    private val lastView = 1
    private val remainingView = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.group_item, parent, false)
            return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return (mGroup.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var group = mGroup.get(position)
        holder.groupName.text = group.groupName
        if(group.groupImgUrl.equals("default")){
            holder.groupImage.setImageResource(R.mipmap.ic_launcher_round)
        } else{
            Glide.with(mContext).load(group.groupImgUrl).into(holder.groupImage)
        }

        numberViews--
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var groupName = itemView.findViewById<TextView>(R.id.GroupName)
        var groupLastMessage = itemView.findViewById<TextView>(R.id.messageGroup)
        var groupImage = itemView.findViewById<CircularImageView>(R.id.Group_profileImage)
        var lastMessageStatus = itemView.findViewById<TextView>(R.id.statusLastMessage)
    }

}