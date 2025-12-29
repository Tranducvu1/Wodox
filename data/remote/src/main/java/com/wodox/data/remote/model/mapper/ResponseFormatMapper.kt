package com.wodox.data.remote.model.mapper

import com.wodox.data.common.base.AbstractDtoMapper
import com.wodox.data.remote.model.model.request.ResponseFormatDto
import com.starnest.domain.remote.model.response.ResponseFormat

class ResponseFormatMapper(val mapper: JsonSchemaMapper) :
    AbstractDtoMapper<ResponseFormatDto, ResponseFormat>() {
    override fun mapToDomain(dto: ResponseFormatDto): ResponseFormat {
        return ResponseFormat(
            jsonSchema = mapper.mapToDomain(dto.jsonSchema),
            type = dto.type
        )
    }


    override fun mapToDto(domain: ResponseFormat): ResponseFormatDto {
        return ResponseFormatDto(
            jsonSchema = mapper.mapToDto(domain.jsonSchema),
            type = domain.type
        )
    }
}