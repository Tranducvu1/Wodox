package com.wodox.data.remote.model.mapper

import com.wodox.data.common.base.AbstractDtoMapper
import com.wodox.data.remote.model.model.request.TextCompletionMessageDto
import com.starnest.domain.remote.model.request.TextCompletionMessage

class TextCompletionMessageMapper :
    AbstractDtoMapper<TextCompletionMessageDto, TextCompletionMessage>() {
    override fun mapToDomain(dto: TextCompletionMessageDto): TextCompletionMessage {

        return TextCompletionMessage(
            role = dto.role,
            content = dto.content,
            name = dto.name
        )
    }


    override fun mapToDto(domain: TextCompletionMessage): TextCompletionMessageDto {
        return TextCompletionMessageDto(
            role = domain.role,
            content = domain.content,
            name = domain.name
        )
    }
}