package com.hust.database

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import java.util.LinkedList

class MMKVUtil private constructor() {
    companion object {
        private lateinit var mmkv: MMKV
        private var mmkvUtil: MMKVUtil? = null

        fun getMMKV(context: Context): MMKVUtil {
            if (mmkvUtil == null) {
                synchronized(MMKVUtil::class.java) {
                    if (mmkvUtil == null) {
                        MMKV.initialize(context)
                        mmkv = MMKV.defaultMMKV()
                        mmkvUtil = MMKVUtil()
                    }
                }
            }
            return mmkvUtil!!
        }
    }

    /**
     * 写入基本数据类型缓存
     *
     * @param key    键
     * @param object 值
     */
    fun put(key: String?, `object`: Any) {
        mmkv.let {
            when (`object`) {
                is String -> {
                    it.encode(key, `object`)
                }

                is Int -> {
                    it.encode(key, `object`)
                }

                is Boolean -> {
                    it.encode(key, `object`)
                }

                is Float -> {
                    it.encode(key, `object`)
                }

                is Long -> {
                    it.encode(key, `object`)
                }

                is Double -> {
                    it.encode(key, `object`)
                }

                is ByteArray -> {
                    it.encode(key, `object`)
                }

                else -> {
                    it.encode(key, `object`.toString())
                }
            }
        }
    }

    fun putSet(key: String?, sets: Set<String?>?) {
        mmkv.encode(key, sets)
    }

    fun putParcelable(key: String?, obj: Parcelable?) {
        mmkv.encode(key, obj)
    }

    fun getInt(key: String?): Int? =
        mmkv.decodeInt(key, 0)

    fun getInt(key: String?, defaultValue: Int): Int =
        mmkv.decodeInt(key, defaultValue)


    fun getDouble(key: String?): Double =
        mmkv.decodeDouble(key, 0.00)

    fun getDouble(key: String?, defaultValue: Double): Double =
        mmkv.decodeDouble(key, defaultValue)

    fun getLong(key: String?): Long =
        mmkv.decodeLong(key, 0L)

    fun getLong(key: String?, defaultValue: Long): Long =
        mmkv.decodeLong(key, defaultValue)

    fun getBoolean(key: String?): Boolean =
        mmkv.decodeBool(key, false)

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean =
        mmkv.decodeBool(key, defaultValue)

    fun getFloat(key: String?): Float =
        mmkv.decodeFloat(key, 0f)

    fun getFloat(key: String?, defaultValue: Float): Float =
        mmkv.decodeFloat(key, defaultValue)

    fun getBytes(key: String?): ByteArray? =
        mmkv.decodeBytes(key)

    fun getBytes(key: String?, defaultValue: ByteArray?): ByteArray? =
        mmkv.decodeBytes(key, defaultValue)

    fun getString(key: String?): String? =
        mmkv.decodeString(key, "")

    fun getString(key: String?, defaultValue: String?): String? =
        mmkv.decodeString(key, defaultValue)

    /**
     * 存放array
     */
    fun <T> setArray(name: String, list: MutableList<T>?) {
        mmkv.let {
            if (list.isNullOrEmpty()) { //清空
                it.putInt(name + "size", 0)
                val size: Int = it.getInt(name + "size", 0)
                for (i in 0 until size) {
                    if (it.getString(name + i, null) != null) {
                        it.remove(name + i)
                    }
                }
            } else {
                it.putInt(name + "size", list.size)
                if (list.size > 20) {
                    list.removeAt(0) //只保留后20条记录
                }
                for (i in list.indices) {
                    it.remove(name + i)
                    it.remove(Gson().toJson(list[i])) //删除重复数据 先删后加
                    it.putString(name + i, Gson().toJson(list[i]))
                }
            }
        }
    }

    /**
     * 获取存取的array,主要为近期使用表情包服务
     */
    fun <T> getArray(name: String, bean: T): List<T> {
        mmkv.let {
            val list: MutableList<T> = LinkedList()
            val size: Int = it.getInt(name + "size", 0)
            for (i in 0 until size) {
                if (it.getString(name + i, null) != null) {
                    try {
                        list.add(
                            Gson().fromJson(it.getString(name + i, null), bean!!::class.java) as T
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return list
        }
    }

    fun getStringSet(key: String?): Set<String?>? =
        mmkv.decodeStringSet(key, emptySet<String>())

    fun getParcelable(key: String?): Parcelable? =
        mmkv.decodeParcelable<Parcelable>(key, null)

    /**
     * 移除某个key对
     */
    fun removeKey(key: String?) =
        mmkv.removeValueForKey(key)

    /**
     * 清除所有key
     */
    fun clearAll() =
        mmkv.clearAll()
}