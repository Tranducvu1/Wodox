package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
@Parcelize
data class ScheduleModel(
    @SerialName("id")
    val id: Int,
    @Contextual
    @SerialName("triggerDate")
    val triggerDate: Calendar,
    @SerialName("isRepeat")
    val isRepeat: Boolean = false
) : java.io.Serializable, Parcelable