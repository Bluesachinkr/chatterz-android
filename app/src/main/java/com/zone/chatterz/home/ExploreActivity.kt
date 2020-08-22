package com.zone.chatterz.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.R
import com.zone.chatterz.adapter.ExploreAdapter
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.model.Post
import com.zone.chatterz.common.Timings
import java.util.*
import kotlin.Comparator

class ExploreActivity(mContext: Context) : Fragment() {

    private val mContext = mContext

    private lateinit var tab_layout_explore: TabLayout
    private lateinit var exploreRecyclerView: RecyclerView
    private val listTabs = arrayListOf<String>("Popular", "Latest", "Following")
    private val postList = arrayListOf<Post>()

    private lateinit var exploreAdapter: ExploreAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        tab_layout_explore = view.findViewById(R.id.tab_layout_explore)
        exploreRecyclerView = view.findViewById(R.id.exploreRecyclerView)
        tab_layout_explore.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    getPosts(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        exploreRecyclerView.layoutManager = layoutManager
        exploreAdapter = ExploreAdapter(mContext, postList)
        exploreRecyclerView.adapter = exploreAdapter

        getPosts(0)
        return view
    }

    private fun getPosts(tab: Int) {
        FirebaseMethods.addValueEvent(Connection.postRef, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                postList.clear()
                when (tab) {
                    0 -> {
                        for (data in dataSnapshot.children) {
                            val post = data.getValue(Post::class.java)
                            post?.let {
                                if (!it.postOwner.equals(Connection.user)) {
                                    postList.add(it)
                                }
                            }
                        }
                    }
                    1 -> {
                        for (data in dataSnapshot.children) {
                            val post = data.getValue(Post::class.java)
                            post?.let {
                                if (!it.postOwner.equals(Connection.user)) {
                                    postList.add(it)
                                }
                            }
                        }
                    }
                    2 -> {
                        for (data in dataSnapshot.children) {
                            val post = data.getValue(Post::class.java)
                            post?.let {
                                if (!it.postOwner.equals(Connection.user)) {
                                    postList.add(it)
                                }
                            }
                        }
                    }
                }
                sort(tab, postList)
            }
        })
    }

    private fun sort(according: Int, postList: MutableList<Post>) {
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
                            } else if (Timings.getHourofTime(date1) > Timings.getHourofTime(
                                    date2
                                )
                            ) {
                                return 1
                            } else {
                                if (Timings.getMinutesofTime(date1) > Timings.getMinutesofTime(
                                        date2
                                    )
                                ) {
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
        exploreAdapter.notifyDataSetChanged()
    }
}