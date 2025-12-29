package com.starnest.data.asset.datasource.mapper

import com.starnest.data.asset.datasource.model.MessageTemplateDto
import com.wodox.data.common.base.AbstractMapper
import com.wodox.domain.asset.model.MessageTemplate

class MessageTemplateMapper : AbstractMapper<MessageTemplateDto, MessageTemplate>() {
    override fun mapToDomain(dto: MessageTemplateDto): MessageTemplate {
        return MessageTemplate(
            id = dto.id,
            name = dto.name,
            category = dto.category,
            description = dto.description,
            isSelected = dto.isSelected,
        )
    }

    override fun mapToEntity(domain: MessageTemplate): MessageTemplateDto {
        return MessageTemplateDto(
            id = domain.id,
            name = domain.name,
            category = domain.category,
            description = domain.description,
            isSelected = domain.isSelected,
        )
    }
}