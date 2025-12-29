package com.wodox.data.remote.model.mapper

import com.wodox.data.common.base.AbstractDtoMapper
import com.wodox.data.remote.model.model.request.SchemaDto
import com.starnest.domain.remote.model.response.Schema

class SchemaMapper :
    AbstractDtoMapper<SchemaDto, Schema>() {
    override fun mapToDomain(dto: SchemaDto): Schema {
        return Schema(
            additionalProperties = dto.additionalProperties,
            properties = dto.properties,
            required = dto.required.map { it },
            type = dto.type,
        )
    }


    override fun mapToDto(domain: Schema): SchemaDto {
        return SchemaDto(
            additionalProperties = domain.additionalProperties,
            properties = domain.properties,
            required = domain.required.map { it },
            type = domain.type,
        )
    }
}