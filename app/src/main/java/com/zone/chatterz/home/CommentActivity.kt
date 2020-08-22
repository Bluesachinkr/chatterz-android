package com.zone.chatterz.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.zone.chatterz.R
import com.zone.chatterz.adapter.CommentAdapter
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import com.zone.chatterz.inferfaces.CommentLayoutListener
import com.zone.chatterz.inferfaces.CommentReloadListener
import com.zone.chatterz.model.Comment
import com.zone.chatterz.common.Timings
import java.io.File

class CommentActivity : AppCompatActivity(), View.OnClickListener, CommentLayoutListener,
    CommentReloadListener {

    private lateinit var comments_recycler: RecyclerView
    private lateinit var comment_add_button: ImageView
    private lateinit var comment_edittext: EditText

    private lateinit var postId: String
    private var toReply: String = ""
    private var parentComment: String = ""
    private var isComment: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val intent = getIntent()
        this.postId = intent.extras?.get("postId") as String

        this.comments_recycler = findViewById(R.id.comments_recycler)
        this.comment_add_button = findViewById(R.id.comment_add)
        this.comment_edittext = findViewById(R.id.comment_edittext)

        val layoutManager = LinearLayoutManager(this)
        comments_recycler.layoutManager = layoutManager
        openComments(postId)
    }

    override fun onClick(v: View?) {
        when (v) {
            comment_add_button -> {
                addComment()
            }
            else -> {
                return
            }
        }
    }

    private fun addComment() {
        val message: String = comment_edittext.text.toString()
        val hashMap = hashMapOf<String, Any>()
        hashMap.put("postId", postId)
        hashMap.put("message", message)
        hashMap.put("sender", Connection.user)
        hashMap.put("likes", 0)
        hashMap.put("heart", false)
        hashMap.put("time", Timings.getCurrentTime())
        if (isComment) {
            hashMap.put("isComment", true)
            hashMap.put("toReply", "none")
            hashMap.put("parent", "none")
            hashMap.put("isReply", 0)
            val databaseReference =
                FirebaseDatabase.getInstance().getReference(Connection.commentsRef)
                    .child(postId)
            val push = databaseReference.push()
            val str = push.key.toString()
            hashMap.put("commentId", str)
            push.setValue(hashMap)
        } else {
            hashMap.put("isComment", false)
            hashMap.put("toReply", toReply)
            hashMap.put("parent", parentComment)
            hashMap.put("isReply", 0)
            val databaseReference =
                FirebaseDatabase.getInstance().getReference(Connection.commentReplyRef)
                    .child(postId).child(parentComment)
            val push = databaseReference.push()
            val str = push.key.toString()
            hashMap.put("commentId", str)
            push.setValue(hashMap)
            val r =
                Connection.commentsRef + File.separator + postId + File.separator + parentComment
            FirebaseMethods.singleValueEvent(r, object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val comment = dataSnapshot.getValue(Comment::class.java)
                    comment?.let {
                        val count = comment.replyCount
                        dataSnapshot.ref.child("isReply").setValue(count + 1)
                    }
                }
            })
        }
    }

    private fun openComments(id: String) {
        val commentsList = mutableListOf<Comment>()
        val hashMap = hashMapOf<String, Boolean>()
        val reference = Connection.commentsRef + File.separator + id
        FirebaseMethods.addValueEvent(reference, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                var count = dataSnapshot.childrenCount
                if (count >= 1) {
                    for (data in dataSnapshot.children) {
                        val comments = data.getValue(Comment::class.java)
                        comments?.let {
                            commentsList.add(comments)
                            if (comments.replyCount > 0) {
                                count += comments.replyCount
                            }
                            hashMap.put(comments.sender, false)
                        }
                    }
                    val adapter = CommentAdapter(
                        this@CommentActivity,
                        commentsList,
                        this@CommentActivity,
                        this@CommentActivity,
                        hashMap
                    )
                    comments_recycler.adapter = adapter
                }
            }
        })
    }

    override fun onReplyInfo(parent: String, to: String) {
        this.parentComment = parent
        this.toReply = to
    }

    override fun onCommentReplyEdit(message: String) {
        this.comment_edittext.text = message.toEditable()
        this.isComment = false
    }

    override fun onReload(
        id: String,
        postId: String,
        hashMap: HashMap<String, Boolean>,
        parent: String
    ) {
        val reference = Connection.commentsRef + File.separator + postId
        HomeActivity.commentsList.clear()
        FirebaseMethods.addValueEvent(reference, object : RequestCallback() {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val count = dataSnapshot.childrenCount
                for (data in dataSnapshot.children) {
                    val comments = data.getValue(Comment::class.java)
                    comments?.let {
                        HomeActivity.commentsList.add(comments)
                        if (hashMap[comments.sender]!!) {
                            val ref =
                                Connection.commentReplyRef + File.separator + postId + File.separator + parent
                            FirebaseMethods.addValueEvent(ref, object : RequestCallback() {
                                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                                    for (data in dataSnapshot.children) {
                                        val com = data.getValue(Comment::class.java)
                                        com?.let {
                                            HomeActivity.commentsList.add(com)
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
                val adapter = CommentAdapter(
                    this@CommentActivity,
                    HomeActivity.commentsList, this@CommentActivity, this@CommentActivity, hashMap
                )
                comments_recycler.adapter = adapter
            }
        })
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}