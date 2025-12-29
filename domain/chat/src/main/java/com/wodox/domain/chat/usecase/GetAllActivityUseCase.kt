package com.wodox.domain.chat.usecase

import com.wodox.domain.chat.model.local.ActivityItem
import com.wodox.domain.base.BaseNoParamsUnsafeUseCase
import com.wodox.resources.R
class GetAllActivityUseCase() : BaseNoParamsUnsafeUseCase<List<ActivityItem>>() {
    override suspend fun execute(): List<ActivityItem> {
        return listOf(
            ActivityItem(
                name = "Activity",
                description = "Mô tả cho Activity",
                icon = R.drawable.ic_activity_home
            ),
            ActivityItem(
                name = "Comment",
                description = "Mô tả cho comment",
                icon = R.drawable.ic_comment
            ),
            ActivityItem(
                name = "Replay",
                description = "Mô tả cho reply",
                icon = R.drawable.ic_reply
            ),
            ActivityItem(
                name = "Tag",
                description = "Mô tả cho tag",
                icon = R.drawable.ic_tag
            )
        )
    }
}
