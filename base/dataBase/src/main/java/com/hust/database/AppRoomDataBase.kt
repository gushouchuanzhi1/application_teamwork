package com.hust.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hust.database.dao.ChatRecordDao
import com.hust.database.dao.PlayListDao
import com.hust.database.dao.RecommendUserSongDao
import com.hust.database.dao.SongsDao
import com.hust.database.dao.UserToUserDao
import com.hust.database.dao.UsersDao
import com.hust.database.tables.ChatRecord
import com.hust.database.tables.RecommendUserSong
import com.hust.database.tables.TablePlayList
import com.hust.database.tables.TableSong
import com.hust.database.tables.User
import com.hust.database.tables.UserToUser

@Database(entities = [User::class, UserToUser::class, ChatRecord::class, TableSong::class, TablePlayList::class, RecommendUserSong::class], version = 1)
abstract class AppRoomDataBase : RoomDatabase() {
    //创建DAO的抽象类
    abstract fun userDao(): UsersDao
    abstract fun userToUserDao(): UserToUserDao
    abstract fun chatRecordDao(): ChatRecordDao
    abstract fun songDao(): SongsDao
    abstract fun playListDao(): PlayListDao
    abstract fun recommendDao(): RecommendUserSongDao
    companion object {
        private const val TAG = "AppRoomDataBase"
        //DATABASE_NAME名称可以叫simple_app或simple_app.db，正常来说应该叫        //simple_app.db，但是名称叫simple_app也没问题
        const val DATABASE_NAME = "app_data.db"


        @Volatile
        private var databaseInstance: AppRoomDataBase? = null

        @Synchronized
        @JvmStatic
        fun get(): AppRoomDataBase {
            try {
                if (databaseInstance == null) {
                    databaseInstance = Room.databaseBuilder(
                        BaseApplication.getContext(),
                        AppRoomDataBase::class.java,
                        DATABASE_NAME
                    ).build()
                }
            }catch (e:Exception) {
                e.printStackTrace()
            }

            return databaseInstance!!
        }
    }
}