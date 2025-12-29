package com.wodox.domain.asset.model

import android.os.Parcelable
import com.wodox.core.data.model.Diffable
import com.wodox.core.data.model.Selectable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String,
    var name: String,
    var icon: String,
    override var isSelected: Boolean = false
) : Selectable, Parcelable, Diffable {
    override fun areContentsTheSame(data: Diffable, payload: String?): Boolean {
        return (data as Category).name == name && data.isSelected == isSelected
    }
}