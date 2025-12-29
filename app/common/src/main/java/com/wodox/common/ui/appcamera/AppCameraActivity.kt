package com.wodox.common.ui.appcamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.wodox.common.databinding.ActivityAppCameraBinding
import com.wodox.common.R
import com.wodox.common.model.CameraActionType
import java.util.concurrent.ExecutorService
import android.provider.Settings
import android.util.Rational
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.ViewPort
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.wodox.common.extension.saveCompressBitmap
import com.wodox.common.model.Constants
import com.wodox.common.ui.appcrop.AppCropActivity
import com.wodox.common.ui.appcrop.CropData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import android.view.Surface
import androidx.camera.core.UseCaseGroup
import com.wodox.common.extension.showDefaultDialog
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.imageDir
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppCameraActivity : BaseActivity<ActivityAppCameraBinding, AppCameraViewModel>(
    AppCameraViewModel::class) {

    override fun layoutId(): Int = R.layout.activity_app_camera
    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null
    private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var cropLauncher: ActivityResultLauncher<Intent>? = null
    private var settingLauncher: ActivityResultLauncher<Intent>? = null
    private var actionType: CameraActionType = CameraActionType.ACTION_START_CAMERA

    override fun initialize() {
        binding.lifecycleOwner = this
        settingLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (!allPermissionsGranted()) {
                showDialogMessage(isOpenSetting = true)
            } else {
                startCamera()
            }
        }
        setupUI()
        setupAction()
    }

    private fun setupUI() {
        binding.lifecycleOwner = this
    }

    @SuppressLint("RestrictedApi")
    private fun setupAction() {
        binding.apply {
            ivTake.debounceClick {
                checkPermission(
                    actionType = CameraActionType.ACTION_TAKE_PICTURE,
                    action = ::takePhoto
                )
            }
            ivFlash.debounceClick {
                checkPermission(
                    actionType = CameraActionType.ACTION_FLASH,
                    action = ::turnOnFlash
                )
            }
            ivGallery.debounceClick {
                pickMedia?.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
            toolbar.backButton.debounceClick {
                finish()
            }
        }
    }


    @SuppressLint("RestrictedApi")
    private fun turnOnFlash() {
        val currentState = !(viewModel.isFlashTurnOn.value ?: false)
        imageCapture?.camera?.cameraControl?.enableTorch(currentState)
        viewModel.isFlashTurnOn.postValue(currentState)
    }

    private fun checkPermission(
        actionType: CameraActionType,
        action: () -> Unit
    ) {
        this.actionType = actionType
        if (allPermissionsGranted()) {
            action()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        when {
            allPermissionsGranted() -> {
                startCamera()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture?.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture?.get() ?: return@addListener
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.preview.surfaceProvider
                }
            imageCapture = ImageCapture.Builder().build()
            val currentImageCapture = imageCapture ?: return@addListener
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val viewPort = ViewPort.Builder(
                Rational(
                    binding.preview.width,
                    binding.preview.height
                ), binding.preview.display?.rotation ?: Surface.ROTATION_0
            ).build()
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(currentImageCapture)
                .setViewPort(viewPort)
                .build()
            try {
                // Unbind use cases before rebinding
                cameraProvider?.unbindAll()
                // Bind use cases to camera
                cameraProvider?.bindToLifecycle(
                    this, cameraSelector, useCaseGroup
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        viewModel.isCapturing.postValue(true)
        val fileName = "${System.currentTimeMillis()}.png"
        val fileDir = File(imageDir, fileName)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(fileDir).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    exc.printStackTrace()
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    handleOnImageSaved(output, fileDir)
                }
            }
        )
    }

    private fun handleOnImageSaved(output: ImageCapture.OutputFileResults, file: File) {
        lifecycleScope.launch(Dispatchers.IO) {
            val cropBounder = Rect(0, 0, binding.preview.width, binding.preview.height)

            val savedUri = output.savedUri ?: return@launch

            val bitmap = getCorrectlyOrientedBitmapFromUri(
                this@AppCameraActivity,
                savedUri
            ) ?: return@launch

            val scale = bitmap.width.toFloat() / binding.preview.width

            val scaledLeft = (cropBounder.left * scale).toInt()
            val scaledTop = (cropBounder.top * scale).toInt()
            val scaledWidth =
                minOf((cropBounder.width() * scale).toInt(), bitmap.width - scaledLeft)
            val scaledHeight =
                minOf((cropBounder.height() * scale).toInt(), bitmap.height - scaledTop)

            val croppedBitmap = Bitmap.createBitmap(
                bitmap,
                scaledLeft,
                scaledTop,
                scaledWidth,
                scaledHeight,
            )
            viewModel.resultUri = croppedBitmap.saveCompressBitmap(
                this@AppCameraActivity,
                file,
                quality = 100
            ).toUri()
            withContext(Dispatchers.Main) {
                handleResult(viewModel.resultUri, true)
            }
        }
    }


    private fun handleResult(
        selectedImageUri: Uri?,
        isCamera: Boolean
    ) {
        if (selectedImageUri == null) return
        viewModel.isCapturing.postValue(false)
        val intent = Intent(this, AppCropActivity::class.java).apply {
            putExtra(
                Constants.Intents.CROP_DATA, CropData(
                    uri = selectedImageUri,
                    cropTitle = getString(com.wodox.resources.R.string.crop),
                    isCamera = isCamera
                )
            )
        }
        cropLauncher?.launch(intent)
    }

    fun getCorrectlyOrientedBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val exifInputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
        val exif = ExifInterface(exifInputStream)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        exifInputStream.close()
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1.0f, -1.0f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showDialogMessage(isOpenSetting: Boolean = false) {
        showDefaultDialog(
            supportFragmentManager,
            title = getString(com.wodox.resources.R.string.camera_access_is_denied),
            message = getString(com.wodox.resources.R.string.please_enable_camera_permission_in_settings_to_continue),
            positiveTitle = getString(com.wodox.resources.R.string.permission),
            positiveCallback = {
                if (isOpenSetting) {
                    openSetting()
                } else {
                    activityResultLauncher?.launch(REQUIRED_PERMISSIONS)
                }
            },
            negativeTitle = getString(com.wodox.core.R.string.cancel),
            negativeCallback = {
            }
        )
    }


    override fun onDestroy() {
        cameraProvider?.unbindAll()
        cameraExecutor?.shutdown()
        cameraExecutor = null
        pickMedia = null
        activityResultLauncher = null
        cropLauncher = null
        super.onDestroy()

    }

    private fun openSetting() {
        val intent = Intent().apply {
            setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            setData(Uri.fromParts("package", packageName, null))
        }
        settingLauncher?.launch(intent)
    }

    companion object {
        private var REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA,
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}