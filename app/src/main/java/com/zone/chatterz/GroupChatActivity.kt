package com.zone.chatterz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.zone.chatterz.Interfaces.DrawerLocker

open class GroupChatActivity : Fragment(), View.OnClickListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var leftNavigationView: NavigationView
    private lateinit var openDrawerBtn: ImageView
    private lateinit var openMembersDrawer: ImageView
    private lateinit var groupName: TextView
    private lateinit var content: RelativeLayout
    private lateinit var rightNavigationView: NavigationView
    private lateinit var groupListView: RecyclerView
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.activity_group_chat, container, false)
        drawer = view.findViewById(R.id.drawerGroups)
        leftNavigationView = view.findViewById(R.id.drawerOpen)
        rightNavigationView = view.findViewById(R.id.drawerMembers)
        openDrawerBtn = view.findViewById(R.id.drawerOpenBtn)
        openMembersDrawer = view.findViewById(R.id.membersBtn)
        groupName = view.findViewById(R.id.groupName)
        content = view.findViewById(R.id.contentGroupChats)
        //  groupListView = leftNavigationView.findViewById(R.id.GroupsList)
        toolbar = view.findViewById(R.id.groupToolbar)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        (activity as DrawerLocker).setDrawerLockerEnabled(false)

        drawer.setScrimColor(Color.TRANSPARENT)

        setDrawerWidth()
        drawer.addDrawerListener(object : ActionBarDrawerToggle(
            this.activity,
            drawer,
            R.string.openDrawer,
            R.string.closeDrawer
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val width = drawerView.width * slideOffset
                if (drawerView != rightNavigationView) {
                    content.translationX = width
                }
            }
        })

        openDrawerBtn.setOnClickListener(this)
        openMembersDrawer.setOnClickListener(this)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.group_menu_appbar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.addNewgroup -> {
                val intetnt = Intent(this.activity, CreateNewGroup::class.java)
                startActivity(intetnt)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            openDrawerBtn -> {
                drawer.openDrawer(leftNavigationView)
                //showGroups()
            }
            openMembersDrawer -> {
                drawer.openDrawer(rightNavigationView)
                //showMembers()
            }
        }
    }

    private fun showGroups() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setDrawerWidth() {
        val width = resources.displayMetrics.widthPixels / 4
        var params = leftNavigationView.layoutParams as DrawerLayout.LayoutParams
        params.width = width
        leftNavigationView.layoutParams = params
    }

    private fun showMembers() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
