package com.wodox.data.remote.model.mapper

import com.wodox.data.common.base.AbstractDtoMapper
import com.wodox.data.remote.model.model.response.TextCompletionMessageResponseDto
import com.starnest.domain.remote.model.response.TextCompletionMessageResponse

class TextCompletionMessageResponseMapper :
    AbstractDtoMapper<TextCompletionMessageResponseDto, TextCompletionMessageResponse>() {
    override fun mapToDomain(dto: TextCompletionMessageResponseDto): TextCompletionMessageResponse {
        return TextCompletionMessageResponse(
            role = dto.role,
            content = dto.content,
            name = dto.name
        )
    }


    override fun mapToDto(domain: TextCompletionMessageResponse): TextCompletionMessageResponseDto {
        return TextCompletionMessageResponseDto(
            role = domain.role,
            content = domain.content,
            name = domain.name
        )
    }
}