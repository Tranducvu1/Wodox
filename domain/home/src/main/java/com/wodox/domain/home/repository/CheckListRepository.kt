package com.wodox.domain.home.repository

import com.wodox.domain.home.model.local.CheckList
import com.wodox.domain.home.model.local.SubTask
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface CheckListRepository {
    fun getAllCheckListByTaskID(id: String): Flow<List<CheckList>>

    suspend fun save(checkList: CheckList): CheckList?
}