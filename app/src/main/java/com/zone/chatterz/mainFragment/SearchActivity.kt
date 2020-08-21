package com.zone.chatterz.mainFragment

import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.adapter.SearchAdapter
import com.zone.chatterz.inferfaces.DrawerLocker
import com.zone.chatterz.model.User
import com.zone.chatterz.R


open class SearchActivity : AppCompatActivity() {

    private lateinit var searchBar: SearchView
    private lateinit var searchProgressBar: ProgressBar
    private lateinit var searchedView: RecyclerView
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
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
        searchProgressBar = findViewById(R.id.searchProgressBar)

        mSearchUser = mutableListOf()
        currentSearchtext = ""

        val layoutManager = LinearLayoutManager(this)
        searchedView.layoutManager = layoutManager

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentSearchtext = query!!
                if (query!!.isEmpty()) {
                    emptySearchedView()
                    return false
                } else {
                    searchProgressBar.visibility = View.VISIBLE
                    searchUser(query)
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
                    searchUser(newText)
                    return true
                }
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
                val getContext = this@SearchActivity
                val adapter = SearchAdapter(getContext, mSearchUser)
                searchedView.adapter = adapter
                searchProgressBar.visibility = View.GONE
                searchedView.visibility = View.VISIBLE
            }
        })
    }

    private fun emptySearchedView() {
        mSearchUser.clear()
        val getContext = this
        val adapter = SearchAdapter(getContext, mSearchUser)
        searchedView.adapter = adapter
    }
}
