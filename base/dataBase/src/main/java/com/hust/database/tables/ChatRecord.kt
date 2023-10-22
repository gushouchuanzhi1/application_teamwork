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

    override fun equals(other: Any?): Boolean {
        if(other is ChatRecord) {
            return other.msgSeq == msgSeq && other.content == content
                    && other.ownerId == ownerId && other.chatId == chatId
                    && other.createAt == createAt
        }
        return false
    }

    override fun hashCode(): Int {
        var result = msgSeq.hashCode()
        result = 31 * result + ownerId
        result = 31 * result + chatId.hashCode()
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + createAt.hashCode()
        return result
    }
}