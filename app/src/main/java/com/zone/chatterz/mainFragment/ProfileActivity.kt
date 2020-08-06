package com.zone.chatterz.mainFragment

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
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
import com.zone.chatterz.model.User
import com.zone.chatterz.settings.EditProfileActivity
import com.zone.chatterz.singleChat.ChatMessageActivity
import java.io.File
import java.lang.StringBuilder


open class ProfileActivity : AppCompatActivity(), View.OnClickListener,
    TabLayout.OnTabSelectedListener {

    private lateinit var user: String
    private lateinit var forPurpose: String
    private var isFollowing : Boolean = false

    private lateinit var profile_img_profile: CircularImageView
    private lateinit var display_unique_name: TextView
    private lateinit var display_user_name : TextView
    private lateinit var about_me_profile: TextView
    private lateinit var back_arrow_profile : ImageView

    private lateinit var setting_profile_btn: RelativeLayout
    private lateinit var message_profile_btn: RelativeLayout

    private lateinit var edit_profile: RelativeLayout
    private lateinit var follow_btn_check_profile: RelativeLayout
    private lateinit var following_btn_profile: RelativeLayout
    private lateinit var followers_btn_profile: RelativeLayout
    private lateinit var follow_following_btn_profile_text : TextView
    private lateinit var followers_count: TextView
    private lateinit var following_count: TextView

    private lateinit var tab_layout_profile: TabLayout
    private lateinit var viewPager_profile: ViewPager

    private lateinit var viewPager_adapter: ViewPagerAdapter

    private val fragments: MutableList<Fragment> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val intentFrom = intent
        this.forPurpose = intentFrom.extras?.get("for") as String
        this.user = intentFrom.extras?.get("user") as String
        if(forPurpose.equals("checking")){
            isFollowing = intentFrom.extras?.get("isFollowing") as Boolean
        }

        profile_img_profile = findViewById(R.id.profile_img_profile)
        display_unique_name = findViewById(R.id.display_unique_name)
        display_user_name = findViewById(R.id.display_user_name)
        about_me_profile = findViewById(R.id.about_me_profile)

        back_arrow_profile = findViewById(R.id.back_arrow_profile)

        setting_profile_btn = findViewById(R.id.settting_profile_btn)
        message_profile_btn = findViewById(R.id.message_profile_btn)

        edit_profile = findViewById(R.id.edit_profile)
        follow_btn_check_profile = findViewById(R.id.follow_btn_check_profile)
        followers_btn_profile = findViewById(R.id.followers_btn_profile)
        following_btn_profile = findViewById(R.id.following_btn_profile)
        follow_following_btn_profile_text = findViewById(R.id.follow_following_btn_profile_text)
        followers_count = findViewById(R.id.followers_count)
        following_count = findViewById(R.id.following_count)

        tab_layout_profile = findViewById(
            R.id.tab_layout_profile
        )
        viewPager_profile = findViewById(R.id.viewPager_profile)

        setUpViewPager()
        loadUserData()

        following_btn_profile.setOnClickListener(this)
        followers_btn_profile.setOnClickListener(this)

        message_profile_btn.setOnClickListener(this)
        edit_profile.setOnClickListener(this)
        follow_btn_check_profile.setOnClickListener(this)

        loadProfileForPurpose()
    }

    private fun loadProfileForPurpose() {
        if (this.forPurpose.equals("checking")) {
            edit_profile.visibility = View.GONE
            follow_btn_check_profile.visibility = View.VISIBLE
            setting_profile_btn.visibility = View.GONE
            message_profile_btn.visibility = View.VISIBLE
            if(isFollowing){
                follow_following_btn_profile_text.text = "Following"
            }else{
                follow_following_btn_profile_text.text = "Follow"
            }
        }
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
        val tabParams = tab_layout_profile.layoutParams
        val displayMetrics = DisplayMetrics()
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
        fragments.add(PhotosPostProfileFragment(this,user))
        fragments.add(VideosProfileFragment())
        fragments.add(ArchiveFragment())
    }

    private fun loadUserData() {
        FirebaseMethods.singleValueEvent(Connection.userRef+File.separator+user,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(User::class.java)
                data?.let {
                    display_user_name.text = data.username
                    about_me_profile.text = data.bio
                    display_unique_name.text = data.displayName
                    //load image
                    if (!data.imageUrl.equals("null")) {
                        Glide.with(this@ProfileActivity).load(data.imageUrl)
                            .into(profile_img_profile)
                    }else{
                        profile_img_profile.setImageResource(R.drawable.google_logo)
                    }
                }
            }
        })

        FirebaseMethods.singleValueEvent(Connection.followersRef + File.separator + user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val count = dataSnapshot.childrenCount.toString()
                    followers_count.text = count
                }
            })
        FirebaseMethods.singleValueEvent(Connection.followingRef + File.separator + user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val count = dataSnapshot.childrenCount.toString()
                    following_count.text = count
                }
            })
    }

    override fun onClick(v: View?) {
        when (v) {
            followers_btn_profile -> {
                val intent = Intent(this, FollowingFollowersActivity::class.java)
                intent.putExtra("from", "followers")
                startActivity(intent)
            }
            following_btn_profile -> {
                val intent = Intent(this, FollowingFollowersActivity::class.java)
                intent.putExtra("from", "following")
                startActivity(intent)
            }
            edit_profile->{
                startActivity(Intent(this,EditProfileActivity::class.java))
            }
            message_profile_btn->{
                val intent = Intent(this, ChatMessageActivity::class.java)
                intent.putExtra("UserId", user)
                startActivity(intent)
            }
            else -> {
                return
            }
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
