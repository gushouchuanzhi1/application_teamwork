package com.hust.resbase

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

/**
 * @classname: TimeUtil
 * @description:
 * @date:
 * @version:1.0
 * @author:
 */
object TimeUtil {
    /**
     * 标准时间解析
     */
    fun time(created_timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.CHINA)

        try {
            calendar.timeInMillis = created_timestamp
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // 月份从 0 开始，所以要加 1
            val day1 = calendar.get(Calendar.DAY_OF_MONTH)
            calendar.timeInMillis = System.currentTimeMillis()
            val dc = abs(System.currentTimeMillis() - created_timestamp)
            val seconds = dc / 1000
            val day = seconds / (24 * 60 * 60) //相差的天数
            val hour = (seconds - day * 24 * 60 * 60) / (60 * 60) //相差的小时数
            val minute = (seconds - day * 24 * 60 * 60 - hour * 60 * 60) / 60 //相差的分钟数
            val second = seconds - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60 //相差的秒数
            return if (day > 8) {
                "$year-$month-$day1"
            } else if (day > 0) {
                day.toString() + "天前"
            } else if (hour > 0) {
                hour.toString() + "小时前"
            } else if (minute > 0) {
                minute.toString() + "分钟前"
            } else {
                "刚刚"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}