package com.jqyd.yuerduo

import com.google.gson.Gson
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.util.MapUtil
import org.junit.Test

/**
 * Created by zhangfan on 2016/11/2 0002.
 */

class Test1 {

    @Test
    fun testA() {
        println("yoooo")
        listOf(1, 2, 3).forEach(::println)

        val sss = """{"itemList":[{"define":{"id":"name","textColor":"#FF0000","text":"默认内容\n内容","labelColor":"#0000FF","data":"被present重置的值","hintTextColor":"#00FF00","label":"名称","hint":"请输入","singleLine":false},"type":0},{"define":{"id":"text1","data":"","hintTextColor":"#00FF00","label":"文本1","hint":"请输入。。。","textColor":"#FF0000","labelColor":"#0000FF"},"type":0},{"define":{"id":"text2","data":"","label":"文本2","hint":"文本1改变时会同步这个文本","textColor":"#FF7777","labelColor":"#FF00FF"},"type":0},{"define":{"id":"testTime","data":"2016-11-03","label":"全时间","hint":"选个时间吧"},"type":0},{"define":{"id":"myTouch","data":"","label":"点点","hint":"点一点","labelColor":"#FF0000"},"type":0}],"editable":true}"""
        val fromJson = Gson().fromJson(sss, BillDefineX::class.java)
        println(fromJson)
    }

    //    @org.junit.Test
    fun testEx() {

        class A {

        }

        class B {

        }

        fun A.d(a: Int, cb: B.() -> Unit): B {
            return B().apply {
                cb()
            }
        }

        fun A.c() = d(1) {
            //求怎么获取到A的this
            println(this)
        }

        A().apply {
            c()
        }

    }

    @Test
    fun sss() {
        val getDistance = MapUtil.GetDistance(39.9126700000, 116.3806190000, 39.9138880000, 116.4246720000)
        println(getDistance)
    }

    @Test
    fun gaode2baidu() {
        val gcj02_To_Bd09 = MapUtil.gcj02_To_Bd09(39.9065373794, 116.3741705886)
        println(gcj02_To_Bd09)
    }


    @Test
    fun baidu2gaode() {
        val gcj02_To_Bd09 = MapUtil.bd09_To_Gcj02(39.9126700000, 116.3806190000)
        println(gcj02_To_Bd09)
    }


    var myFun: (Int) -> Boolean = a@{ index ->
        if (index < 5) {
            return@a true
        }
        //balabala...
        false
    }

    @Test
    fun hehe() {
        val a: String? ="ss"
        print(a?.let { "1" } ?: "2")
    }
}
