package com.hust.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_to_user")
data class UserToUser (
    var selfId: Int,

    var friendId: Int,

    var friendNickname: String,

    var friendProfilePicPath: String,

    var chatId: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}