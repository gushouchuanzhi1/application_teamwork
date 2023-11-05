package com.hust.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hust.database.tables.RecommendUserSong

@Dao
abstract class RecommendUserSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(recommend_user_song:RecommendUserSong)

    @Query("select * from recommend_user_song where userId=:uid")
    abstract fun getRecommendByUser(uid:String):RecommendUserSong

    @Query("select * from recommend_user_song where songId=:sid")
    abstract fun getRecommendBySong(sid:String):RecommendUserSong

    @Query("select * from recommend_user_song")
    abstract fun getAllRecommend():List<RecommendUserSong>
}
