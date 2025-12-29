package com.wodox.data.remote.model.mapper

import com.wodox.data.common.base.AbstractDtoMapper
import com.wodox.data.remote.model.model.request.JsonSchemaDto
import com.starnest.domain.remote.model.response.JsonSchema

class JsonSchemaMapper(val schemaMapper: SchemaMapper) :
    AbstractDtoMapper<JsonSchemaDto, JsonSchema>() {
    override fun mapToDomain(dto: JsonSchemaDto): JsonSchema {
        return JsonSchema(
            name = dto.name,
            schema = schemaMapper.mapToDomain(dto.schema),
            strict = dto.strict,
        )
    }


    override fun mapToDto(domain: JsonSchema): JsonSchemaDto {
        return JsonSchemaDto(
            name = domain.name,
            schema = schemaMapper.mapToDto(domain.schema),
            strict = domain.strict,
        )
    }
}