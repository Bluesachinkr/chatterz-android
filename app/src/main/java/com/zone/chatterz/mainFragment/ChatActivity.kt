package com.zone.chatterz.mainFragment

import com.zone.chatterz.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*


open class ChatActivity : Fragment() {

    private lateinit var fragmentList: ArrayList<Fragment>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_chat, container, false)
        view.toolbarMain.setTitle("Chats")
        view.tabBarLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewpager.currentItem = p0!!.position
            }
        })
        view.viewpager.currentItem = 0
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tabBarLayout.addTab(tabBarLayout.newTab().setIcon(R.drawable.ic_person))
        tabBarLayout.addTab(tabBarLayout.newTab().setIcon(R.drawable.ic_group))
        tabBarLayout.addTab(tabBarLayout.newTab().setIcon(R.drawable.ic_friends))
        tabBarLayout.addTab(tabBarLayout.newTab().setIcon(R.drawable.icon_online))

        tabBarLayout.setTabswithCustomWidth(3)

        val fragmentManager = getFragmentManager()
        fragmentList = arrayListOf()
        addFramenttoFragmentList(fragmentList)
        val adapters = PagerAdapter(fragmentManager, fragmentList)
        viewpager.adapter = adapters

        viewpager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{

            val hideAppbarAtPosition = 3

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

              /*  if(appBarLayout !=null){

                    var mBottom = appBarLayout.bottom.toFloat()

                    if(position == hideAppbarAtPosition){

                        var y= (positionOffset*mBottom) - mBottom

                        if( y == mBottom){
                            val h = appBarLayout.height as Float
                            if(mBottom<h){
                                mBottom=h
                            }
                            y = -mBottom - mBottom / 8f

                        }
                        appBarLayout.translationY = y
                    }else if(appBarLayout.translationY != 0f){
                        appBarLayout.translationY = 0f
                    }
                }*/
            }

        })
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabBarLayout))
    }

    private fun addFramenttoFragmentList(list: ArrayList<Fragment>) {
        list.add(PrivateChats())
        list.add(GroupChat())
        list.add(FollowerActivity())
        list.add(OnlineActivity())
    }

    private fun TabLayout.setTabswithCustomWidth(tabPosition: Int) {
        //Take LinearLayout of last tab and change there weight to the .4f
        val linearLayout = (tabBarLayout.getChildAt(0) as LinearLayout).getChildAt(tabPosition) as LinearLayout
        val customParams = linearLayout.layoutParams as LinearLayout.LayoutParams
        customParams.weight = .4f
        linearLayout.layoutParams = customParams

    }

}
