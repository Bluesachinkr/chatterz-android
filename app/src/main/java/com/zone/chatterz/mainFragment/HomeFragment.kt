package com.zone.chatterz.mainFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.zone.chatterz.singleChat.ChatActivity
import com.zone.chatterz.R
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.User

class HomeFragment(context: Context) : Fragment(), View.OnClickListener {

    private val mContext = context
    private lateinit var profile_image_home_frag: ImageView
    private lateinit var chat_btn_home_frag: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        //Initialization of elements of Home fragment
        profile_image_home_frag = view.findViewById(R.id.profile_image_home_frag)
        chat_btn_home_frag = view.findViewById(R.id.chat_btn_home_frag)

        //Setting on click listener of buttons in home fragment
        chat_btn_home_frag.setOnClickListener(this)
        profile_image_home_frag.setOnClickListener(this)

        setProfilePic()
        return view
    }

    private fun setProfilePic() {
        FirebaseMethods.singleValueEventChild(Connection.userRef, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    val imageUrl = it.imageUrl
                    if (!user.imageUrl.equals("null")) {
                        Glide.with(mContext).load(user.imageUrl).into(profile_image_home_frag)
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
            profile_image_home_frag->{
                //profile button
               /* startActivity(Intent(mContext, ProfileActivity::class.java))*/
            }
            else -> {
                return
            }
        }
    }
}