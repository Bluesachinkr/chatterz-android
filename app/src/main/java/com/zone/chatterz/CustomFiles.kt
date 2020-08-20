package com.zone.chatterz

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.lang.StringBuilder

class CustomFiles {

    companion object{
        val images = "IMAGES"
        val videos = "VIDEOS"
        fun createNewFile(mContext : Context,type : String,filename : String,extension : String) : File{
            var directory = createNewDirectory(mContext)
            val builder = StringBuilder(directory.toString())
            builder.append(File.separator)
            if(type.equals(images)){
                builder.append(images)
            }
            if(type.equals(videos)){
                builder.append(videos)
            }
            val subsfolder = File(builder.toString())
            while (!subsfolder.exists()){
                subsfolder.mkdirs()
            }
            builder.append(File.separator)
            builder.append(filename)
            builder.append(extension)
            if(ActivityCompat.checkSelfPermission(mContext,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ActivityCompat.checkSelfPermission(mContext,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(mContext as Activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE),101)
            }
            val file = File(builder.toString())
            if(!file.exists()){
                file.createNewFile()
            }
            return file
        }

        private fun createNewDirectory(mContext: Context): Any {
            val builder = StringBuilder("")
            builder.append(Environment.getExternalStorageDirectory())
            builder.append(File.separator)
            builder.append("Chatterz")
            val mediaDir= File(builder.toString())
            if(!mediaDir.exists()){
                if(!mediaDir.mkdirs()){
                    return ""
                }
            }
            return mediaDir.path
        }
    }
}
