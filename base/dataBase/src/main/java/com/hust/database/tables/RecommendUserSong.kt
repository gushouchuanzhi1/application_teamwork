package com.hust.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "recommend_user_song")
data class RecommendUserSong(
    @PrimaryKey
    val userId:String,
    val songId:String
) : Serializable