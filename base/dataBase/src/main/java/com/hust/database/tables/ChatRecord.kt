package com.hust.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_record")
class ChatRecord {
    @PrimaryKey(autoGenerate = true)
    var msgSeq: Long = 0L

    var ownerId: Int = -1

    var chatId: String = ""

    var content: String? = null

    var createAt: Long = 0L
}