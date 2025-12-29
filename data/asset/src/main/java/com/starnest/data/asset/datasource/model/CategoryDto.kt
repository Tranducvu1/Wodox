package com.starnest.data.asset.datasource.model

import android.annotation.SuppressLint
import com.wodox.core.data.model.Selectable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CategoryDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    var name: String = "",
    @SerialName("icon")
    var icon: String = "",
    override var isSelected: Boolean = false
) : Selectable, java.io.Serializable