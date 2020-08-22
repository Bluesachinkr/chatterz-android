package com.zone.chatterz.home

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zone.chatterz.camera.CameraChatterz
import com.zone.chatterz.R
import com.zone.chatterz.common.Timings
import com.zone.chatterz.services.PostUploadService
import java.io.*

class CreatePostActivity : AppCompatActivity(), View.OnClickListener {

    private val TAKE_PHOTO_GALLERY = 101
    private val PERMISSION_CODE = 200

    private lateinit var back_arrow_create_post: ImageView
    private lateinit var shareBtn_create_post: Button
    private lateinit var create_post_image: ImageView

    private lateinit var create_post_description: EditText

    private lateinit var camera_btn_create_post: ImageView

    private var selectedImage: Uri? = null
    private var resultPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val ig = intent
        ig?.let {
            val from = it.extras?.get("from")
            if(from!= null && from?.equals("main")){
                selectedImage = (it.extras?.get("output")) as Uri
                selectedImage?.let {
                    this.resultPath = it.path.toString()
                }
            }
        }

        this.back_arrow_create_post = findViewById(R.id.back_arrow_create_post)
        this.shareBtn_create_post = findViewById(R.id.shareBtn_create_post)
        this.create_post_image = findViewById(R.id.create_post_image)
        this.camera_btn_create_post = findViewById(R.id.camera_btn_create_post)

        this.create_post_description = findViewById(R.id.create_post_desciption)
        this.shareBtn_create_post.setOnClickListener(this)
        this.back_arrow_create_post.setOnClickListener(this)
        this.camera_btn_create_post.setOnClickListener(this)

        val permission = arrayOf<String>(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if ((ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED)
            && (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED)
        ) {
            ActivityCompat.requestPermissions(this, permission, PERMISSION_CODE)
        }

        selectedImage?.let {
            this.create_post_image.setImageURI(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    startActivity(Intent(this, MainActivity::class.java))
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
                this.resultPath = file.path
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sharePost() {
        val time = Timings.getCurrentTime()
        val description = create_post_description.text.toString()
        if (!resultPath.isEmpty() && !description.isEmpty()) {
            startPostUploadService(time, description)
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun startPostUploadService(time: String, description: String) {
        val intent = Intent(this, PostUploadService::class.java)
        intent.putExtra("time", time)
        intent.putExtra("description", description)
        intent.putExtra("resultPath", resultPath)
        startService(intent)
    }

    override fun onClick(v: View?) {
        when (v) {
            shareBtn_create_post -> {
                sharePost()
            }
            camera_btn_create_post -> {
                startActivity(Intent(this, CameraChatterz::class.java))
            }
          /*  camera_take_photo_gallery -> {
                val permission = arrayOf<String>(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                )
                if ((ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED)
                    && (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED)
                ) {
                    ActivityCompat.requestPermissions(this, permission, PERMISSION_CODE)
                }
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, TAKE_PHOTO_GALLERY)
            }*/
            back_arrow_create_post -> {
                finish()
            }
            else -> {
                return
            }
        }
    }
}