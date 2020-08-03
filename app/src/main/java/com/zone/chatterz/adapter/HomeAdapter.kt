package com.zone.chatterz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.CommentActivity
import com.zone.chatterz.MainActivity
import com.zone.chatterz.R
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.inferfaces.CommentControlListener
import com.zone.chatterz.mainFragment.HomeActivity
import com.zone.chatterz.model.Comment
import com.zone.chatterz.model.Post
import com.zone.chatterz.model.User
import com.zone.chatterz.requirements.Timings
import java.io.File

class HomeAdapter(
    mContext: Context,
    postList: MutableList<Post>
) :
    RecyclerView.Adapter<HomeAdapter.Viewholder>() {

    private val mContext = mContext
    private val postList = postList

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage_post: CircularImageView = itemView.findViewById(R.id.profileImage_post)
        val profileName_post: TextView = itemView.findViewById(R.id.profileName_post)
        val options_post: ImageView = itemView.findViewById(R.id.options_post)
        val image_post: ImageView = itemView.findViewById(R.id.image_post)
        val like_post: ImageView = itemView.findViewById(R.id.like_post)
        val time_post: TextView = itemView.findViewById(R.id.time_post)
        val no_of_likes_post: TextView = itemView.findViewById(R.id.no_of_likes_post)
        val comment_post: ImageView = itemView.findViewById(R.id.comment_post)
        val no_of_comments_post: TextView = itemView.findViewById(R.id.no_of_comments_post)
        val description_post: TextView = itemView.findViewById(R.id.description_post)
        val profileImage_comment_box_post: CircularImageView =
            itemView.findViewById(R.id.profileImage_comment_box_post)
        val comment_box_post: LinearLayout = itemView.findViewById(R.id.comment_edittext_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return this.postList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val post = postList.get(position)

        loadOwnerDataPost(holder, post.postOwner)

        //setting image
        Glide.with(mContext).load(post.postImage).into(holder.image_post)

        //setting description on post
        holder.description_post.text = post.postDescription

        //settting time post uploaded
        holder.time_post.text = Timings.timeUploadPost(post.postTime)

        likesCount(post.postId, holder)

        commentsCount(post.postId, holder)

        holder.like_post.setOnClickListener {
            likeOnPost(post.postId, holder)
        }

        holder.comment_post.setOnClickListener {
            val intent = Intent(mContext, CommentActivity::class.java)
            intent.putExtra("postId", post.postId)
            mContext.startActivity(intent)
        }

        holder.comment_box_post.setOnClickListener {
            (mContext as HomeActivity.NavigationControls).removeNavigation()
            (mContext as HomeActivity.NavigationControls).openCommentEditext(post.postId)
            it.visibility = View.GONE
            HomeActivity.commentItem = position
            MainActivity.is_changed = true
            MainActivity.comment_viewholder_changed = holder
        }

        FirebaseMethods.singleValueEvent(Connection.likesRef + File.separator + post.postId + File.separator + Connection.user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        holder.like_post.setImageResource(R.drawable.ic_filled_like_star_post)
                    }
                }
            })
    }

    private fun commentsCount(postId: String, holder: Viewholder) {
        var commentsCount: Long = 0
        FirebaseMethods.addValueEventDifferentChild(
            Connection.commentsRef,
            postId,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    commentsCount += dataSnapshot.childrenCount
                    for (data in dataSnapshot.children) {
                        val comment = data.getValue(Comment::class.java)
                        comment?.let {
                            commentsCount += comment.isReply
                        }
                    }
                    holder.no_of_comments_post.text = commentsCount.toString()
                }
            })
    }

    private fun likesCount(
        postId: String,
        holder: Viewholder
    ) {
        FirebaseMethods.singleValueEventDifferentChild(
            Connection.likesRef,
            postId,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val count = dataSnapshot.childrenCount
                    val zero: Long = 0
                    if (count == zero) {
                        holder.no_of_likes_post.text = "0"
                    } else {
                        holder.no_of_likes_post.text = count.toString()
                    }
                }
            })
    }

    private fun likeOnPost(
        postId: String,
        holder: Viewholder
    ) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference(Connection.likesRef).child(postId)
                .child(Connection.user)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val str = holder.no_of_likes_post.text
                var cnt: Int = Integer.parseInt(str.toString())
                if (p0.exists()) {
                    cnt--
                    databaseReference.removeValue()
                    holder.like_post.setImageResource(R.drawable.ic_outline_star)
                } else {
                    cnt++
                    databaseReference.setValue(true)
                    holder.like_post.setImageResource(R.drawable.ic_filled_like_star_post)
                }
                holder.no_of_likes_post.text = cnt.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun loadOwnerDataPost(holder: Viewholder, postOwner: String) {
        FirebaseMethods.singleValueEventDifferentChild(
            Connection.userRef,
            postOwner,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {
                        //setting profile image
                        Glide.with(mContext).load(it.imageUrl).into(holder.profileImage_post)

                        //setting profile image comment
                        Glide.with(mContext).load(it.imageUrl)
                            .into(holder.profileImage_comment_box_post)

                        //setting profile name
                        holder.profileName_post.text = it.username
                    }
                }
            })
    }
}
