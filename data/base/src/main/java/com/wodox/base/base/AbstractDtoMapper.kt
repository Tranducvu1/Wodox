package com.wodox.base.base

abstract class AbstractDtoMapper<DTO, Domain> : BaseDtoMapper<DTO, Domain> {
    
    override fun mapToDomainList(dtos: List<DTO>): List<Domain> {
        return dtos.map { mapToDomain(it) }
    }

    override fun mapToDtoList(domains: List<Domain>): List<DTO> {
        return domains.map { mapToDto(it) }
    }
} 