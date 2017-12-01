package com.jqyd.yuerduo.global

import java.util.*

/**
 * 全局事件
 * Created by zhangfan on 16-6-30.
 */
object GlobalEvent {

    private val map: HashMap<Any, (Any?) -> Unit> = HashMap();

    /**
     * 注册事件
     * @param key 事件标示
     * @param callBack 事件回调
     */
    fun register(key: Any, callBack: (Any?) -> Unit) {
        map.put(key, callBack);
    }

    /**
     * 投递事件
     * @param key 事件标示
     * @param param 事件参数
     */
    fun post(key: Any, param: Any?) {
        if (!map.containsKey(key)) return
        val callBack = map[key]
        callBack ?: map.remove(key)
        callBack?.let {
            callBack(param)
        }
    }

    /**
     * 注销事件监听
     * @param key 事件标示
     */
    fun unRegister(key: Any) {
        map.remove(key)
    }
}
