package com.zone.chatterz.services

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.iceteck.silicompressorr.SiliCompressor
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Post
import java.io.ByteArrayOutputStream
import java.io.File


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

            val databaseReference = FirebaseDatabase.getInstance().reference
            val push =
                databaseReference.child(com.zone.chatterz.firebaseConnection.Connection.postRef)
                    .push()
            val str = push.key.toString()
            hashMap.put("postId",str)
            push.setValue(hashMap)

            FirebaseMethods.singleValueEvent(Connection.postRef+ File.separator+str,object : RequestCallback(){
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    resultImageArray?.let {
                        uploadImage(str,dataSnapshot,it)
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