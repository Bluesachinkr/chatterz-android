package com.zone.chatterz.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.common.util.concurrent.ListenableFuture
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.common.CustomFiles
import com.zone.chatterz.R
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment(
    mContext: Context,
    windowManager: WindowManager
) : Fragment(),
    View.OnClickListener {

    private val mContext = mContext
    private val windowManager = windowManager
    private val permissionRequestCode = 101

    private lateinit var camera_play_pause_btn: CircularImageView

    private var lens_facing = CameraSelector.LENS_FACING_BACK
    private lateinit var imagePreview: Preview
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var outputDirectory: File
    private lateinit var imageCapture: ImageCapture


    private lateinit var imageAnalysis: ImageAnalysis
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var mCameraView: PreviewView

    val permission = arrayOf<String>(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        camera_play_pause_btn = view.findViewById(R.id.camera_play_pause_btn)
        mCameraView = view.findViewById(R.id.mCameraView)

        camera_play_pause_btn.setOnClickListener(this)

        cameraProviderFuture = ProcessCameraProvider.getInstance(mContext)

        if (allPermissionGranted()) {
            mCameraView.post {
                startCamera()
            }
        }

        outputDirectory = getOutputDirectory(mContext)

        return view
    }

    private fun allPermissionGranted(): Boolean {
        for (p in permission) {
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    p
                ) == PackageManager.PERMISSION_DENIED
            ) {
                return false
            }
        }
        return true
    }

    private fun startCamera() {
        try {
            imageAnalysis = ImageAnalysis.Builder().apply {
                setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            }.build()
            imageAnalysis.setAnalyzer(executor, LuminosityAnalyzer())

            imagePreview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
                setTargetRotation(mCameraView.display.rotation)
            }.build()

            //setImage Capture
            imageCapture = ImageCapture.Builder().apply {
                this.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                setTargetResolution(Size(resources.displayMetrics.widthPixels,resources.displayMetrics.heightPixels))
            }.build()

            val selector = CameraSelector.Builder().requireLensFacing(lens_facing).build()

            cameraProviderFuture.addListener(Runnable {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.bindToLifecycle(this, selector, imagePreview,imageCapture,imageAnalysis)

                mCameraView.preferredImplementationMode =
                    PreviewView.ImplementationMode.TEXTURE_VIEW
                imagePreview.setSurfaceProvider(mCameraView.createSurfaceProvider())
            }, ContextCompat.getMainExecutor(mContext))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takePicture() {
        val fileName = SimpleDateFormat(FILENAME, Locale.US)
            .format(System.currentTimeMillis())
        var file =
            CustomFiles.createNewFile(mContext, CustomFiles.images, fileName, PHOTO_EXTENSION)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${file.absolutePath}"
                    mCameraView.post {
                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
                    }
                    val intent = Intent(mContext,FinalPreviewActivity::class.java)
                    intent.putExtra("file",file)
                    mContext.startActivity(intent)
                }

                override fun onError(exception: ImageCaptureException) {
                    mCameraView.post {
                        Toast.makeText(mContext, exception.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    override fun onClick(v: View?) {
        when (v) {
            camera_play_pause_btn -> {
                takePicture()
            }
            else -> {
                return
            }
        }
    }

    companion object {
        private const val TAG = "Camera Fragment"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists()) mediaDir else appContext.filesDir
        }

        fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )
    }

    class LuminosityAnalyzer() : ImageAnalysis.Analyzer {

        private var lastAnalyzedTimestmp = 0L

        private fun ByteBuffer.toByTeArray(): ByteArray {
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }

        override fun analyze(image: ImageProxy) {
            val currentTimestamp = System.currentTimeMillis()
            if (currentTimestamp - lastAnalyzedTimestmp >=
                TimeUnit.SECONDS.toMillis(1)
            ) {
                val buffer = image.planes[0].buffer
                val data = buffer.toByTeArray()
                val pixels = data.map { it.toInt() and 0xFF }
                val luma = pixels.average()

                Log.d("CameraXApp", "Average luminosity: $luma")

                lastAnalyzedTimestmp = currentTimestamp
            }
            image.close()
        }
    }
}

