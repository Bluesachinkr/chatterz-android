package com.zone.chatterz.inferfaces

interface CommentReloadListener {
    fun onReload(id : String,postId :String,hashMap: HashMap<String,Boolean>,parent : String)
}