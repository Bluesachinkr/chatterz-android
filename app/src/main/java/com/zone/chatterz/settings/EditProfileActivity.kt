package com.zone.chatterz.settings

import com.zone.chatterz.inferfaces.OnEditListener
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.iceteck.silicompressorr.SiliCompressor
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.MainActivity
import com.zone.chatterz.model.User
import com.zone.chatterz.R
import com.zone.chatterz.firebaseConnection.Connection
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.requirements.JpegImageCompressor
import java.io.ByteArrayOutputStream
import java.io.File

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var addAccountImage: TextView
    private lateinit var accountImage: CircularImageView
    private lateinit var backArrow: RelativeLayout
    private lateinit var editUserName: RelativeLayout
    private lateinit var edit_text_username: TextView
    private lateinit var userName: TextView
    private lateinit var changeUserName: EditText
    private lateinit var editGender: RelativeLayout
    private lateinit var edit_text_gender: TextView
    private lateinit var gender: TextView
    private lateinit var changeGender: EditText
    private lateinit var editBio: RelativeLayout
    private lateinit var edit_text_bio: TextView
    private lateinit var bio: TextView
    private lateinit var changeBio: EditText
    private lateinit var editdisplayName: RelativeLayout
    private lateinit var edit_text_display_name: TextView
    private lateinit var displayName: TextView
    private lateinit var changeDisplayName: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    private val REQUESTCODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        mAuth = FirebaseAuth.getInstance()

        addAccountImage = findViewById(R.id.addProfilePhoto)
        accountImage = findViewById(R.id.accountImage)
        backArrow = findViewById(R.id.backArrowGeneralSettings)

        //settings findViewById
        editUserName = findViewById(R.id.Edit_userName)
        editBio = findViewById(R.id.Edit_bio)
        editGender = findViewById(R.id.Edit_gender)
        editdisplayName = findViewById(R.id.Edit_displayName)
        editdisplayName.visibility = View.GONE

        edit_text_bio = findViewById(R.id.edit_text_bio)
        edit_text_display_name = findViewById(R.id.edit_text_display_name)
        edit_text_display_name.visibility = View.GONE
        edit_text_gender = findViewById(R.id.edit_text_gender)
        edit_text_username = findViewById(R.id.edit_text_username)

        userName = findViewById(R.id.textView_userName)
        gender = findViewById(R.id.textView_gender)
        displayName = findViewById(R.id.textView_displayName)
        bio = findViewById(R.id.textView_bio)

        changeUserName = findViewById(R.id.EditText_userName)
        changeGender = findViewById(R.id.EditText_gender)
        changeDisplayName = findViewById(R.id.EditText_displayName)
        changeBio = findViewById(R.id.EditText_bio)



        addAccountImage.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUESTCODE)

        }
        addAccountImage.setOnClickListener(this)
        backArrow.setOnClickListener(this)
        editUserName.setOnClickListener(this)
        editGender.setOnClickListener(this)
        editdisplayName.setOnClickListener(this)
        editBio.setOnClickListener(this)

        loaduserData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUESTCODE && data != null) {

            val uri = data.data
            val resultPath = uri.toString()
            val compressedImg = SiliCompressor.with(this).getCompressBitmap(File(resultPath).toString())
            val stream = ByteArrayOutputStream()
            compressedImg.compress(Bitmap.CompressFormat.PNG,100,stream)
            val array = stream.toByteArray()
            storageReference = FirebaseStorage.getInstance()
                .getReference("profileImages/" + Connection.user + ".jpg")

            val uploadTask = storageReference.putBytes(array)

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

    fun performEdit(
        edit: RelativeLayout,
        edit_text: TextView,
        textView: TextView,
        editText: EditText,
        type: String
    ) {
        edit_text.text = "Done"
        val name = textView.text.toString()
        textView.visibility = View.GONE
        editText.setHint(name)
        editText.visibility = View.VISIBLE
        edit.setOnClickListener {
            editDetails(edit_text, textView, editText.text.toString(), type, editText)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            backArrow -> {
                onBackPressed()
            }
            addAccountImage -> {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUESTCODE)
            }
            editUserName -> {
                performEdit(editUserName, edit_text_username, userName, changeUserName, "username")
            }
            editGender -> {
                performEdit(editGender, edit_text_gender, gender, changeGender, "gender")
            }
          /*  editdisplayName -> {
                performEdit(
                    editdisplayName,
                    edit_text_display_name,
                    displayName,
                    changeDisplayName,
                    "displayName"
                )
            }*/
            editBio -> {
                performEdit(editBio, edit_text_bio, bio, changeBio, "bio")
            }
        }
    }

    fun editDetails(
        edit_text: TextView,
        textView: TextView,
        content: String,
        type: String,
        editText: EditText
    ) {
        FirebaseMethods.singleValueEvent(Connection.userRef + File.separator + Connection.user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {
                        val hashMap = HashMap<String, Any>()
                        hashMap.put(type, content)
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                    edit_text.text = "Edit"
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
        FirebaseMethods.singleValueEvent(Connection.userRef + File.separator + Connection.user,
            object : RequestCallback() {
                override fun onDataChanged(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {
                        userName.text = it.username
                        gender.text = it.gender
                        if (user.imageUrl.equals("null")) {
                            accountImage.setImageResource(R.drawable.new_group_icon)
                        } else {
                            Glide.with(this@EditProfileActivity).load(user.imageUrl)
                                .into(accountImage)
                        }
                        displayName.text = it.displayName
                    }
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
