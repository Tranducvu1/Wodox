package com.wodox.domain.chat.model

import android.content.Context
import android.os.Parcelable
import com.wodox.chat.model.AdvanceToolType
import kotlinx.parcelize.Parcelize
import com.wodox.core.data.model.Selectable

@Parcelize
data class AdvanceTool(
    var type: AdvanceToolType = AdvanceToolType.OTHER,
    var bgIcon: String? = null,
    var suggestionCategory: String? = null,
    var isMostUse: Boolean = false,
    var order: Int = 0,
    override var isSelected: Boolean = false
) : Parcelable, Selectable {
    fun getInitialQuestion(context: Context): String? {
        return null
    }

    fun getName(context: Context): String {
        return type.getName(context)
    }
}