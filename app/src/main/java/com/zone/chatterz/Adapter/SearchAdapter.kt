package com.zone.chatterz.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.Model.User
import com.zone.chatterz.R

class SearchAdapter(context: Context, list: List<User>) :
    RecyclerView.Adapter<SearchAdapter.Viewholder>() {

    private val mlist = list
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.search_user_item, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return mlist.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val user = mlist.get(position)
        holder.userName.text = user.username
        if (user.imageUrl.equals("null")) {
            holder.profileImg.setImageResource(R.drawable.google_logo)
        } else {
            Glide.with(mContext).load(user.imageUrl).into(holder.profileImg)
        }
        isFollowing(user.id, holder)
        holder.friendButton.setOnClickListener {
            if (holder.buttonText.equals("Friend")) {
                setFollow(user.id)
                holder.buttonText.text = "Unfriend"
            } else {
                removeFollow(user.id)
                holder.buttonText.text = "Friend"
            }
        }

    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val friendButton: LinearLayout = itemView.findViewById(R.id.friendButton)
        val buttonText: TextView = itemView.findViewById(R.id.friendButtonText)
        val userName: TextView = itemView.findViewById(R.id.search_userName)
        val profileImg: CircularImageView = itemView.findViewById(R.id.search_profileImg)

    }

    private fun setFollow(userId: String) {
        val firbaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Friends").child(firbaseUser.uid)
        databaseReference.child(userId).push().setValue(true)
    }

    private fun removeFollow(userId: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.uid)
        databaseReference.child(userId).removeValue()
    }

    private fun isFollowing(userId: String, holder: Viewholder) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val friendsId = p0.key
                if (friendsId.equals(userId)) {
                    holder.buttonText.text = "Unfriend"
                    return
                }
            }
        })
    }
}