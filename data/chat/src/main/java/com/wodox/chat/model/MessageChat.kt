package com.wodox.chat.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["id"], unique = true),
    ]
)
data class MessageChatEntity(
    @PrimaryKey
    @PropertyName("id")
    val id: String = "",

    @PropertyName("text")
    var text: String = "",

    @PropertyName("senderId")
    val senderId: String = "",

    @PropertyName("receiverId")
    val receiverId: String = "",

    @PropertyName("timestamp")
    val timestamp: Long = 0L,

    @PropertyName("status")
    val status: String = "SENT",

) {
    constructor() : this("", "", "", "", 0L, "SENT")
}