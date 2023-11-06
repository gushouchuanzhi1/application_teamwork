package com.hust.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "recommend_user_song")
data class RecommendUserSong(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var userId: Int,
    var songId: String,
    var createAt: Long
) : Serializable