package com.wodox.common.navigation


import android.content.Context

interface DocNavigator {
    fun openDocs(context: Context)

    fun openDocsDetail(context: Context, documentId: String? = null)

}