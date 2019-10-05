package com.zone.chatterz

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

open class PagerAdapter(fm: FragmentManager?,arrayLists: ArrayList<Fragment>) :
    FragmentPagerAdapter(fm){

    private var numberOFTabs : Int = 3
    private var arrayList : ArrayList<Fragment> = arrayLists

    override fun getItem(position: Int): Fragment? {
        return arrayList.get(position)
    }

    override fun getCount(): Int {
        return numberOFTabs
    }
}