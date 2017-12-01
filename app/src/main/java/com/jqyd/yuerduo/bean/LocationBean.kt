package com.jqyd.yuerduo.bean

import java.io.Serializable

/**
 * 位置类，用于上传和本地缓存, 百度坐标系
 * Created by zhangfan on 2016/4/19 0019.
 */
class LocationBean(
        var lon: Double,
        var lat: Double,
        var radius: Float,
        var provice: String? = "",
        var city: String? = "",
        var district: String? = "",
        var address: String? = "",
        var content: String? = "",
        var createTime: String? = "",
        var lbstype: Int = -1,
        //100以后为自定义错误，101解析位置失败
        var errorCode: Int = -1

) : Serializable {
    fun isSuccess(): Boolean {
        return errorCode == 0;
    }
}
