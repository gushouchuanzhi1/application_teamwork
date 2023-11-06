package com.hust.netbase

import com.hust.resbase.OnFileReadCallback
import com.hust.resbase.OnFunctionCallBack
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBeanBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.regex.Pattern


object WebPageRequest {
    private const val BASE_URL = "https://music.163.com"
    fun getPlayList(input: InputStream, callBack: OnFileReadCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val reader = InputStreamReader(input, Charset.forName("UTF-8"))
            val strategy: ColumnPositionMappingStrategy<PlayList> = ColumnPositionMappingStrategy()
            strategy.setColumnMapping("name", "id")
            strategy.type = PlayList::class.java
            val csvToBean = CsvToBeanBuilder<PlayList>(reader)
                .withMappingStrategy(strategy)
                .build()
            try {
                val list = csvToBean.parse()
                callBack.onSuccess(list)
            }catch (e: Exception) {
                e.printStackTrace()
                callBack.onFailure(e.message.toString())
            }
        }
    }

    fun getSongList(input: InputStream, callBack: OnFileReadCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val reader = InputStreamReader(input, Charset.forName("UTF-8"))
            val strategy: ColumnPositionMappingStrategy<Song> = ColumnPositionMappingStrategy()
            strategy.setColumnMapping("songId", "songName", "songGenres")
            strategy.type = Song::class.java
            val csvToBean = CsvToBeanBuilder<Song>(reader)
                .withMappingStrategy(strategy)
                .build()
            try {
                val list = csvToBean.parse()
                callBack.onSuccess(list)
            }catch (e: Exception){
                e.printStackTrace()
                callBack.onFailure(e.message.toString())
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

    private fun getSongs(url: String, songList: ArrayList<Song>) {
        val doc = Jsoup.connect(url)
            .header("Cookie", "WM_TID=36fj4OhQ7NdU9DhsEbdKFbVmy9tNk1KM; _iuqxldmzr_=32; _ntes_nnid=26fc3120577a92f179a3743269d8d0d9,1536048184013; _ntes_nuid=26fc3120577a92f179a3743269d8d0d9; __utmc=94650624; __utmz=94650624.1536199016.26.8.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); WM_NI=2Uy%2FbtqzhAuF6WR544z5u96yPa%2BfNHlrtTBCGhkg7oAHeZje7SJiXAoA5YNCbyP6gcJ5NYTs5IAJHQBjiFt561sfsS5Xg%2BvZx1OW9mPzJ49pU7Voono9gXq9H0RpP5HTclE%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6eed5cb8085b2ab83ee7b87ac8c87cb60f78da2dac5439b9ca4b1d621f3e900b4b82af0fea7c3b92af28bb7d0e180b3a6a8a2f84ef6899ed6b740baebbbdab57394bfe587cd44b0aebcb5c14985b8a588b6658398abbbe96ff58d868adb4bad9ffbbacd49a2a7a0d7e6698aeb82bad779f7978fabcb5b82b6a7a7f73ff6efbd87f259f788a9ccf552bcef81b8bc6794a686d5bc7c97e99a90ee66ade7a9b9f4338cf09e91d33f8c8cad8dc837e2a3; JSESSIONID-WYYY=G%5CSvabx1X1F0JTg8HK5Z%2BIATVQdgwh77oo%2BDOXuG2CpwvoKPnNTKOGH91AkCHVdm0t6XKQEEnAFP%2BQ35cF49Y%2BAviwQKVN04%2B6ZbeKc2tNOeeC5vfTZ4Cme%2BwZVk7zGkwHJbfjgp1J9Y30o1fMKHOE5rxyhwQw%2B%5CDH6Md%5CpJZAAh2xkZ%3A1536204296617; __utma=94650624.1052021654.1536048185.1536199016.1536203113.27; __utmb=94650624.12.10.1536203113")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36")
            .get()
        val body = doc.body().toString()

        val pat1 = "obj"
        val r1 = Pattern.compile(pat1, Pattern.DOTALL)
        val m1 = r1.matcher(body)

        var label = "sss"
        while(m1.find()) {
            val labels = m1.group() ?: ""
            val pat2 = "<i>(?<label>.*?)</i>"
            val r2 = Pattern.compile(pat2)
            val m2 = r2.matcher(labels)
            val builder = StringBuilder()
            while(m2.find()) {
                builder.append(m2.group("label")?.plus('|') ?: "")
            }
            builder.deleteCharAt(builder.length - 1)
            label = builder.toString()
        }

        val pat3 = "<li><a href=\"/song\\?id=(?<songId>\\d+)\">(?<songName>.*?)</a></li>"
        val r3 = Pattern.compile(pat3)
        val m3 = r3.matcher(body)

        while(m3.find()) {
            val song = Song(
                songId = m3.group("songId") ?: "",
                songName = m3.group("songName") ?: "",
                songGenres = label
            )
            songList.add(song)
        }
    }


}