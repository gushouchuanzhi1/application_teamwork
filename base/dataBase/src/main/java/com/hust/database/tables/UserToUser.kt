package com.hust.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_to_user")
class UserToUser {
    @PrimaryKey
    var selfId: Int = -1

    var friendNickname: String = ""

    var friendProfilePicPath: Int = -1

    var chatId: String = ""
}