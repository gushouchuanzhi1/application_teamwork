package com.hust.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playList")
data class TablePlayList(
    @PrimaryKey
    var id: String,
    var name: String
)