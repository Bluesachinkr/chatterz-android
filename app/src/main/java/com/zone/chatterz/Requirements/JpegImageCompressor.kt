package com.zone.chatterz.Requirements

import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.ByteArrayOutputStream

open  class JpegImageCompressor {

    companion object {

        private val compressedSize = 300f

        fun imageCompression(originalImage: Bitmap): ByteArray {

            val width = originalImage.width
            val height = originalImage.height

            val matrix = Matrix()
            val scaleHeight = (compressedSize / height)
            val scaleWidth = (compressedSize  / width)
            matrix.postScale(scaleWidth, scaleHeight)

            var compressedImage =
                Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true)
            val outputStream = ByteArrayOutputStream()
            compressedImage.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)

            var byteArray = outputStream.toByteArray()

            return byteArray
        }
    }
}