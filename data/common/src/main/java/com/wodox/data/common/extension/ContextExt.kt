package com.starnest.data.common.extension

import android.content.Context
import com.wodox.core.app.AbstractApplication

val Context.isPremium
    get() = (applicationContext as AbstractApplication).isPremium
