package com.zone.chatterz.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.Model.Group
import com.zone.chatterz.R

class GroupAdapter(context: Context, list: List<Group>) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    private val mContext = context
    private val mGroup = list

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var groupCard: CardView = itemView.findViewById(R.id.GroupCard)
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
        } else {
            Glide.with(mContext).load(group.groupImgUrl).into(holder.groupImage)
        }
        holder.groupCard.visibility = View.GONE
        /*holder.itemView.setOnClickListener{
            LoadGroupData()
        }*/
    }

    private fun LoadGroupData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}