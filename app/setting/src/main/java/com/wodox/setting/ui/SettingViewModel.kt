package com.wodox.ui.setting

import android.app.Application
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.setting.R
import com.wodox.setting.model.SettingItem
import com.wodox.setting.model.SettingItemType
import com.wodox.setting.model.SettingSection
import com.wodox.setting.ui.SettingUiAction
import com.wodox.setting.ui.SettingUiEvent
import com.wodox.setting.ui.SettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    val app: Application
) : BaseUiStateViewModel<SettingUiState, SettingUiEvent, SettingUiAction>(app) {
    override fun handleAction(action: SettingUiAction) {
        when (action) {
            is SettingUiAction.LoadSettings -> loadSettings()
            is SettingUiAction.ClaimPromotion -> handleClaimPromotion()
        }
    }

    override fun initialState(): SettingUiState = SettingUiState()

    private fun loadSettings() {
        val settingItems = listOf(
            SettingSection(
                title = "Chung",
                items = listOf(
                    SettingItem(
                        icon = R.drawable.ic_notification,
                        title = "Thông báo",
                        type = SettingItemType.NOTIFICATION
                    ),
                    SettingItem(
                        icon = R.drawable.ic_password,
                        title = "Mật mã",
                        type = SettingItemType.PASSWORD
                    ),
                    SettingItem(
                        icon = R.drawable.ic_language,
                        title = "Ngôn ngữ",
                        type = SettingItemType.LANGUAGE
                    )
                )
            ),
            // Hỗ trợ & phản hồi section
            SettingSection(
                title = "Hỗ trợ & phản hồi",
                items = listOf(
                    SettingItem(
                        icon = R.drawable.ic_rate,
                        title = "Rate us",
                        type = SettingItemType.RATE_US
                    ),
                    SettingItem(
                        icon = R.drawable.ic_apps,
                        title = "Khám phá thêm ứng dụng",
                        type = SettingItemType.EXPLORE_APPS,
                        badge = "Ứng dụng tốt nhất của tôi"
                    ),
                    SettingItem(
                        icon = R.drawable.ic_privacy,
                        title = "Quyền riêng tư",
                        type = SettingItemType.PRIVACY
                    )
                )
            )
        )

        updateState { it.copy(settingItems = settingItems) }
    }

    private fun handleClaimPromotion() {
        sendEvent(SettingUiEvent.ShowPromotionDialog)
    }
}