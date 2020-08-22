package com.zone.chatterz.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.zone.chatterz.R
import java.lang.StringBuilder

class FollowingFollowersActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var unique_display_name: TextView
    private lateinit var back_from_followers_following: ImageView

    private lateinit var followers_following_tab_layout: TabLayout
    private lateinit var followers_following_viewpager: ViewPager

    private val fragments = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_followers)

        getFragments()

        this.unique_display_name = findViewById(R.id.unique_display_name)
        this.back_from_followers_following = findViewById(R.id.back_from_followers_following)

        this.followers_following_tab_layout = findViewById(R.id.followers_following_tab_layout)
        this.followers_following_viewpager = findViewById(R.id.followers_following_viewpager)

        this.back_from_followers_following.setOnClickListener {
            finish()
        }

        setUpViewPager()

        getIntentData()
    }

    private fun getIntentData() {
        val intentData = intent
        val str = intentData.extras?.get("from") as String
        if(str.equals("followers")){
            followers_following_viewpager.setCurrentItem(0)
        }else{
            followers_following_viewpager.setCurrentItem(1)
        }
    }

    private fun getFragments() {
        this.fragments.clear()
        fragments.add(FollowersFragment(this))
        fragments.add(FollowingFragment(this))
    }

    private fun setUpViewPager() {
        //set up height
        setViewPagerHeight()

        val adapter = ViewPagerAdapter(supportFragmentManager, fragments)

        //set up adapter to viewpager
        this.followers_following_viewpager.adapter = adapter

        //tablayout set up viewpager
        this.followers_following_tab_layout.setupWithViewPager(this.followers_following_viewpager)

        this.followers_following_tab_layout.setOnTabSelectedListener(this)

        setUpTabs()
    }

    private fun setUpTabs() {
        val followersCount = StringBuilder(100.toString())
        followersCount.append(" followers")
        val followingCount = StringBuilder(100.toString())
        followingCount.append(" following")

        this.followers_following_tab_layout.getTabAt(0)?.setText(followersCount.toString())
        this.followers_following_tab_layout.getTabAt(1)?.setText(followingCount.toString())
    }

    private fun setViewPagerHeight() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val params = followers_following_viewpager.layoutParams
        val tabParams = followers_following_tab_layout.layoutParams
        params.height = displayMetrics.heightPixels - (2 * tabParams.height) - getStatusBarHeight()
        followers_following_viewpager.layoutParams = params
    }

    private class ViewPagerAdapter(fm: FragmentManager, fragments: MutableList<Fragment>) :
        FragmentPagerAdapter(fm) {

        private val fragments = fragments

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let {
            this.followers_following_viewpager.setCurrentItem(it.position)
        }
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}