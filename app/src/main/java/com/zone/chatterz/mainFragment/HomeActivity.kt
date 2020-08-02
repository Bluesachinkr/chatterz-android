package com.zone.chatterz.mainFragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.R
import com.zone.chatterz.adapter.CommentAdapter
import com.zone.chatterz.adapter.HomeAdapter
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.inferfaces.CommentControlListener
import com.zone.chatterz.inferfaces.CommentReloadListener
import com.zone.chatterz.model.Comment
import com.zone.chatterz.model.Post
import com.zone.chatterz.model.User
import com.zone.chatterz.requirements.Timings
import com.zone.chatterz.singleChat.ChatActivity
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class HomeActivity(context: Context, listener: NavigationControls) : Fragment(),
    View.OnClickListener, CommentControlListener, CommentReloadListener {

    private val mContext = context

    private lateinit var recyclerView_home: RecyclerView
    private lateinit var profile_image_home_frag: ImageView
    private lateinit var chat_btn_home_frag: ImageView
    private lateinit var down_comment_view: ImageView
    private lateinit var comments_counts_bottom_sheet: TextView
    private lateinit var commentSheetRecyclerView: RecyclerView
    private lateinit var bottomSheetBeahavior: BottomSheetBehavior<View>

    private lateinit var reloadProgressBar_home: ProgressBar

    companion object{
        val commentsList = mutableListOf<Comment>()
    }

    private lateinit var postList: MutableList<Post>
    private val listener = listener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        //Initialization of elements of Home fragment
        profile_image_home_frag = view.findViewById(R.id.profile_image_home_frag)
        chat_btn_home_frag = view.findViewById(R.id.chat_btn_home_frag)
        recyclerView_home = view.findViewById(R.id.recyclerView_home)
        reloadProgressBar_home = view.findViewById(R.id.reloadProgressBar_home)

        commentSheetRecyclerView = view.findViewById(R.id.comments_bottom_recycler)
        down_comment_view = view.findViewById(R.id.down_comment_view)
        comments_counts_bottom_sheet = view.findViewById(R.id.comments_counts_bottom_sheet)

        //Setting on click listener of buttons in home fragment
        chat_btn_home_frag.setOnClickListener(this)
        profile_image_home_frag.setOnClickListener(this)
        down_comment_view.setOnClickListener(this)



        reloadPosts()

        if (Build.VERSION.SDK_INT >= 11) {
            recyclerView_home.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (bottom < oldBottom) {
                    recyclerView_home.postDelayed({
                        recyclerView_home.adapter?.let {
                            recyclerView_home.smoothScrollToPosition(it.itemCount - 1)
                        }
                    }, 100)
                }
            })
        }

        val bottomsheet: View = view.findViewById(R.id.bottom_comment_sheet)
        bottomSheetBeahavior = BottomSheetBehavior.from(bottomsheet)
        val displayMetrics = activity!!.resources.displayMetrics

        val height = displayMetrics.heightPixels

        val maxHeight = (height * 0.6).toInt()
        bottomSheetBeahavior.peekHeight = maxHeight
        bottomSheetBeahavior.isHideable = true
        bottomSheetBeahavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBeahavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_HIDDEN) {
                    listener.openNavigation()
                }
            }
        })
        return view
    }

    override fun onStart() {
        super.onStart()
        setProfilePic()
    }

    private fun reloadPosts() {
        reloadProgressBar_home.visibility = View.VISIBLE
        val friendsList: MutableList<String> = mutableListOf()
        FirebaseMethods.addValueEventChild(Connection.friendRef, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val key = data.key.toString()
                    friendsList.add(key)
                }
                getPostList(friendsList)
            }
        })
    }

    /*
    * Initialize user information
    */

    private fun getPostList(friendsList: MutableList<String>) {
        this.postList = mutableListOf()
        friendsList.add(Connection.user)
        for (friend in friendsList) {
            FirebaseMethods.singleValueEventChild(Connection.postRef, object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        val post = data.getValue(Post::class.java)
                        post?.let {
                            postList.add(post)
                        }
                    }
                    sort(postList)
                    val linearLayout = LinearLayoutManager(mContext)
                    recyclerView_home.layoutManager = linearLayout
                    val adapter = HomeAdapter(mContext, postList, this@HomeActivity)
                    recyclerView_home.adapter = adapter
                    recyclerView_home.visibility = View.VISIBLE
                    reloadProgressBar_home.visibility = View.GONE
                }
            })
        }
    }

    private fun sort(postList: MutableList<Post>) {
        Collections.sort(postList, object : Comparator<Post> {
            override fun compare(o1: Post?, o2: Post?): Int {
                var date1 = o1?.postTime.toString()
                var date2 = o2?.postTime.toString()
                if (Timings.getYearOfTime(date1) == Timings.getYearOfTime(date2)) {
                    return -1
                } else if (Timings.getYearOfTime(date1) < Timings.getYearOfTime(date2)) {
                    return 1
                } else {
                    if (Timings.getMonthOfTime(date1) > Timings.getMonthOfTime(date2)) {
                        return -1
                    } else if (Timings.getMonthOfTime(date1) > Timings.getMonthOfTime(date2)) {
                        return 1
                    } else {
                        if (Timings.getDateOfTime(date1) > Timings.getDateOfTime(date2)) {
                            return -1
                        } else if (Timings.getDateOfTime(date1) > Timings.getDateOfTime(date2)) {
                            return 1
                        } else {
                            if (Timings.getHourofTime(date1) > Timings.getHourofTime(date2)) {
                                return -1
                            } else if (Timings.getHourofTime(date1) > Timings.getHourofTime(date2)) {
                                return 1
                            } else {
                                if (Timings.getMinutesofTime(date1) > Timings.getMinutesofTime(date2)) {
                                    return -1
                                } else if (Timings.getMinutesofTime(date1) > Timings.getMinutesofTime(
                                        date2
                                    )
                                ) {
                                    return 1
                                } else {
                                    return 1
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun setProfilePic() {
        FirebaseMethods.singleValueEventChild(Connection.userRef, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    if (!it.imageUrl.equals("null")) {
                        Glide.with(mContext).load(it.imageUrl).into(profile_image_home_frag)
                    } else {
                        profile_image_home_frag.setImageResource(R.drawable.google_logo)
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            chat_btn_home_frag -> {
                //chat button
                startActivity(Intent(mContext, ChatActivity::class.java))
            }
            profile_image_home_frag -> {
                //profile button
                startActivity(Intent(mContext, ProfileActivity::class.java))
            }
            down_comment_view -> {
                closeCommentLayout()
            }
            else -> {
                return
            }
        }
    }

    override fun openCommentLayout(id: String) {
        bottomSheetBeahavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        listener.openCommentEditext(id)
        val layoutManager = LinearLayoutManager(mContext)
        commentSheetRecyclerView.layoutManager = layoutManager
        openComments(id)
    }

    private fun openComments(id: String) {
        val commentsList = mutableListOf<Comment>()
        val hashMap = hashMapOf<String,Boolean>()
        val reference = Connection.commentsRef + File.separator + id
        FirebaseMethods.addValueEvent(reference, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val count = dataSnapshot.childrenCount
                val builder = StringBuffer("(")
                builder.append(count)
                builder.append(")")
                comments_counts_bottom_sheet.text = builder.toString()
                for (data in dataSnapshot.children) {
                    val comments = data.getValue(Comment::class.java)
                    comments?.let {
                        commentsList.add(comments)
                        hashMap.put(comments.sender,false)
                    }
                }
                val adapter = CommentAdapter(mContext, commentsList, this@HomeActivity,hashMap)
                commentSheetRecyclerView.adapter = adapter

            }
        })
    }

    override fun closeCommentLayout() {
        bottomSheetBeahavior.state = BottomSheetBehavior.STATE_HIDDEN
        listener.openNavigation()
    }


    interface NavigationControls {
        fun removeNavigation()
        fun openNavigation()
        fun openCommentEditext(id: String)
        fun onCommentReplyEdit(message: String)
        fun onCommentInfo(parent: String, to: String)
    }

    override fun onReload(id: String, postId: String,hashMap : HashMap<String,Boolean>,parent : String) {
      //  val commentsList = mutableListOf<Comment>()
        val reference = Connection.commentsRef + File.separator + postId
        FirebaseMethods.addValueEvent(reference, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val count = dataSnapshot.childrenCount
                val builder = StringBuffer("(")
                builder.append(count)
                builder.append(")")
                comments_counts_bottom_sheet.text = builder.toString()
                for (data in dataSnapshot.children) {
                    val comments = data.getValue(Comment::class.java)
                    comments?.let {
                        commentsList.add(comments)
                        if(hashMap[comments.sender]!!){
                            val ref = Connection.commentReplyRef + File.separator + postId + File.separator + parent
                            FirebaseMethods.addValueEvent(ref,object : RequestCallback(){
                                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                                    for (data in dataSnapshot.children){
                                        val comment = data.getValue(Comment::class.java)
                                        comment?.let {
                                            commentsList.add(comment)
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
                val adapter = CommentAdapter(mContext, commentsList, this@HomeActivity,hashMap)
                commentSheetRecyclerView.adapter = adapter
            }
        })
    }
}