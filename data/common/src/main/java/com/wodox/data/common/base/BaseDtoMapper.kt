package com.starnest.data.base

interface BaseDtoMapper<DTO, Domain> {
    fun mapToDomain(dto: DTO): Domain
    fun mapToDto(domain: Domain): DTO
    fun mapToDomainList(dtos: List<DTO>): List<Domain>
    fun mapToDtoList(domains: List<Domain>): List<DTO>
} 