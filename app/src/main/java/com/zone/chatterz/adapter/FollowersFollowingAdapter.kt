package com.zone.chatterz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.R
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.User
import java.io.File

class FollowersFollowingAdapter(
    mContext: Context,
    mUserList: MutableList<String>,
    followingHashMap: HashMap<String, Boolean>?
) : RecyclerView.Adapter<FollowersFollowingAdapter.Viewholder>() {

    private val mContext = mContext
    private val mUserList = mUserList
    private val followingHashMap = followingHashMap

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val followers_following_profile_img =
            itemView.findViewById<CircularImageView>(R.id.followers_following_profile_img)
        val followers_following_online_status =
            itemView.findViewById<CircularImageView>(R.id.followers_following_online_status)
        val followers_following_unique_display_name =
            itemView.findViewById<TextView>(R.id.followers_following_unique_display_name)
        val followers_following_profile_name =
            itemView.findViewById<TextView>(R.id.followers_following_profile_name)
        val follow_button_follower_following_screen =
            itemView.findViewById<RelativeLayout>(R.id.follow_button_follower_following_screen)
        val following_button_follower_following_screen =
            itemView.findViewById<RelativeLayout>(R.id.following_button_follower_following_screen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.followers_item, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return mUserList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val user = mUserList[position]

        FirebaseMethods.singleValueEvent(Connection.userRef + File.separator + user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {
                        if (user.imageUrl.equals("null")) {
                            holder.followers_following_profile_img.setImageResource(R.drawable.google_logo)
                        } else {
                            Glide.with(mContext).load(user.imageUrl)
                                .into(holder.followers_following_profile_img)
                        }

                        if (user.status.equals("online")) {
                            holder.followers_following_online_status.visibility = View.VISIBLE
                        } else {
                            holder.followers_following_online_status.visibility = View.GONE
                        }

                        holder.followers_following_profile_name.text = user.username
                    }
                }
            })

        if (followingHashMap != null && followingHashMap.containsKey(user)) {
            holder.following_button_follower_following_screen.visibility = View.VISIBLE
            holder.following_button_follower_following_screen.visibility = View.GONE
        } else {
            holder.following_button_follower_following_screen.visibility = View.GONE
            holder.following_button_follower_following_screen.visibility = View.VISIBLE
        }
    }
}