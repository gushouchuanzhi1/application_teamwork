package com.hust.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hust.database.tables.RecommendUserSong

@Dao
abstract class RecommendUserSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(recommend_user_song:RecommendUserSong)

    @Query("delete from recommend_user_song where userId=:userId and songId=:songId")
    abstract fun cancelLike(userId: Int, songId: String)

    @Query("select * from recommend_user_song where userId=:uid")
    abstract fun getRecommendByUser(uid:Int): List<RecommendUserSong>

    @Query("select * from recommend_user_song where userId=:userId and songId=:songId")
    abstract fun isLikeTheSong(userId: Int, songId: String): RecommendUserSong?

    @Query("select * from recommend_user_song where songId=:sid")
    abstract fun getRecommendBySong(sid:String):RecommendUserSong

    @Query("select * from recommend_user_song")
    abstract fun getAllRecommend():List<RecommendUserSong>
}
