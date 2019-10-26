package com.zone.chatterz.Requirements

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

open  class JpegImageCompressor {

    companion object {

        private val compressedSize = 150f

        fun imageCompression(mContext : Context,originalImage: Bitmap): ByteArray {

            val width = originalImage.width
            val height = originalImage.height

            val matrix = Matrix()
            val scaleHeight = (compressedSize / height)
            val scaleWidth = (compressedSize  / width)
            matrix.postScale(scaleWidth, scaleHeight)
            matrix.postRotate(45F)

            var compressedImage =
                Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true)
            val outputStream = ByteArrayOutputStream()
            compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

            var byteArray = outputStream.toByteArray()
            return byteArray
        }

    }
}