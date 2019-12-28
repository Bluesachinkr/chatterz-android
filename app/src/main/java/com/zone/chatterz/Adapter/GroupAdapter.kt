package com.zone.chatterz.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.CreateNewGroup
import com.zone.chatterz.Interfaces.GroupchatControl
import com.zone.chatterz.Model.Group
import com.zone.chatterz.R

class GroupAdapter(context: Context, list: List<Group>,interfaceChats : GroupchatControl) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    private val mContext = context
    private val mGroup = list
    private val interfaceChats  = interfaceChats

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var groupImage: CircularImageView = itemView.findViewById(R.id.Group_profileImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.group_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return (mGroup.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var group = mGroup.get(position)
        if (group.groupImgUrl.equals("default")) {
            holder.groupImage.setImageResource(R.mipmap.ic_launcher_round)
        } else if(group.groupImgUrl.equals("add")){
            holder.groupImage.setImageResource(R.drawable.new_group_icon)
        }else {
            Glide.with(mContext).load(group.groupImgUrl).into(holder.groupImage)
        }
        holder.itemView.setOnClickListener{
            if(holder.groupImage.equals("add")){
                val intent= Intent(mContext,CreateNewGroup::class.java)
                mContext.startActivity(intent)
            }else{
                interfaceChats.loadGroupChats(group.id)
            }
        }
    }

    private fun LoadGroupData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}