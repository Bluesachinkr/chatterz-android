package com.zone.chatterz.mainFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.firnedAdapter
import com.zone.chatterz.Interfaces.DrawerLocker
import com.zone.chatterz.Model.User
import com.zone.chatterz.R


open class SearchActivity : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        (activity as DrawerLocker).setDrawerLockerEnabled(false)

        return view
    }

}
