package com.zone.chatterz.MainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.zone.chatterz.*
import kotlinx.android.synthetic.main.fragment_chat.*


open class ChatActivity : Fragment() {

    private lateinit var arrayList : ArrayList<Fragment>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_chat, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fragmentManager = getFragmentManager()
        arrayList = arrayListOf()
        addFrament(arrayList)
        val adapters = PagerAdapter(fragmentManager, arrayList)

        viewpager.adapter = adapters
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabBarLayout))
        viewpager.setCurrentItem(0,true)
    }
    private fun addFrament(list: ArrayList<Fragment>){
        list.add(PrivateChats())
        list.add(GroupChat())
        list.add(FollowerActivity())
    }
}
