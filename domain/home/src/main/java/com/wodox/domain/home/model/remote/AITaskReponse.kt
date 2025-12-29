package com.wodox.domain.home.model.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class AITaskResponse(
    var title: String,
    var description: String,
) : Parcelable, Serializable {
}