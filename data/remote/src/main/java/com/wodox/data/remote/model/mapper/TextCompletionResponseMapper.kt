package com.wodox.data.remote.model.mapper

import com.wodox.data.common.base.AbstractDtoMapper
import com.wodox.data.remote.model.model.response.TextCompletionResponseDto
import com.wodox.domain.remote.model.response.TextCompletionResponse

class TextCompletionResponseMapper(val mapper: TextCompletionDataMapper) :
    AbstractDtoMapper<TextCompletionResponseDto, TextCompletionResponse>() {
    override fun mapToDomain(dto: TextCompletionResponseDto): TextCompletionResponse {
        return TextCompletionResponse(
            data = dto.data?.let { mapper.mapToDomain(it) },
            error = dto.error,
        )
    }

    override fun mapToDto(domain: TextCompletionResponse): TextCompletionResponseDto {
        return TextCompletionResponseDto(
            data = domain.data?.let { mapper.mapToDto(it) },
            error = domain.error,
        )
    }
}