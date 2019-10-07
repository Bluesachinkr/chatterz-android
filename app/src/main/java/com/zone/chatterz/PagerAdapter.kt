package com.zone.chatterz

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

open class PagerAdapter(fm: FragmentManager?,arrayLists: ArrayList<Fragment>) :
    FragmentPagerAdapter(fm){

    private var arrayList : ArrayList<Fragment> = arrayLists

    override fun getItem(position: Int): Fragment? {
        val fragment = arrayList.get(position)
        fragment.onResume()
        return fragment
    }

    override fun getCount(): Int {
        return arrayList.size
    }

}