package com.wodox.data.remote.model.mapper

import com.wodox.data.common.base.AbstractDtoMapper
import com.wodox.data.remote.model.model.response.TextCompletionDataDto
import com.starnest.domain.remote.model.response.TextCompletionData

class TextCompletionDataMapper(val mapper: TextCompletionChoiceMapper) :
    AbstractDtoMapper<TextCompletionDataDto, TextCompletionData>() {

    override fun mapToDomain(dto: TextCompletionDataDto): TextCompletionData {
        return TextCompletionData(
            id = dto.id,
            choices = mapper.mapToDomainList(dto.choices)
        )
    }

    override fun mapToDto(domain: TextCompletionData): TextCompletionDataDto {
        return TextCompletionDataDto(
            id = domain.id,
            choices = mapper.mapToDtoList(domain.choices)
        )
    }
}