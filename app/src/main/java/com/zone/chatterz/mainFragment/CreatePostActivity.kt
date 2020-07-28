package com.zone.chatterz.mainFragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.zone.chatterz.MainActivity
import com.zone.chatterz.R
import com.zone.chatterz.firebaseConnection.FirebaseMethods
import com.zone.chatterz.firebaseConnection.RequestCallback
import com.zone.chatterz.model.Post
import com.zone.chatterz.requirements.JpegImageCompressor
import com.zone.chatterz.requirements.Timings
import java.io.File
import java.nio.file.Files
import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class CreatePostActivity : AppCompatActivity(), View.OnClickListener {

    private val TAKE_PHOTO_GALLERY = 101
    private val PERMISSION_CODE = 200

    private lateinit var back_arrow_create_post: ImageView
    private lateinit var shareBtn_create_post: Button
    private lateinit var create_post_image: ImageView

    private lateinit var create_post_title_name: EditText
    private lateinit var create_post_hashtags: EditText

    private lateinit var camera_take_photo_gallery: ImageView

    private var selectedImage: Uri? = null
    private var resultPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        this.back_arrow_create_post = findViewById(R.id.back_arrow_create_post)
        this.shareBtn_create_post = findViewById(R.id.shareBtn_create_post)
        this.create_post_image = findViewById(R.id.create_post_image)
        this.camera_take_photo_gallery = findViewById(R.id.camera_take_photo_gallery)

        this.create_post_title_name = findViewById(R.id.create_post_title_name)
        this.create_post_hashtags = findViewById(R.id.create_post_hashtags)

        this.shareBtn_create_post.setOnClickListener(this)
        this.camera_take_photo_gallery.setOnClickListener(this)
        this.back_arrow_create_post.setOnClickListener(this)

        val permission = arrayOf<String>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if((ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            && (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)){
            ActivityCompat.requestPermissions(this,permission,PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivity(Intent(this,MainActivity::class.java))
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PHOTO_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                this.selectedImage = data?.data
                this.create_post_image.setImageURI(selectedImage)
                var str = selectedImage?.path
                str = str?.substring(4)
                val file = File(str)
                /*val path = StringBuffer(Environment.getExternalStorageDirectory().toString()+File.separator)
                path.append("Chatterz")
                path.append(File.separator)
                val mediaStorageDir = File(path.toString())//temp
                if (mediaStorageDir.isDirectory == false && !mediaStorageDir.exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Files.createDirectories(mediaStorageDir.toPath())
                    }else{
                        mediaStorageDir.mkdirs()
                    }
                }
                val timeStamp: String = SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.getDefault()
                ).format(Date())
                val mediaFile = File(
                    mediaStorageDir.path.toString() + File.separator
                            + "IMG" + timeStamp + ".jpeg"
                )
                mediaFile.createNewFile()
                file.copyTo(mediaFile)*/
                this.resultPath = file.path
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sharePost() {
        val time = Timings.getCurrentTime()
        val title = create_post_title_name.text.toString()
        val hashtags = create_post_hashtags.text.toString()
        if (!resultPath.isEmpty() && !title.isEmpty() && !hashtags.isEmpty()) {
            PostBackgroundTask().execute(time, title, hashtags, resultPath)
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            shareBtn_create_post -> {
                sharePost()
            }
            camera_take_photo_gallery -> {
                val permission = arrayOf<String>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                if((ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    && (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)){
                    ActivityCompat.requestPermissions(this,permission,PERMISSION_CODE)
                }
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, TAKE_PHOTO_GALLERY)
            }
            back_arrow_create_post -> {
                finish()
            }
            else -> {
                return
            }
        }
    }

    class PostBackgroundTask : AsyncTask<String, String,String>() {

        override fun doInBackground(vararg params: String?) : String {
            val time = params[0]
            val title = params[1]
            val hashtags = params[2]
            val path = params[3]
            val resultBytes = JpegImageCompressor.imageCompression(File(path))
            if (time != null && title != null && hashtags != null) {
                val hashMap = hashMapOf<String, Any>()
                hashMap.put("postOwner", com.zone.chatterz.firebaseConnection.Connection.user)
                hashMap.put("postTitle", title)
                hashMap.put("postHashTags", hashtags)
                hashMap.put("postTime", time)
                hashMap.put("postImage", "null");

                val databaseReference = FirebaseDatabase.getInstance().reference
                databaseReference.child(com.zone.chatterz.firebaseConnection.Connection.postRef)
                    .child(com.zone.chatterz.firebaseConnection.Connection.user)
                    .push()
                    .setValue(hashMap)

                FirebaseMethods.addValueEventChild(com.zone.chatterz.firebaseConnection.Connection.postRef,
                    object : RequestCallback() {
                        override fun onDataChanged(dataSnapshot: DataSnapshot) {
                            for (data in dataSnapshot.children) {
                                val post = data.getValue(Post::class.java)
                                if(post != null ){
                                    if (post.postTime.equals(time) && post.postTitle.equals(title)) {
                                        val postId: String = data.ref.key.toString()
                                        val storageRef = FirebaseStorage.getInstance()
                                            .getReference("/postImages" + postId + ".jpg")
                                        val uploadTask = storageRef.putBytes(resultBytes)

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
                                                }
                                            }
                                        }
                                        break
                                    }
                                }
                            }
                        }
                    })
            }
            val res = ""
            return res
        }
    }
}