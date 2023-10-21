package com.hust.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hust.database.tables.ChatRecord

@Dao
abstract class ChatRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(record: ChatRecord)

    @Query("select * from chat_record where chatId=:chatId")
    abstract fun queryRecord(chatId: String): List<ChatRecord>

    @Query("select * from chat_record where chatId=:chatId limit 1")
    abstract fun queryOneRecord(chatId: String): ChatRecord
    // 慎用
    @Query("select * from chat_record")
    abstract fun queryAll(): List<ChatRecord>
}