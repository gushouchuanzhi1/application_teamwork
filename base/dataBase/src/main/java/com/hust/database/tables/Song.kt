package com.hust.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "song")
data class TableSong(
    @PrimaryKey
    var songId: String,
    var songName: String,
    var songGenres: String
) : Serializable
