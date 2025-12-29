package com.wodox.data.remote.model.mapper

import com.wodox.data.common.base.AbstractDtoMapper
import com.wodox.data.remote.model.model.response.TextCompletionChoiceDto
import com.starnest.domain.remote.model.response.TextCompletionChoice

class TextCompletionChoiceMapper(val textCompletionMessageResponseMapper: TextCompletionMessageResponseMapper) :
    AbstractDtoMapper<TextCompletionChoiceDto, TextCompletionChoice>() {

    override fun mapToDomain(dto: TextCompletionChoiceDto): TextCompletionChoice {
        return TextCompletionChoice(
            message = dto.message?.let {
                textCompletionMessageResponseMapper.mapToDomain(it)
            },
            delta = dto.delta?.let {
                textCompletionMessageResponseMapper.mapToDomain(it)
            },
        )
    }


    override fun mapToDto(domain: TextCompletionChoice): TextCompletionChoiceDto {
        return TextCompletionChoiceDto(
            delta = domain.delta?.let {
                textCompletionMessageResponseMapper.mapToDto(it)
            },
            message = domain.message?.let {
                textCompletionMessageResponseMapper.mapToDto(it)
            }
        )
    }
}