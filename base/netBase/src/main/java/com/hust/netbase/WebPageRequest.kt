package com.hust.netbase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.util.regex.Pattern


object WebPageRequest {
    private const val BASE_URL = "https://music.163.com"

    fun getHomepage() {
        CoroutineScope(Dispatchers.IO).launch {
            for (i in 0 until 30) {
                val playList = getAllHotSong(
                    "https://music.163.com/discover/playlist/" +
                            "?order=hot&cat=%E5%85%A8%E9%83%A8&limit=35&offset=${35 * i}"
                )
                playList.forEach { pair ->
                    getSongs("https://music.163.com/playlist?id=111${pair.first}")
                }
            }
        }
    }

    private fun getAllHotSong(url: String): ArrayList<Pair<String?, String?>> {
        val doc = Jsoup.connect(url).userAgent("Mozilla").get()
        val body = doc.body().toString()

        val playList = arrayListOf<Pair<String?, String?>>()

        // 匹配到歌曲id
        val pat1 = "<a title=\"(?<title>.*?)\" href=\"/playlist\\?id=(?<id>\\d+)\" class=\"msk\"></a>"
        val r1 = Pattern.compile(pat1)
        val m1 = r1.matcher(body)

        while (m1.find()) {
            playList.add(
                Pair(
                    m1.group("id"),
                    m1.group("title")
                )
            )
        }
        return playList
    }

    private fun getSongs(url: String) {
        val doc = Jsoup.connect(url).userAgent("Mozilla").get()
        val body = doc.body().toString()

        val songList = arrayListOf<Pair<String?, String?>>()

        val pat1 = "<tr id=\"(\\d+)\" class=\"even \">(?<content>.*?)</tr>"
        val r1 = Pattern.compile(pat1)
        val m1 = r1.matcher(body)

        while(m1.find()) {
            val content = m1.group("content")
            content?.let {
                val pat2 = "<a href=\"/song?id=(?<songId>\\d+)\"><b title=\"(?<songName>.*?)\">"
                val r2 = Pattern.compile(pat2)
                val m2 = r1.matcher(it)

                while(m2.find()) {
                    songList
                }
            }
        }
    }
}