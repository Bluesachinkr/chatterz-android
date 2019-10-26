package com.zone.chatterz.mainFragment

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mikhaellopez.circularimageview.CircularImageView
import Interfaces.DrawerLocker
import com.zone.chatterz.Model.User
import com.zone.chatterz.R

open class ProfileActivity : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var profileImg : CircularImageView
    private lateinit var userName : TextView
    private lateinit var textStatus : TextView
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        mAuth = FirebaseAuth.getInstance()

        profileImg = view.findViewById(R.id.ProfileImage)
        userName = view.findViewById(R.id.userName_Profile)
        textStatus = view.findViewById(R.id.userProfileTextStatus)
        toolbar = view.findViewById(R.id.toolbarProfile)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)

        (activity as DrawerLocker).setDrawerLockerEnabled(true)

        loadProfileData()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.profile_menu_appbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {

            R.id.settings_profile -> {
                (activity as DrawerLocker).openDrawer()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return false
    }

    private fun loadProfileData(){
       firebaseUser = mAuth.currentUser!!
        databaseReference  = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children){
                    val user = data.getValue(User::class.java)
                    if(user!=null){
                        if(user.id.equals(firebaseUser.uid)){
                            setProfileLayout(user)
                            break
                        }
                    }
                }
            }
        })
    }

    private fun setProfileLayout(user : User){

        userName.text = user.username
        textStatus.text = user.bio

        if(!user.imageUrl.equals("null")){
            Glide.with(this).load(user.imageUrl).into(profileImg)
        }
    }

}
