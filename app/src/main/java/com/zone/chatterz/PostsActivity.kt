package com.zone.chatterz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zone.chatterz.adapter.HomeAdapter
import com.zone.chatterz.model.Post

class PostsActivity : AppCompatActivity() {

    private lateinit var back_from_posts: RelativeLayout
    private lateinit var postsRecyclerView: RecyclerView

    private var list: ArrayList<Post> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        this.back_from_posts = findViewById(R.id.back_from_posts)
        this.postsRecyclerView = findViewById(R.id.postsRecyclerView)

        val ig = intent
        ig?.let {
            list = it.extras?.get("list") as ArrayList<Post>
        }

        val layoutManager = LinearLayoutManager(this)
        postsRecyclerView.layoutManager = layoutManager
        postsRecyclerView.adapter = HomeAdapter(this, list)

    }
}