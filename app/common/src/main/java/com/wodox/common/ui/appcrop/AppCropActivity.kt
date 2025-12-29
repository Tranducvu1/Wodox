package com.wodox.common.ui.appcrop

import android.net.Uri
import android.os.Parcelable
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.parcelable
import com.wodox.common.databinding.ActivityAppCropBinding
import com.wodox.common.model.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import com.wodox.common.R
import android.content.Intent
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.gone
import com.wodox.core.extension.toast

@Parcelize
data class CropData(
    val uri: Uri,
    val cropTitle : String? = null,
    val isCamera : Boolean = false,
) : Parcelable

@AndroidEntryPoint
class AppCropActivity:  BaseActivity<ActivityAppCropBinding, AppCropViewModel>(AppCropViewModel::class) {

   private val cropData by lazy {
       intent?.parcelable<CropData>(Constants.Intents.CROP_DATA)
   }

    override fun layoutId(): Int  = R.layout.activity_app_crop

    override fun initialize() {
        setupUI()
        setupAction()
    }

    private fun setupUI(){
        val cropData = cropData ?: return
        binding.toolbar.tvTitle.gone()
        binding.tvCrop.text = cropData.cropTitle ?: getString(com.wodox.resources.R.string.continue_title)
        binding.imageView.setImageUriAsync(cropData.uri)
    }
    private fun setupAction(){
        binding.apply {
            toolbar.backButton.debounceClick {
                finish()
            }
            llSolve.debounceClick {
                    solve()
            }
            llRotate.debounceClick {
                binding.imageView.rotateImage(90)
            }
            llRetake.debounceClick {
                setResult(
                    RESULT_CANCELED,
                    Intent().apply {
                        putExtra(Constants.Intents.IS_RETAKE, true)
                    }
                )
                finish()
            }
        }
    }

    override fun finish() {
        super.finish()
    }

    private fun solve() {
        val cropImageOptions = CropImageOptions()
        binding.imageView.setOnCropImageCompleteListener(object :
            CropImageView.OnCropImageCompleteListener {
            override fun onCropImageComplete(
                view: CropImageView,
                result: CropImageView.CropResult
            ) {
                if (result.isSuccessful) {
                    handleResult(result.uriContent)
                } else {
                    toast("cropping image failed")
                }
            }
        })
        binding.imageView.croppedImageAsync(
            saveCompressFormat = cropImageOptions.outputCompressFormat,
            saveCompressQuality = cropImageOptions.outputCompressQuality,
            reqWidth = cropImageOptions.outputRequestWidth,
            reqHeight = cropImageOptions.outputRequestHeight,
            options = cropImageOptions.outputRequestSizeOptions,
            customOutputUri = cropImageOptions.customOutputUri,
        )
    }

    private fun handleResult(uri: Uri?) {
        setResult(
            RESULT_OK,
            Intent().apply {
                putExtra(Constants.Intents.IMAGE_URI, uri)
                putExtra(Constants.Intents.IS_CAMERA, cropData?.isCamera)
            }
        )

        finish()
    }

}