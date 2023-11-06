package com.hust.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hust.database.tables.TablePlayList

@Dao
abstract class PlayListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(playList: TablePlayList)

    @Query("select * from playList where id=:id")
    abstract fun getPlayListById(id: String): TablePlayList?

    @Query("select * from playList")
    abstract fun getAllPlayList(): List<TablePlayList>?
}