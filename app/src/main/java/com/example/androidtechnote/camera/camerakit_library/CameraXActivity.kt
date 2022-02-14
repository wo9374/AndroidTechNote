package com.example.androidtechnote.camera.camerakit_library

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityCameraxBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXActivity : AppCompatActivity() {
    lateinit var binding : ActivityCameraxBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraAnimationListener : Animation.AnimationListener

    private var useCameraBool = true

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val FILENAME_FORMAT_KO = "yyyy년 MM월 dd일 E요일 HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camerax)

        if (allPermissionsGranted()) { //카메라 권한요청
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.captureBtn.setOnClickListener {
            takePhoto()
        }

        binding.flipCameraBtn.setOnClickListener {
            useCameraBool = !useCameraBool
            startCamera(useCameraBool)
        }

        binding.flipCameraBtn
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera(useCam: Boolean = true) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get() // 카메라 라이프사이클을 바인딩하는데 사용
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA  //후방 카메라 기본값으로 선택

            if (!useCam) cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()  // 카메라 시작 전 바인딩해제
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture) // 카메라에 바인딩
            } catch(exc: Exception) {
                Log.e(TAG, "카메라 바인딩 실패", exc)
            }
        }, ContextCompat.getMainExecutor(this))

        //사진촬영시 애니메이션 리스너 init
        cameraAnimationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.shutterAnimView.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // 이미지를 보관할 타임스탬프 출력 파일 만들기
        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT_KO, Locale.KOREAN).format(System.currentTimeMillis()) + ".jpg")
        val photoFile2 = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT_KO, Locale.US).format(System.currentTimeMillis()) + ".jpg")

        Log.d("takePhoto", ": $photoFile")

        // 파일 + 메타데이터가 포함된 출력 옵션 만들기
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // 사진 촬영 후 트리거되는 이미지 캡처 Callback 설정
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "사진촬영 실패: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val msg = "사진촬영 성공: $savedUri"
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)

                val animation = AnimationUtils.loadAnimation(this@CameraXActivity, R.anim.camera_shutter)
                animation.setAnimationListener(cameraAnimationListener)

                binding.shutterAnimView.animation = animation
                binding.shutterAnimView.visibility = View.VISIBLE
                binding.shutterAnimView.startAnimation(animation)
            }
        })
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "권한 미허용", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}