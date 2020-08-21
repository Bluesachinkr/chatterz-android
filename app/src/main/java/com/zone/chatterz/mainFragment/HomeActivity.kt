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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.R
import com.zone.chatterz.adapter.HomeAdapter
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Comment
import com.zone.chatterz.model.Post
import com.zone.chatterz.model.User
import com.zone.chatterz.requirements.Timings
import com.zone.chatterz.singleChat.ChatActivity
import java.util.*


class HomeActivity(context: Context, listener: NavigationControls) : Fragment(){

    private val mContext = context

    private lateinit var recyclerView_home: RecyclerView
    private lateinit var bottomSheetBeahavior: BottomSheetBehavior<View>

    private lateinit var reloadProgressBar_home: ProgressBar

    companion object {
        val commentsList = mutableListOf<Comment>()
        var commentItem = 0
        val postList: MutableList<Post> = mutableListOf()
    }

    private val listener = listener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        //Initialization of elements of Home fragment
        recyclerView_home = view.findViewById(R.id.recyclerView_home)
        reloadProgressBar_home = view.findViewById(R.id.reloadProgressBar_home)

        reloadPosts()

        if (Build.VERSION.SDK_INT >= 11) {
            recyclerView_home.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (bottom < oldBottom) {
                    recyclerView_home.postDelayed({
                        recyclerView_home.adapter?.let {
                            recyclerView_home.smoothScrollToPosition(commentItem)
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
    }

    private fun reloadPosts() {
        reloadProgressBar_home.visibility = View.VISIBLE
        val friendsList: MutableList<String> = mutableListOf()
        FirebaseMethods.addValueEventChild(Connection.followingRef, object : RequestCallback() {
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
        friendsList.add(Connection.user)
        FirebaseMethods.addValueEvent(Connection.postRef, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                postList.clear()
                for (data in dataSnapshot.children) {
                    val post = data.getValue(Post::class.java)
                    post?.let {
                        if (friendsList.contains(post.postOwner)) {
                            postList.add(post)
                        }
                    }
                }
                sort(postList)
                val linearLayout = LinearLayoutManager(mContext)
                recyclerView_home.layoutManager = linearLayout
                val adapter = HomeAdapter(mContext, postList)
                recyclerView_home.adapter = adapter
                recyclerView_home.visibility = View.VISIBLE
                reloadProgressBar_home.visibility = View.GONE
            }
        })
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

    interface NavigationControls {
        fun removeNavigation()
        fun openNavigation()
        fun openCommentEditext(id: String)
    }
}