package com.hust.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User {
    @PrimaryKey(autoGenerate = true)
    var id: Int = -1

    @ColumnInfo(name = "user_name")
    var userName: String = ""

    var password: String = ""

    var createdAt: Long = 0
}