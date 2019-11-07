package com.zone.chatterz.mainFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zone.chatterz.Adapter.SearchAdapter
import com.zone.chatterz.Interfaces.DrawerLocker
import com.zone.chatterz.Model.User
import com.zone.chatterz.R


open class SearchActivity : Fragment() {

    private lateinit var searchBar : SearchView
    private lateinit var searchedView : RecyclerView
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mSearchUser : MutableList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        mAuth = FirebaseAuth.getInstance()

        firebaseUser = mAuth.currentUser!!
        searchBar = view.findViewById(R.id.searchBar)
        searchedView = view.findViewById(R.id.searchedView)

        (activity as DrawerLocker).setDrawerLockerEnabled(false)
        mSearchUser = mutableListOf()

        val layoutManager = LinearLayoutManager(this.context)
        searchedView.layoutManager = layoutManager

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchUser(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               searchUser(newText)
                return true
            }

        })

        return view
    }

    private fun searchUser(text : String?){
        val query  = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .startAt(text).endAt(text+"\uf8ff")

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children){
                    val user = data.getValue(User::class.java)
                    user?.let { mSearchUser.add(user) }
                }
                val getContext = context!!
                val adapter = SearchAdapter(getContext,mSearchUser)
                searchedView.adapter = adapter
            }

        })
    }

}
