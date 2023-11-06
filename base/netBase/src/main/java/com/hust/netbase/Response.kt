package com.hust.netbase

import com.hust.database.tables.ChatRecord
import com.hust.database.tables.RecommendUserSong
import com.hust.database.tables.TablePlayList
import com.hust.database.tables.TableSong
import java.io.Serializable

data class ChatMessage(
    val content: String,
    val content_timestamp: Long
)
data class ChatUnit(
    val use_id: Int,
    val profilePicPath: String,
    val nickname: String,
    val message: ChatRecord
): Serializable

data class FindUnit(
    val title: String,
    val profilePicPath: String
)

data class PlayList(
    var id: String = "",
    var name: String = ""
) {
    fun asTablePlayList(): TablePlayList {
        return TablePlayList(
            id = id,
            name = name
        )
    }
}

data class Song(
    var songId: String = "",
    var songName: String = "",
    var songGenres: String = ""
) {
    fun asTableSong(): TableSong {
        return TableSong(
            songId = songId,
            songName = songName,
            songGenres = songGenres
        )
    }
}

data class UserLike(
    val info: RecommendUserSong,
    val nickname: String,
    val profilePicPath: String,
    val songName: String,
    val isLike: Boolean = false
)