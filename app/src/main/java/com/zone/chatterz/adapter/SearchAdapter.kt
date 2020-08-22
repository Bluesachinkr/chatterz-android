package com.zone.chatterz.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.model.User
import com.zone.chatterz.R
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.group.GroupChatMessageActivity
import com.zone.chatterz.model.Group

class SearchAdapter(context: Context, mlist: List<User>,mGroups : List<Group>,type : String) :
    RecyclerView.Adapter<SearchAdapter.Viewholder>() {

    private val mlist = mlist
    private val mContext = context
    private val mFriends = mutableListOf<String>()
    private val mGroups = mGroups
    private val type = type
    private val mAuth = FirebaseAuth.getInstance()
    private val firebaseUser = mAuth.currentUser!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.search_user_item, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        if(type.equals("Group")){
            return mGroups.size
        }else {
            return mlist.size
        }
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        if(type.equals("Group")){
            val group = mGroups[position]
            holder.userName.text = group.groupName

            if (group.groupImgUrl.equals("null")) {
                holder.profileImg.setImageResource(R.drawable.ic_multiple_users_silhouette)
            } else {
                Glide.with(mContext).load(group.groupImgUrl).into(holder.profileImg)
            }

            holder.itemView.setOnClickListener {
                val alert = AlertDialog.Builder(mContext)
                alert.setMessage("You want to join group")
                alert.setPositiveButton("YES",object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        FirebaseDatabase.getInstance().getReference(Connection.groupMemRef).child(group.id).child(Connection.user).setValue(true)
                        FirebaseDatabase.getInstance().getReference(Connection.groupJoinedRef).child(Connection.user).child(group.id).setValue(true)
                        val intent = Intent(mContext,GroupChatMessageActivity::class.java)
                        val list = arrayListOf<String>()
                        list.add(group.id)
                        list.add(group.groupName)
                        intent.putStringArrayListExtra("groupInfo", list)
                        mContext.startActivity(intent)
                    }
                })
                alert.setNegativeButton("NO",object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.let {
                            it.dismiss()
                        }
                    }
                })
                alert.create().show()
            }
        }else {
            val user = mlist.get(position)

            FirebaseMethods.addValueEventChild(Connection.friendRef, object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    mFriends.clear()
                    for (data in dataSnapshot.children) {
                        data.key?.let { mFriends.add(it) }
                    }
                }
            })

            holder.userName.text = user.username

            if (user.imageUrl.equals("null")) {
                holder.profileImg.setImageResource(R.drawable.google_logo)
            } else {
                Glide.with(mContext).load(user.imageUrl).into(holder.profileImg)
            }

            if (mFriends.contains(user.id)) {
                setVisibility(holder.friendButton, holder.unfriendButton)
            } else {
                setVisibility(holder.unfriendButton, holder.friendButton)
            }

            holder.friendButton.setOnClickListener {
                setFollow(user.id, holder)
                setVisibility(holder.friendButton, holder.unfriendButton)
            }

            holder.unfriendButton.setOnClickListener {
                removeFollow(user.id, holder)
                setVisibility(holder.unfriendButton, holder.friendButton)
            }
        }
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val friendButton: LinearLayout = itemView.findViewById(R.id.friendButton)
        val userName: TextView = itemView.findViewById(R.id.search_userName)
        val profileImg: CircularImageView = itemView.findViewById(R.id.search_profileImg)
        val unfriendButton: LinearLayout = itemView.findViewById(R.id.unfriendButton)
        val search_mutualFriend_count: TextView =
            itemView.findViewById(R.id.search_mutualFriend_count)
    }

    private fun setFollow(userId: String, holder: Viewholder) {
        val databaseReference = FirebaseDatabase.getInstance().getReference(Connection.followingRef)
            .child(firebaseUser.uid)
        databaseReference.child(userId).setValue(true)
        val followerRef =
            FirebaseDatabase.getInstance().getReference(Connection.followersRef).child(userId)
        followerRef.child(Connection.user).setValue(true)
        setVisibility(holder.friendButton, holder.unfriendButton)
    }

    private fun removeFollow(userId: String, holder: Viewholder) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference(Connection.followingRef)
                .child(firebaseUser.uid)
        databaseReference.child(userId).removeValue()
        val followerRef =
            FirebaseDatabase.getInstance().getReference(Connection.followersRef).child(userId)
        followerRef.child(Connection.user).removeValue()
        setVisibility(holder.unfriendButton, holder.friendButton)
    }

    private fun setVisibility(l1: LinearLayout, l2: LinearLayout) {
        l1.visibility = View.GONE
        l2.visibility = View.VISIBLE
    }

}