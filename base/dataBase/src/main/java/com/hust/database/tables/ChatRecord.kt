package com.hust.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "chat_record")
data class ChatRecord (
    var ownerId: Int,

    var chatId: String,

    var content: String?,

    var createAt: Long
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var msgSeq: Int = 0
}