package com.zone.chatterz.home

import android.app.Notification
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.R
import com.zone.chatterz.adapter.NotificationAdapter
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.inferfaces.ChatControlListener
import com.zone.chatterz.model.PopUp
import java.io.File

class NotificationFragment(mContext : Context) : Fragment() {

    private val mContext = mContext
    private lateinit var notification_recycler : RecyclerView
    private lateinit var back_arrow_notification : ImageView
    private lateinit var adapter : NotificationAdapter
    private val list = mutableListOf<PopUp>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        notification_recycler = view.findViewById(R.id.notification_recycler)
        back_arrow_notification = view.findViewById(R.id.back_arrow_notification)

        back_arrow_notification.setOnClickListener {
            (activity as ChatControlListener).onBack()
        }
        val layoutManager = LinearLayoutManager(mContext)
        notification_recycler.layoutManager = layoutManager
        adapter = NotificationAdapter(mContext,list)
        notification_recycler.adapter = adapter
        loadNotification()
        return view
    }

    private fun loadNotification() {
        FirebaseMethods.singleValueEvent(Connection.notification+ File.separator+Connection.user,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    list.clear()
                    for (data  in dataSnapshot.children){
                        val popup = data.getValue(PopUp::class.java)
                        popup?.let {
                            list.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
}