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

        private val compressedSize = 150

        fun imageCompression(mContext : Context,originalImage: Bitmap): Uri {
            val bitmap = BitmapFactory.decodeFile(originalImage.toString())
            val width = bitmap.width
            val height = bitmap.height

            val matrix = Matrix()
            val scaleHeight = (compressedSize as Float) / height as Float
            val scaleWidth = (compressedSize as Float) / width as Float
            matrix.postScale(scaleWidth, scaleHeight)
            matrix.postRotate(45F)

            var compressedImage =
                Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true)
            val outputStream = ByteArrayOutputStream()
            compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

            val path = MediaStore.Images.Media.insertImage(mContext,compressedImage,"ProfileImage",null)
            return compressedImage

        }

    }
}