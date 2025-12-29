package com.wodox.intro.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.wodox.data.common.datasource.AppSharePrefs
import com.wodox.common.navigation.AuthNavigator
import com.wodox.common.navigation.MainNavigator
import com.wodox.core.base.activity.BaseFullScreenActivity
import com.wodox.intro.R
import com.wodox.intro.databinding.ActivitySplashBinding
import com.wodox.intro.model.Constants
import com.wodox.intro.ui.intro.IntroActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity :
    BaseFullScreenActivity<ActivitySplashBinding, SlashViewModel>(SlashViewModel::class) {
    override val contentView: View
        get() = binding.ctContainer

    @Inject
    lateinit var appSharePrefs: AppSharePrefs

    private var timer: CountDownTimer? = null

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var authNavigator: AuthNavigator


    private var isAdShown: Boolean = false

    private val isFirstLaunch by lazy {
        intent.getBooleanExtra(
            Constants.Intents.IS_FIRST_LAUNCH,
            true
        )
    }

    override fun layoutId(): Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initialize() {
        setupUI()
    }

    private fun setupUI() {
        binding.tvTitle.show()
        binding.progressBar.show()
        binding.progressBar.min = 0
        binding.progressBar.max = getWaitingTime()
        checkToShowAdOrMain()
    }

    private fun checkToShowAdOrMain() {
        timer = object : CountDownTimer(getWaitingTime().toLong(), 10) {
            override fun onFinish() {
                binding.progressBar.progress = getWaitingTime()
                if (!isAdShown) {
                    goToMainOrIntro()
                }
                timer?.cancel()
            }

            override fun onTick(millisUntilFinished: Long) {
                binding.progressBar.progress = getWaitingTime() - millisUntilFinished.toInt()
                if (getWaitingTime() - millisUntilFinished.toInt() > 2000) {
                    timer?.cancel()
                    timer = null
                    goToMainOrIntro()
                } else {
                    if (getWaitingTime() - millisUntilFinished.toInt() > 2000) {
                        if (isFirstLaunch) {
                            goToMainOrIntro()
                        } else {
                            binding.progressBar.progress = binding.progressBar.max
                        }

                    }
                }
            }
        }.start()
    }

    private fun goToMainOrIntro() {
        if (appSharePrefs.isFirstOpen) {
            openActivity<IntroActivity>()
        } else {
            mainNavigator.showMain(this, false)
        }
        finish()
    }

    fun getWaitingTime(): Int {
        return if (isFirstLaunch) {
            WAITING_TIME
        } else {
            WAITING_TIME_MOVE_TO_FOREGROUND
        }
    }

    override fun onDestroy() {
        timer?.cancel()
        timer = null
        super.onDestroy()
    }

    companion object {
        const val WAITING_TIME = 10000
        const val WAITING_TIME_MOVE_TO_FOREGROUND = 5000
        const val WAITING_TIME_FIRST_INSTALL = 1500
    }
}