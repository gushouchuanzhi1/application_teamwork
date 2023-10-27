package com.hust.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hust.database.tables.UserToUser

@Dao
abstract class UserToUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(info: UserToUser)

    @Query("select * from user_to_user where selfId=:who")
    abstract fun queryFriends(who: Int): List<UserToUser>?

    @Query("select * from user_to_user where selfId=:a and friendNickname=:b")
    abstract fun hasFriend(a: Int, b: String): UserToUser?

    @Query("select * from user_to_user")
    abstract fun queryAll(): List<UserToUser>?
}