package com.zone.chatterz.settings

import com.zone.chatterz.inferfaces.OnEditListener
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.MainActivity
import com.zone.chatterz.model.User
import com.zone.chatterz.R
import com.zone.chatterz.requirements.JpegImageCompressor
import java.io.File

class GeneralSettings : AppCompatActivity(), OnEditListener,View.OnClickListener {

    private lateinit var addAccountImage: ImageView
    private lateinit var accountImage: CircularImageView
    private lateinit var backArrow: ImageView
    private lateinit var editUserName: TextView
    private lateinit var userName: TextView
    private lateinit var changeUserName: EditText
    private lateinit var editGender: TextView
    private lateinit var gender: TextView
    private lateinit var changeGender: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    private val REQUESTCODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_settings)

        mAuth = FirebaseAuth.getInstance()

        addAccountImage = findViewById(R.id.addProfilePhoto)
        accountImage = findViewById(R.id.accountImage)
        backArrow = findViewById(R.id.backArrowGeneralSettings)

        //settings findViewById
        editUserName = findViewById(R.id.Edit_userName)
        userName = findViewById(R.id.textView_userName)
        changeUserName = findViewById(R.id.EditText_userName)
        editGender = findViewById(R.id.Edit_gender)
        gender = findViewById(R.id.textView_gender)
        changeGender = findViewById(R.id.EditText_gender)


        addAccountImage.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUESTCODE)

        }
        backArrow.setOnClickListener(this)
        editUserName.setOnClickListener(this)
        editGender.setOnClickListener(this)

        loaduserData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESTCODE && data != null) {

            val uri = data.data

            val compressedImg = JpegImageCompressor.imageCompression(File(uri?.path))
            firebaseUser = mAuth.currentUser!!
            storageReference = FirebaseStorage.getInstance()
                .getReference("profileImages/" + firebaseUser.uid + ".jpg")

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
                        val accountImgUrl = uri.toString()
                        loadImgToDatabase(accountImgUrl)
                    }
                }
            }
        }
    }

    override fun performEdit(edit: TextView, textView: TextView, editText: EditText, type: String) {
        edit.text = "Done"
        val name = textView.text.toString()
        textView.visibility = View.GONE
        editText.setHint(name)
        editText.visibility = View.VISIBLE
        edit.setOnClickListener {
            editDetails(edit, textView, editText.text.toString(), type, editText)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            backArrow->{
                onBackPressed()
            }
            editUserName -> {
                performEdit(editUserName, userName, changeUserName, "username")
            }
            editGender -> {
                performEdit(editGender, gender, changeGender, "gender")
            }
        }
    }

    override fun editDetails(
        edit: TextView,
        textView: TextView,
        content: String,
        type: String,
        editText: EditText
    ) {
        firebaseUser = mAuth.currentUser!!

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val user = data.getValue(User::class.java)
                    if (user != null) {
                        if (user.id.equals(firebaseUser.uid)) {
                            val hashMap = HashMap<String, Any>()
                            hashMap.put(type, content)
                            data.ref.updateChildren(hashMap)
                            break
                        }
                    }
                }
                edit.text = "Edit"
                editText.visibility = View.GONE
                textView.visibility = View.VISIBLE
            }
        })
    }

    private fun loadImgToDatabase(imageUrl: String) {
        storageReference = FirebaseStorage.getInstance().getReference("profileImages/")
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val user = data.getValue(User::class.java)
                    if (user != null) {
                        if (user.id.equals(firebaseUser.uid)) {

                            val hashMap = HashMap<String, Any>()
                            hashMap.put("imageUrl", imageUrl)
                            data.ref.updateChildren(hashMap)
                            break
                        }
                    }
                }
            }
        })
    }

    private fun loaduserData() {
        firebaseUser = mAuth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val user = data.getValue(User::class.java)
                    if (user != null) {
                        if (user.id.equals(firebaseUser.uid)) {
                            userName.text = user.username
                            gender.text = user.gender
                            if (user.imageUrl.equals("null")) {
                                accountImage.setImageResource(R.drawable.new_group_icon)
                            } else {
                                Glide.with(this@GeneralSettings).load(user.imageUrl)
                                    .into(accountImage)
                            }
                            break
                        }
                    }
                }
            }

        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
