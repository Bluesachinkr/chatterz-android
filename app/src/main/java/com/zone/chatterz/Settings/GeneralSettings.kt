package com.zone.chatterz.Settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.UploadTask.TaskSnapshot
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.Model.User
import com.zone.chatterz.R
import com.zone.chatterz.Requirements.JpegImageCompressor
import java.io.File

class GeneralSettings : AppCompatActivity() {

    private lateinit var addAccountImage : ImageView
    private lateinit var accountImage : CircularImageView
    private val REQUESTCODE_PHOTO = 1

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var storageReference: StorageReference

    private lateinit var accountImgUrl : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_settings)

        mAuth = FirebaseAuth.getInstance()
        addAccountImage = findViewById(R.id.addProfilePhoto)
        accountImage = findViewById(R.id.accountImage)

        addAccountImage.setOnClickListener {

            val intent =  Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,REQUESTCODE_PHOTO)

        }
        loadProfileImage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK && requestCode==REQUESTCODE_PHOTO && data!=null){

            val uri = data.data

            val bitmap = BitmapFactory.decodeStream(uri?.let { contentResolver.openInputStream(it) })
            val compressedImg = JpegImageCompressor.imageCompression(this,bitmap)
            firebaseUser = mAuth.currentUser!!
            storageReference = FirebaseStorage.getInstance().getReference("profileImages/"+firebaseUser.uid+".jpg")

           storageReference.putBytes(compressedImg)
            storageReference.downloadUrl.addOnSuccessListener {
                uri ->
                accountImgUrl = uri.toString()
            }.addOnFailureListener {
                exception ->
                accountImgUrl = exception.toString()
            }

            loadImgToDatabase()
        }
    }

    private fun loadImgToDatabase(){
        storageReference = FirebaseStorage.getInstance().getReference("profileImages/"+firebaseUser.uid+".jpg")
        val databaseReference =  FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                for(data in p0.children){
                    val user=data.getValue(User::class.java)
                    if (user != null) {
                        if(user.id.equals(firebaseUser.uid)){

                            val hashMap = HashMap<String,Any>()
                                hashMap.put("imageUrl",accountImgUrl)
                            hashMap.put("bio","Iam not cool")
                            data.ref.updateChildren(hashMap)
                            break
                        }
                    }
                }
            }
        })
    }
    private fun loadProfileImage(){
        val firebaseUser =  mAuth.currentUser!!
        val databaseReference  = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
              for(data in p0.children){
                  val user=data.getValue(User::class.java)
                  if(user!=null){
                      if(user.id.equals(firebaseUser.uid)){
                          if(user.imageUrl.equals("null")){
                             accountImage.setImageResource(R.drawable.new_group_icon)
                          }else {
                              Glide.with(this@GeneralSettings).load(user.imageUrl).into(accountImage)
                          }
                          break
                      }
                  }
              }
            }

        })
    }
}
