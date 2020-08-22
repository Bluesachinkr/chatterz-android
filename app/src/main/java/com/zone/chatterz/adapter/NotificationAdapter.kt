package com.zone.chatterz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.R
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.model.PopUp
import com.zone.chatterz.model.User
import java.io.File

class NotificationAdapter(mContext : Context,list : List<PopUp>) : RecyclerView.Adapter<NotificationAdapter.Viewholder>() {

    private val mContext = mContext
    private val list = list

    class Viewholder(item : View) : RecyclerView.ViewHolder(item){
        val image_notification = item.findViewById<ImageView>(R.id.image_notification)
        val message_notification : TextView = item.findViewById(R.id.message_notification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
       val popUp = list[position]
        if(popUp.image.equals("null")){
            FirebaseMethods.singleValueEvent(Connection.userRef + File.separator + popUp.sender,
                object : RequestCallback() {
                    override fun onDataChanged(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let {
                            Glide.with(mContext).load(user.imageUrl).into(holder.image_notification)
                        }
                    }
                })
        }else{
            Glide.with(mContext).load(popUp.image).into(holder.image_notification)
        }

        holder.message_notification.text = popUp.message
    }

    override fun getItemCount(): Int {
        return list.size
    }
}