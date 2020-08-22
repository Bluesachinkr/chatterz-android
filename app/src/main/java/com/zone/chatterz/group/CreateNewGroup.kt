package com.zone.chatterz.group

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import com.iceteck.silicompressorr.SiliCompressor
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.home.MainActivity
import com.zone.chatterz.R
import com.zone.chatterz.model.Group
import com.zone.chatterz.common.JpegImageCompressor
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.collections.HashMap

class CreateNewGroup : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var groupImage: CircularImageView
    private lateinit var editButton: ImageView
    private lateinit var groupNameEdittext: EditText
    private lateinit var nextBtn: TextView
    private lateinit var backBtn: ImageView
    private lateinit var compressedImg: ByteArray
    private val REQUESTCODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_group)

        mAuth = FirebaseAuth.getInstance()
        groupImage = findViewById(R.id.groupImage)
        editButton = findViewById(R.id.cameraEditBtn)
        groupNameEdittext = findViewById(R.id.groupNameEdittext)
        nextBtn = findViewById(R.id.nextButton)
        backBtn = findViewById(R.id.backArrow_CreateNewGroup)

        backBtn.setOnClickListener(this)
        nextBtn.setOnClickListener(this)
        editButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            nextBtn -> {
                createGroup()
            }
            backBtn -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fromReturn", "createGroup")
                startActivity(intent)
            }
            editButton -> {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUESTCODE)
            }
        }
    }

    private fun createGroup() {
        val user = mAuth.currentUser!!
        val grpName = groupNameEdittext.text.toString()
        val hashMap = HashMap<String, String>()
        hashMap.put("groupName", grpName)
        hashMap.put("groupImgUrl", "null")
        hashMap.put("groupMaker", mAuth.currentUser?.uid.toString())
        hashMap.put("id", user.uid + grpName)
        databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child("Groups").push().setValue(hashMap)
        val q = FirebaseDatabase.getInstance().getReference("Groups")
        q.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val v = data.getValue(Group::class.java)
                    if (v != null && v.id.equals(user.uid + grpName)) {
                        val reference = data.ref.key.toString()
                        uploadImage(reference)
                        addGroupMembers(reference)
                        joinInGroup(reference)
                        val hashMap = HashMap<String, Any>()
                        hashMap.put("id", data.ref.key.toString())
                        data.ref.updateChildren(hashMap)
                        break
                    }
                }
            }

        })
        onBackPressed()
    }

    private fun addGroupMembers(reference: String) {
        val user = mAuth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("GroupMembers")
        databaseReference.child(reference).child(user.uid).setValue(true)
    }

    private fun joinInGroup(reference: String) {
        val user = mAuth.currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("GroupJoined").child(user.uid)
        databaseReference.child(reference).setValue(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val bitmap = SiliCompressor.with(this).getCompressBitmap(uri?.path.toString())
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            compressedImg = stream.toByteArray()
            groupImage.setImageURI(uri)
        }
    }

    private fun loadImageGroup(url: String, reference: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups/" + reference)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val data = p0.getValue(Group::class.java)
                if (data != null) {
                    val hashMap = HashMap<String, Any>()
                    hashMap.put("groupImgUrl", url)
                    p0.ref.updateChildren(hashMap)
                }
            }
        })
    }

    private fun uploadImage(ref: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups")
        storageReference =
            FirebaseStorage.getInstance().getReference("groupImages/" + ref + ".jpg")
        val uploadTask = storageReference.putBytes(compressedImg)
        uploadTask.addOnSuccessListener {
            uploadTask.continueWithTask {
                if (!it.isSuccessful) {
                    throw it.exception!!
                }
                return@continueWithTask storageReference.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    val uri = it.result
                    val imageUrl = uri.toString()
                    loadImageGroup(imageUrl, ref)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
