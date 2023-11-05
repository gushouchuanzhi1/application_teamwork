package com.hust.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "song")
data class Song(
    @PrimaryKey
    val songId: String,
    val songName: String,
    val songGenres: String
) : Serializable
