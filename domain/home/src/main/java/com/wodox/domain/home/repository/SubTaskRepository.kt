package com.wodox.domain.home.repository

import androidx.paging.PagingData
import com.wodox.domain.home.model.local.SubTask
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface SubTaskRepository {

    fun getAllSubTaskByTaskID(id: UUID): Flow<List<SubTask>>

    suspend fun save(task: SubTask): SubTask?
}