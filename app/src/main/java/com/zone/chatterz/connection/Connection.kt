package com.zone.chatterz.connection

import com.google.firebase.auth.FirebaseAuth

open class Connection {
    companion object {
        //user id
        val  user : String = FirebaseAuth.getInstance().currentUser!!.uid
        //database reference
        val userRef: String = "Users"
        val groupsRef: String = "Groups"
        val groupMemRef: String = "GroupMembers"
        val groupJoinedRef: String = "GroupJoined"
        val groupChats: String = "GroupChats"
        val userChats : String = "Chats"
        val tokens : String = "Tokens"
        val postRef : String = "Post"
        val friendRef : String= "Friends"
        val followersRef : String = "Followers"
        val followingRef : String = "Following"
        val likesRef : String = "Likes"
        val commentsRef : String = "Comments"
        val commentReplyRef : String = "CommentsReply"
        val archivePost : String = "Archive"
        val notification : String = "PopUp"
    }
}