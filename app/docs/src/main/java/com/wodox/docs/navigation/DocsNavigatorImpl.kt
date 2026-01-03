package com.wodox.docs.navigation


import android.content.Context
import android.os.Bundle

import com.wodox.common.navigation.DocNavigator
import com.wodox.core.extension.openActivity
import com.wodox.docs.model.Constants
import com.wodox.docs.ui.docdetail.DocsDetailActivity
import com.wodox.domain.docs.model.model.SharedDocument

class DocsNavigatorImpl : DocNavigator {
    override fun openDocs(context: Context) {
        return context.openActivity<DocsDetailActivity>()
    }

    override fun openDocsDetail(
        context: Context,
        documentId: String?
    ) {
        return context.openActivity<DocsDetailActivity>(
            Constants.Intents.SHARE_DOCUMENT to documentId
        )
    }
}