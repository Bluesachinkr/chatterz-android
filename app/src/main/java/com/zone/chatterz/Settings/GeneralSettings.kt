package com.zone.chatterz.Settings

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.R
import com.zone.chatterz.Requirements.JpegImageCompressor

class GeneralSettings : AppCompatActivity() {

    private lateinit var addAccountImage : ImageView
    private lateinit var accountImage : CircularImageView
    private val REQUESTCODE_PHOTO = 200

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_settings)

        mAuth = FirebaseAuth.getInstance()

        addAccountImage = findViewById(R.id.addProfilePhoto)
        accountImage = findViewById(R.id.accountImage)

        addAccountImage.setOnClickListener {

            val intent =  Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,REQUESTCODE_PHOTO)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK){
            val targetUri = data!!.data
            val bitmap = BitmapFactory.decodeStream(targetUri?.let {
                contentResolver.openInputStream(it) })
            val compressedImg = JpegImageCompressor.imageCompression(bitmap)
            val uri = compressedImg.
            firebaseUser = mAuth.currentUser!!
            storageReference = FirebaseStorage.getInstance().getReference("ProfileImages")

            storageReference.putFile()

        }
    }
}
