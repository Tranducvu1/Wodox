package com.wodox.data.common.datasource

import com.wodox.core.data.model.SharePrefs


interface AppSharePrefs: SharePrefs {
    var hadASuccessfulKeyboardInstallation: Boolean
    var isChatSuggestion: Boolean
    var isEnableHomeSendAnimation: Boolean
}