package com.wodox.core.data.model
interface Diffable {
    fun areContentsTheSame(data: Diffable, payload: String? = null): Boolean
}