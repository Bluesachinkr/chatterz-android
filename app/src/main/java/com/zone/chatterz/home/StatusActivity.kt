package com.zone.chatterz.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zone.chatterz.inferfaces.DrawerLocker
import com.zone.chatterz.R


open class StatusActivity : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_status, container, false)

        (activity as DrawerLocker).setDrawerLockerEnabled(false)

        return view
    }
}
