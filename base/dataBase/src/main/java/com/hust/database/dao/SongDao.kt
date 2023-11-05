package com.hust.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hust.database.tables.Song

@Dao
abstract class SongsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(song: Song)

    @Query("select * from song where songId=:id")
    abstract fun getSongById(id:String):Song

    @Query("select * from song where songGenres=:genre")
    abstract fun getSongsByGenre(genre:String):List<Song>

    @Query("select * from song")
    abstract fun getAllSongs():List<Song>
}