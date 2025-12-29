package com.starnest.data.asset.datasource.model

import android.annotation.SuppressLint
import com.wodox.core.data.model.Selectable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MessageTemplateDto(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("category")
    var category: String,
    @SerialName("name")
    var name: String,
    @SerialName("description")
    var description: String,
    override var isSelected: Boolean = false
) : Selectable, java.io.Serializable