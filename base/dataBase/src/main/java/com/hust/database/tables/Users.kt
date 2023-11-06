package com.hust.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey
    var id: Int,
    @ColumnInfo(name = "user_name")
    var userName: String,

    var password: String,

    var createdAt: Long,

    var nickname: String,

    var profilePicPath: String
) {

}