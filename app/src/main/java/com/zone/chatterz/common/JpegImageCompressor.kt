package com.zone.chatterz.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

open class JpegImageCompressor {

    companion object {

        fun imageCompression(imageFile: File): ByteArray {
            var compressedFile: Bitmap? = null
            var byteArray: ByteArray? = null

            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            var inputStream: FileInputStream?
            try {
                inputStream = FileInputStream(imageFile)
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val image_max_size = 1024
            var scale = 1
            if (options.outHeight > image_max_size || options.outWidth > image_max_size) {
                scale = Math.pow(
                    2.0, Math.ceil(
                        Math.log(
                            (image_max_size /
                                    Math.max(options.outHeight, options.outWidth)).toDouble()
                        ) / Math.log(0.5)
                    )
                ).toInt()
            }

            //decode with sample size
            val optionsTwo = BitmapFactory.Options()
            optionsTwo.inSampleSize = scale
            try {
                val inputStreamTwo = FileInputStream(imageFile)
                compressedFile = BitmapFactory.decodeStream(inputStreamTwo, null, optionsTwo)
                inputStreamTwo.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val path = StringBuffer(Environment.getExternalStorageDirectory().toString()+File.separator)
            path.append("Chatterz")
            val mediaStorageDir = File(path.toString())//temp
            if (mediaStorageDir.isDirectory == false && !mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }
            val timeStamp: String = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val mediaFile = File(
                mediaStorageDir.getPath().toString() + File.separator
                        + "IMG" + timeStamp + ".mp4"
            )
            try {
                val outputStream = FileOutputStream(mediaFile)
                compressedFile?.let {
                    it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                val bitStream = ByteArrayOutputStream()
                compressedFile?.let {
                    it.compress(Bitmap.CompressFormat.PNG, 100, bitStream)
                }
                byteArray = bitStream.toByteArray()
                outputStream.flush()
                outputStream.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            return byteArray!!
        }
    }
}