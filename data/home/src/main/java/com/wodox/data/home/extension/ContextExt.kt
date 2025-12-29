package com.wodox.data.home.extension

import android.content.Context
import com.wodox.core.app.AbstractApplication
import com.wodox.data.common.datasource.AppSharePrefs
import com.wodox.data.common.datasource.AppSharePrefsImpl


val Context.appSharePrefs: AppSharePrefs
    get() {
        val app = applicationContext as AbstractApplication
        return AppSharePrefsImpl(
            app.sharePrefs
        )
    }

val Context.isPremium
    get() = (applicationContext as AbstractApplication).isPremium
