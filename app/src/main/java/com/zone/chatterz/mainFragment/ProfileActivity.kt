package com.zone.chatterz.mainFragment

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.*
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import java.io.File


open class ProfileActivity : AppCompatActivity(), View.OnClickListener,
    TabLayout.OnTabSelectedListener {

    private lateinit var profile_img_profile: CircularImageView
    private lateinit var display_unique_name: TextView
    private lateinit var about_me_profile: TextView

    private lateinit var following_btn_profile : RelativeLayout
    private lateinit var followers_btn_profile : RelativeLayout
    private lateinit var followers_count : TextView
    private lateinit var following_count : TextView

    private lateinit var tab_layout_profile: TabLayout
    private lateinit var viewPager_profile: ViewPager

    private lateinit var viewPager_adapter: ViewPagerAdapter

    private val fragments: MutableList<Fragment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        this.profile_img_profile = findViewById(R.id.profile_img_profile)
        this.display_unique_name = findViewById(R.id.display_unique_name)
        this.about_me_profile = findViewById(R.id.about_me_profile)

        this.followers_btn_profile = findViewById(R.id.followers_btn_profile)
        this.following_btn_profile = findViewById(R.id.following_btn_profile)
        this.followers_count = findViewById(R.id.followers_count)
        this.following_count = findViewById(R.id.following_count)

        this.tab_layout_profile = findViewById(
            R.id.tab_layout_profile
        )
        this.viewPager_profile = findViewById(R.id.viewPager_profile)

        setUpViewPager()
        loadUserData()

        this.following_btn_profile.setOnClickListener(this)
        this.followers_btn_profile.setOnClickListener(this)
    }

    private fun setUpViewPager() {
        //load fragments
        loadFragments()
        //loading viewpager
        this.viewPager_adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        viewPager_profile.adapter = this.viewPager_adapter
        viewPager_profile.offscreenPageLimit = 2

        //set up custom height
        setViewPagerCustomHeight()

        //set up viewpager on tab
        tab_layout_profile.setupWithViewPager(viewPager_profile)
        //set up icons
        setUptabIcons()
        //set up selected listener
        tab_layout_profile.setOnTabSelectedListener(this)
    }

    private fun setViewPagerCustomHeight() {
        val layoutParams = viewPager_profile.layoutParams
        val displayMetrics = DisplayMetrics()
        val tabParams = tab_layout_profile.layoutParams
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels - tabParams.height - getStatusBarHeight()
        layoutParams.height = height
        viewPager_profile.layoutParams = layoutParams
    }


    private fun setUptabIcons() {
        tab_layout_profile.getTabAt(0)?.setIcon(R.drawable.ic_home_tab_profile)
        tab_layout_profile.getTabAt(1)?.setIcon(R.drawable.ic_videos_tab_profile)
        tab_layout_profile.getTabAt(2)?.setIcon(R.drawable.ic_archive_tab_profile)
    }

    private fun loadFragments() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        fragments.add(PhotosPostProfileFragment(this,displayMetrics.widthPixels/3))
        fragments.add(VideosProfileFragment())
        fragments.add(ArchiveFragment())
    }

    private fun loadUserData() {
        this.display_unique_name.text = com.zone.chatterz.data.UserData.username

        this.about_me_profile.text = com.zone.chatterz.data.UserData.bio

        if (!com.zone.chatterz.data.UserData.imageUrl.isEmpty()) {
            Glide.with(this).load(com.zone.chatterz.data.UserData.imageUrl)
                .into(profile_img_profile)
        }

        FirebaseMethods.singleValueEvent(Connection.followersRef+ File.separator+Connection.user,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val count = dataSnapshot.childrenCount.toString()
                followers_count.text = count
            }
        })
        FirebaseMethods.singleValueEvent(Connection.followingRef+ File.separator+Connection.user,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val count = dataSnapshot.childrenCount.toString()
                following_count.text = count
            }
        })
    }

    override fun onClick(v: View?) {
        when(v){
            followers_btn_profile->{
                val intent = Intent(this,FollowingFollowersActivity::class.java)
                intent.putExtra("from","followers")
                startActivity(intent)
            }
            following_btn_profile->{
                val intent = Intent(this,FollowingFollowersActivity::class.java)
                intent.putExtra("from","following")
                startActivity(intent)
            }
            else->{
                return
            }
        }
    }

    class ViewPagerAdapter(fm: FragmentManager, fragments: MutableList<Fragment>) :
        FragmentPagerAdapter(fm) {
        private val fragments = fragments
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {

    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {

    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        p0?.let {
            viewPager_profile.setCurrentItem(it.position)
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
