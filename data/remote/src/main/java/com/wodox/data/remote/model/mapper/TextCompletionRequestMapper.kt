package com.wodox.data.remote.model.mapper

import com.wodox.data.common.base.AbstractDtoMapper
import com.wodox.data.remote.model.model.request.TextCompletionRequestDto
import com.starnest.domain.remote.model.request.TextCompletionRequest
import com.wodox.core.extension.toArrayList

class TextCompletionRequestMapper(
    val messageMapper: TextCompletionMessageMapper,
    val responseFormatMapper: ResponseFormatMapper
) :
    AbstractDtoMapper<TextCompletionRequestDto, TextCompletionRequest>() {

    override fun mapToDomain(dto: TextCompletionRequestDto): TextCompletionRequest {
        return TextCompletionRequest(
            messages = dto.messages?.let { messageMapper.mapToDomainList(it) }?.toArrayList(),
            responseFormat = dto.responseFormat?.let { responseFormatMapper.mapToDomain(it) },
            expertId = dto.expertId,
            advanceToolType = dto.advanceToolType,
            suggestionId = dto.suggestionId,
            assistantId = dto.assistantId,
            isVip = dto.isVip,
            maxTokens = dto.maxTokens
        )
    }


    override fun mapToDto(domain: TextCompletionRequest): TextCompletionRequestDto {
        return TextCompletionRequestDto(
            messages = domain.messages?.let { messageMapper.mapToDtoList(it) }?.toArrayList(),
            responseFormat = domain.responseFormat?.let { responseFormatMapper.mapToDto(it) },
            expertId = domain.expertId,
            advanceToolType = domain.advanceToolType,
            suggestionId = domain.suggestionId,
            assistantId = domain.assistantId,
            isVip = domain.isVip,
            maxTokens = domain.maxTokens
        )
    }
}