package com.hust.netbase

import com.hust.database.tables.ChatRecord

data class ChatMessage(
    val content: String,
    val content_timestamp: Long
)

data class ChatUnit(
    val use_id: Int,
    val profilePicPath: Int,
    val nickname: String,
    val message: ChatRecord
)

data class FindUnit(
    val title: String,
    val profilePicPath: Int
)