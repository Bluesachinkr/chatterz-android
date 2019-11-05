package com.zone.chatterz.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zone.chatterz.Model.User
import com.zone.chatterz.R

class firnedAdapter(list : List<User>, context : Context) : RecyclerView.Adapter<firnedAdapter.Viewholder>(){

    private val list = list
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.follow_item,parent,false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    private fun setFollow(userId : String){
        val firbaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference = FirebaseDatabase.getInstance().getReference("Friends").child(firbaseUser.uid)
        databaseReference.child(userId).push().setValue(true)
    }
}
