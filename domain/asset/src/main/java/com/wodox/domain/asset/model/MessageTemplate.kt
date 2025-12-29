package com.wodox.domain.asset.model

import android.os.Parcelable
import com.wodox.core.data.model.Selectable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageTemplate(
    val id: Int = -1,
    var category: String,
    var name: String,
    var description: String,
    override var isSelected: Boolean = false
) : Selectable, Parcelable {
}