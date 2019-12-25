package com.zone.chatterz

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.Model.Group
import com.zone.chatterz.Requirements.JpegImageCompressor
import kotlin.concurrent.fixedRateTimer

class CreateNewGroup : AppCompatActivity(),View.OnClickListener {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var groupImage : CircularImageView
    private lateinit var editButton : ImageView
    private lateinit var groupNameEdittext : EditText
    private lateinit var nextBtn :  TextView
    private lateinit var backBtn : ImageView
    private lateinit var compressedImg : ByteArray
    private lateinit var reference: String
    private val REQUESTCODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_group)

        mAuth  = FirebaseAuth.getInstance()
        groupImage = findViewById(R.id.groupImage)
        editButton = findViewById(R.id.cameraEditBtn)
        groupNameEdittext = findViewById(R.id.groupNameEdittext)
        nextBtn = findViewById(R.id.nextBtn_create_new_group)
        backBtn = findViewById(R.id.backArrow_CreateNewGroup)

        backBtn.setOnClickListener(this)
        nextBtn.setOnClickListener(this)
        editButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            nextBtn->{
                createGroup()
            }
            backBtn->{
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("fromReturn","createGroup")
                startActivity(intent)
            }
            editButton->{
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,REQUESTCODE)
            }
        }
    }

    private fun createGroup() {
        val grpName = groupNameEdittext.text.toString()
        val hashMap = HashMap<String,String>()
        hashMap.put("groupName",grpName)
        hashMap.put("groupImgUrl","null")
        hashMap.put("groupMaker",mAuth.currentUser?.uid.toString())
        databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child("Groups").push().setValue(hashMap).addOnSuccessListener {
            reference = databaseReference.ref.toString()
            uploadImage()
            addGroupMembers()
            joinInGroup()
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("Back","CreateNewGroup")
            startActivity(intent)
        }
    }

    private fun addGroupMembers() {
        val user = mAuth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("GroupMembers").child(reference)
        databaseReference.child(user.uid).push().setValue(true)
    }

    private fun joinInGroup(){
        val user = mAuth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("GroupJoined").child(user.uid)
        databaseReference.child(reference).push().setValue(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUESTCODE && resultCode == Activity.RESULT_OK && data!=null){
             val uri = data.data
            val bitmap =
                BitmapFactory.decodeStream(uri?.let { contentResolver.openInputStream(it) })
            compressedImg = JpegImageCompressor.imageCompression(bitmap)
        }
    }

    private fun loadImageGroup(url : String) {
        databaseReference  = FirebaseDatabase.getInstance().getReference("Groups/"+reference)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
               val data =p0.getValue(Group::class.java)
                if(data!=null){
                    val hashMap = HashMap<String,Any>()
                    hashMap.put("groupImgUrl",url)
                    p0.ref.updateChildren(hashMap)
                }
            }
        })
    }

    private fun uploadImage(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups")
        storageReference = FirebaseStorage.getInstance().getReference("groupImages/"+reference+".jpg")
        val uploadTask = storageReference.putBytes(compressedImg)
        uploadTask.addOnSuccessListener {
            uploadTask.continueWithTask {
                if(!it.isSuccessful){
                    throw it.exception!!
                }
                return@continueWithTask storageReference.downloadUrl
            }.addOnCompleteListener {
                if(it.isSuccessful){
                    val uri = it.result
                    val imageUrl = uri.toString()
                    loadImageGroup(imageUrl)
                }
            }
        }
    }
}
