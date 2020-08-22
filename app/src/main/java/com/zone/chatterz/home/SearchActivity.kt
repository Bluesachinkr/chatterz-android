package com.zone.chatterz.home

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.adapter.SearchAdapter
import com.zone.chatterz.model.User
import com.zone.chatterz.R
import com.zone.chatterz.connection.Connection.Companion.user
import com.zone.chatterz.model.Group
import java.sql.Connection


open class SearchActivity : AppCompatActivity(),TabLayout.OnTabSelectedListener {

    private lateinit var searchBar: android.widget.SearchView
    private lateinit var tablayout_searchView: TabLayout
    private lateinit var searchProgressBar: ProgressBar
    private lateinit var searchedView: RecyclerView
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private var selectedTab : Int = 0
    private lateinit var mSearchUser: MutableList<User>
    private var currentSearchtext: String = ""


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search)
        mAuth = FirebaseAuth.getInstance()
        firebaseUser = mAuth.currentUser!!

        searchBar = findViewById(R.id.searchBar)
        searchedView = findViewById(R.id.searchedView)
        tablayout_searchView = findViewById(R.id.tablayout_searchView)
        tablayout_searchView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        searchProgressBar = findViewById(R.id.searchProgressBar)

        mSearchUser = mutableListOf()
        currentSearchtext = ""

        val layoutManager = LinearLayoutManager(this)
        searchedView.layoutManager = layoutManager

        searchBar.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentSearchtext = query!!
                if (query!!.isEmpty()) {
                    emptySearchedView()
                    return false
                } else {
                    searchProgressBar.visibility = View.VISIBLE
                    if (tablayout_searchView.selectedTabPosition == 0) {
                        searchUser(query)
                    } else {
                        searchGroups(query)
                    }
                    return true
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchtext = newText!!
                if (newText!!.isEmpty()) {
                    emptySearchedView()
                    return false
                } else {
                    searchProgressBar.visibility = View.VISIBLE
                    if (tablayout_searchView.selectedTabPosition == 0) {
                        searchUser(newText)
                    } else {
                        searchGroups(newText)
                    }
                    return true
                }
            }
        })
    }

    private fun searchGroups(text: String) {
        val mGroups = mutableListOf<Group>()
        val query = FirebaseDatabase.getInstance().getReference("Groups").orderByChild("groupName")
            .startAt(text).endAt(text + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val groups = data.getValue(Group::class.java)
                    groups?.let {
                        if (!it.groupMaker.equals(com.zone.chatterz.connection.Connection.user)) {
                            mGroups.add(groups)
                        }
                    }
                }
                val query2 =
                    FirebaseDatabase.getInstance().getReference("Groups").orderByChild("groupName")
                        .startAt(text).endAt(text + "\uf8ff")
                query2.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (data in dataSnapshot.children) {
                            val group = data.getValue(Group::class.java)
                            group?.let {
                                if (!it.groupMaker.equals(com.zone.chatterz.connection.Connection.user)) {
                                    mGroups.add(it)
                                }
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
                val getContext = this@SearchActivity
                val adapter = SearchAdapter(getContext, mSearchUser, mGroups, "Group")
                searchedView.adapter = adapter
                searchProgressBar.visibility = View.GONE
                searchedView.visibility = View.VISIBLE
            }
        })
    }

    private fun searchUser(text: String?) {

        val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .startAt(text).endAt(text + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val user = data.getValue(User::class.java)
                    user?.let {
                        if (!mSearchUser.contains(user) && !user.id.equals(firebaseUser.uid)) {
                            mSearchUser.add(user)
                        }
                    }
                }
                val query2 =
                    FirebaseDatabase.getInstance().getReference("Users").orderByChild("displayname")
                        .startAt(text).endAt(text + "\uf8ff")
                query2.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (data in dataSnapshot.children) {
                            val user = data.getValue(User::class.java)
                            user?.let {
                                if (!mSearchUser.contains(user) && !user.id.equals(firebaseUser.uid)) {
                                    mSearchUser.add(user)
                                }
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
                val getContext = this@SearchActivity
                val adapter = SearchAdapter(getContext, mSearchUser, arrayListOf(), "People")
                searchedView.adapter = adapter
                searchProgressBar.visibility = View.GONE
                searchedView.visibility = View.VISIBLE
            }
        })
    }

    private fun emptySearchedView() {
        mSearchUser.clear()
        val getContext = this
        val adapter = SearchAdapter(getContext, mSearchUser, arrayListOf(),"People")
        searchedView.adapter = adapter
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
       selectedTab = tab?.position!!
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }
}
