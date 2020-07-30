package com.zone.chatterz.services

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.iceteck.silicompressorr.SiliCompressor
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Post
import java.io.ByteArrayOutputStream


class PostUploadService : IntentService(null) {

    private var resultImageArray : ByteArray? = null

    override fun onHandleIntent(intent: Intent?) {
        val time = intent?.extras?.get("time") as String
        val description = intent?.extras?.get("description") as String
        val resultPath = intent?.extras?.get("resultPath") as String
        compressImage(resultPath)
        if (time != null && description != null) {
            val hashMap = hashMapOf<String, Any>()
            hashMap.put("postOwner", com.zone.chatterz.firebaseConnection.Connection.user)
            hashMap.put("postDescription", description)
            hashMap.put("postTime", time)
            hashMap.put("postImage", "null")
            hashMap.put("postId", "null")

            val databaseReference = FirebaseDatabase.getInstance().reference
            databaseReference.child(com.zone.chatterz.firebaseConnection.Connection.postRef)
                .child(com.zone.chatterz.firebaseConnection.Connection.user)
                .push()
                .setValue(hashMap)

            FirebaseMethods.addValueEventChild(com.zone.chatterz.firebaseConnection.Connection.postRef,
                object : RequestCallback() {
                    override fun onDataChanged(dataSnapshot: DataSnapshot) {
                        var postId: String = ""
                        for (data in dataSnapshot.children) {
                            val post = data.getValue(Post::class.java)
                            if (post != null) {
                                if (post.postTime.equals(time) && post.postDescription.equals(
                                        description
                                    )
                                ) {
                                    postId= data.ref.key.toString()
                                    hashMap.clear()
                                    hashMap.put("postId", postId)
                                    data.ref.updateChildren(hashMap)
                                    resultImageArray?.let {
                                        uploadImage(postId,data,it)
                                    }
                                    break
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun uploadImage(
        postId: String,
        data: DataSnapshot,
        resultArray: ByteArray
    ) {
        val storageRef = FirebaseStorage.getInstance()
            .getReference("postImages/" + postId + ".jpg")

        val uploadTask = storageRef.putBytes(resultArray)
        //uploadTask
        uploadTask.addOnSuccessListener {
            uploadTask.continueWithTask {
                if (!it.isSuccessful) {
                    throw it.exception!!
                }
                return@continueWithTask storageRef.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    val uri = it.result
                    val imgUrl = uri.toString()
                    val hashMap = hashMapOf<String, Any>()
                    hashMap.put("postImage", imgUrl)
                    data.ref.updateChildren(hashMap)
                    return@addOnCompleteListener
                }
            }
        }
    }

    private fun compressImage(imagePath : String){
        val imageBitmap = SiliCompressor.with(this).getCompressBitmap(imagePath)
        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        resultImageArray = stream.toByteArray()
    }
}