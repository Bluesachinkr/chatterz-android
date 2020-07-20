package com.zone.chatterz.mainFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.zone.chatterz.ChatActivity
import com.zone.chatterz.R

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var camera_btn_home_frag: ImageView
    private lateinit var chat_btn_home_frag: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        camera_btn_home_frag = view.findViewById(R.id.camera_btn_home_frag)
        chat_btn_home_frag = view.findViewById(R.id.chat_btn_home_frag)

        chat_btn_home_frag.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v) {
            chat_btn_home_frag -> {
                startActivity(Intent(context, ChatActivity::class.java))
            }
            camera_btn_home_frag -> {

            }
            else -> {
                return
            }
        }
    }
}