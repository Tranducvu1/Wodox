package com.starnest.domain.asset.usecase

import com.wodox.domain.asset.model.Category
import com.wodox.domain.asset.model.MessageTemplate
import com.starnest.domain.asset.repository.AppRepository
import com.wodox.domain.common.base.BaseParamsFlowUnsafeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMessageTemplatesUseCase(
    private val appRepository: AppRepository
) : BaseParamsFlowUnsafeUseCase<Category?, List<MessageTemplate>>() {
    override suspend fun execute(params: Category?): Flow<List<MessageTemplate>> {
        return flow {
            val templates = appRepository.getMessageTemplates(params)

            emit(templates)
        }
    }
}