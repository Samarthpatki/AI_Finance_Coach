package com.samarth.aifinancecoach.data.mapper

import com.samarth.aifinancecoach.data.local.entity.AiMessageEntity
import com.samarth.aifinancecoach.domain.model.AiMessage
import com.samarth.aifinancecoach.domain.model.MessageRole

object AiMessageMapper {
    fun AiMessageEntity.toDomain(): AiMessage {
        return AiMessage(
            id = id,
            role = MessageRole.valueOf(role),
            content = content,
            timestamp = timestamp,
            isError = isError
        )
    }

    fun AiMessage.toEntity(): AiMessageEntity {
        return AiMessageEntity(
            id = id,
            role = role.name,
            content = content,
            timestamp = timestamp,
            isError = isError
        )
    }
}
