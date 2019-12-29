package com.zone.chatterz.Requirements

import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.ByteArrayOutputStream

open class JpegImageCompressor {

    companion object {

        fun imageCompression(originalImage: Bitmap): ByteArray {

            val width = originalImage.width
            val height = originalImage.height
            val matrix = Matrix()

            matrix.postScale(width as Float,height as Float)
            var compressedImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true)
            val outputStream = ByteArrayOutputStream()
            compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            var byteArray = outputStream.toByteArray()
            return byteArray
        }
    }
}