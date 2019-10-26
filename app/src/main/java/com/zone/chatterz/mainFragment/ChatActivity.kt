package com.zone.chatterz.mainFragment

import Interfaces.DrawerLocker
import com.zone.chatterz.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.zone.chatterz.Adapter.PagerAdapter
import kotlinx.android.synthetic.main.fragment_chat.*


open class ChatActivity : Fragment() {

    private lateinit var adapter: PagerAdapter
    private lateinit var fragmentList: ArrayList<Fragment>
    private val firstPageIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view :View = inflater.inflate(R.layout.fragment_chat, container, false)

        (activity as DrawerLocker).setDrawerLockerEnabled(false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tabBarLayout.addTab(tabBarLayout.newTab().setIcon(R.drawable.chats_light_icon))
        tabBarLayout.addTab(tabBarLayout.newTab().setIcon(R.drawable.ic_multiple_users_silhouette))
        tabBarLayout.addTab(tabBarLayout.newTab().setIcon(R.drawable.ic_followers))

        fragmentList = arrayListOf()
        addFramentToFragmentList(fragmentList)
        adapter = PagerAdapter(childFragmentManager, fragmentList)
        viewpager.offscreenPageLimit =2
        viewpager.adapter = adapter

        tabBarLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {

                if (p0 != null) {
                    viewpager.currentItem = p0.position
                }
            }
        })

        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabBarLayout))
        viewpager.currentItem = firstPageIndex

    }

    private fun addFramentToFragmentList(list: ArrayList<Fragment>) {
        list.add(RecentActivity())
        list.add(GroupChat())
        list.add(FollowerActivity())
    }

    /* private fun TabLayout.setTabswithCustomWidth(tabPosition: Int) {
         //Take LinearLayout of last tab and change there weight to the .4f
         val linearLayout = (tabBarLayout.getChildAt(0) as LinearLayout).getChildAt(tabPosition) as LinearLayout
         val customParams = linearLayout.layoutParams as LinearLayout.LayoutParams
         customParams.weight = .4f
         linearLayout.layoutParams = customParams

     }*/

}
