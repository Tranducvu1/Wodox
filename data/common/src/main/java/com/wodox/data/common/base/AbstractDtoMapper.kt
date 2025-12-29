package com.wodox.data.common.base

import com.starnest.data.base.BaseDtoMapper

abstract class AbstractDtoMapper<DTO, Domain> : BaseDtoMapper<DTO, Domain> {
    
    override fun mapToDomainList(dtos: List<DTO>): List<Domain> {
        return dtos.map { mapToDomain(it) }
    }

    override fun mapToDtoList(domains: List<Domain>): List<DTO> {
        return domains.map { mapToDto(it) }
    }
} 