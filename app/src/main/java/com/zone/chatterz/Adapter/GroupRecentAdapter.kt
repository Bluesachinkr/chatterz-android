package com.zone.chatterz.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.GroupChatMessageActivity
import com.zone.chatterz.Model.Group
import com.zone.chatterz.Model.GroupChats
import com.zone.chatterz.R
import com.zone.chatterz.Requirements.Timings

class GroupRecentAdapter(mContext: Context, mGroupRecent: List<Group>) :
    RecyclerView.Adapter<GroupRecentAdapter.ViewHolder>() {

    private val mContext = mContext
    private val mGroupRecent = mGroupRecent

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var lastMessage: TextView = itemView.findViewById(R.id.messageGroupUser)
        var lastMessageTime: TextView = itemView.findViewById(R.id.statusLastMesssageGroupTime)
        var groupName: TextView = itemView.findViewById(R.id.groupNameRecent)
        var groupImg: CircularImageView = itemView.findViewById(R.id.groupImg_Recent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.group_recent_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mGroupRecent.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recent = mGroupRecent.get(position)
        holder.groupName.text = recent.groupName
        if (!recent.groupImgUrl.equals("null")) {
            Glide.with(mContext).load(recent.groupImgUrl).into(holder.groupImg)
        }
        lastMessage(recent.id, holder)
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext,GroupChatMessageActivity::class.java)
            val list = arrayListOf<String>()
            list.add(recent.id)
            list.add(recent.groupName)
            intent.putStringArrayListExtra("groupInfo",list)
            mContext.startActivity(intent)
        }
    }

    private fun lastMessage(id: String, holder: ViewHolder) {
        var lastMsg = ""
        var time = ""
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference(com.zone.chatterz.FirebaseConnection.Connection.groupChats).child(id)
        databaseReference.addValueEventListener(object
            : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val values = data.getValue(GroupChats::class.java)
                    if (values != null) {
                        lastMsg = values.message
                        time = values.dateTime
                    }
                }
                holder.lastMessage.text = lastMsg
                if(!time.isBlank()){
                    holder.lastMessageTime.text = Timings.showTime(time)
                }else{
                    holder.lastMessageTime.text = time
                }
            }
        })
    }

}