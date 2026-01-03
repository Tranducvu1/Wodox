package com.wodox.main.navigation

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.wodox.common.navigation.MainNavigator
import com.wodox.core.extension.openActivity
import com.wodox.core.extension.showAllowingStateLoss
import com.wodox.domain.main.model.Constants
import com.wodox.main.ui.main.MainActivity
import com.wodox.main.ui.main.addperson.AddPersonBottomSheet
import com.wodox.main.ui.main.aiassistant.AIBottomSheet
import com.wodox.main.ui.main.editprofile.EditProfileActivity
import com.wodox.main.ui.main.profile.ProfileBottomSheet

class MainNavigatorImpl : MainNavigator {
    override fun showMain(context: Context, isFirstLaunch: Boolean) {
        context.openActivity<MainActivity>(
            Constants.Intents.IS_FIRST_LAUNCH to isFirstLaunch
        )
    }

    override fun showAddPerson(fragmentManager: FragmentManager) {
        AddPersonBottomSheet.newInstance().apply {
            showAllowingStateLoss(fragmentManager, "")
        }
    }

    override fun showProfile(fragmentManager: FragmentManager) {
        ProfileBottomSheet.newInstance().apply {
            showAllowingStateLoss(fragmentManager, "")
        }
    }

    override fun openAIBottomSheet(fragmentManager: FragmentManager) {
        AIBottomSheet.newInstance().apply {
            showAllowingStateLoss(fragmentManager, "")
        }
    }

    override fun openEditActivity(context: Context) {
        context.openActivity<EditProfileActivity>()
    }

}
