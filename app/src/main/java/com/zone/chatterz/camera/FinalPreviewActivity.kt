package com.zone.chatterz.camera

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.zone.chatterz.R
import com.zone.chatterz.home.CreatePostActivity
import java.io.File


class FinalPreviewActivity() :
    AppCompatActivity() {

    private lateinit var image_final_preview_btn: Button
    private lateinit var image_final_preview: ImageView

    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_final_preview)

        val ig = intent
        ig?.let {
            file = it.extras?.get("file") as File
        }
        image_final_preview_btn = findViewById(R.id.image_final_preview_btn)
        image_final_preview = findViewById(R.id.image_final_preview)

        image_final_preview_btn.setOnClickListener {
            val intent = Intent(this,CreatePostActivity::class.java)
            intent.putExtra("from","camera")
            intent.putExtra("output", Uri.fromFile(file).path)
            startActivity(intent)
        }

        image_final_preview.setImageBitmap(getBitmapFromFile(file))
    }

    private fun getBitmapFromFile(file: File): Bitmap? {
        val path = file.path
        val bitmap = BitmapFactory.decodeFile(path)
        return bitmap
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}