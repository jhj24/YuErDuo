package com.jqyd.yuerduo.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Serializable

/**
 * Created by zhangfan on 2016/12/16 0016.
 *
 */
object AreaUtil {
    /**
     * 获取省市县信息
     */
    fun getAreaList(context: Context): List<AreaNode> {
        val inputStream = context.resources.assets.open("area.json")
        val text = inputStream.use {
            val buf = BufferedReader(InputStreamReader(inputStream, "utf-8"))
            buf.readText()
        }
        return Gson().fromJson(text, object : TypeToken<List<AreaNode>>() {}.type)
    }

    class AreaNode(var areaDeep:Int = 1,
                   var areaId:Int = 1,
                   var countChildren:Int = 1,
                   var areaName: String = "",
                   var areaParentId:Int = 1,
                   var areaSort: Int = 1) : Serializable {
//        "areaDeep":1, "areaId":1,  "countChildren":0,"areaName":"北京",  "areaParentId":0,"areaSort":0,"childern":[]

        override fun toString(): String {
            return areaName
        }
    }

    /**
     * 读取简繁字体库
     */
    fun convert(context: Context): Character {
        val inputStream = context.resources.assets.open("character.json")
        val text = inputStream.use {
            val buf = BufferedReader(InputStreamReader(inputStream, "utf-8"))
            buf.readText()
        }
        return Gson().fromJson(text, object : TypeToken<Character>() {}.type)
    }

    class Character(
            var simple: String = "",
            var complex: String = ""
    ) : Serializable
}