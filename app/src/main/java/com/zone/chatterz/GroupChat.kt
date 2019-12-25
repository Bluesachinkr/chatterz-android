package com.zone.chatterz

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.Adapter.GroupAdapter
import com.zone.chatterz.Model.Group
import kotlinx.android.synthetic.main.fragment_group_chat.*

open class GroupChat : Fragment() {

    private lateinit var firebaseUser : FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerViewGroup : RecyclerView

    private lateinit var mGroupList : MutableList<Group>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_chat, container, false)
        val newGroupCreateButton = view.findViewById<FloatingActionButton>(R.id.createNewGroup)
        newGroupCreateButton.setOnClickListener {
            val intent= Intent(context,CreateNewGroup::class.java)
            startActivity(intent)
        }
        //Setting Recycler View in the frameLayout
        recyclerViewGroup = view.findViewById<RecyclerView>(R.id.RecyclerViewGroups)
        recyclerViewGroup.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerViewGroup.layoutManager = layoutManager
        loadGroups()

        return view
    }

    private fun loadGroups(){
        mGroupList = mutableListOf()

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
               mGroupList.clear()
                for (dataSet in p0.children){
                    val group= dataSet.getValue(Group::class.java)
                        group?.let { mGroupList.add(group) }
                }

                val getContext = context
                if(getContext!=null){
                    val adapter = GroupAdapter(getContext,mGroupList)
                    recyclerViewGroup.adapter  = adapter
                }
            }

        })
    }
}
