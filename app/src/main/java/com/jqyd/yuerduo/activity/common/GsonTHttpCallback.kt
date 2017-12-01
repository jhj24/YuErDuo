package com.jqyd.yuerduo.activity.common

import android.os.Handler
import com.jqyd.yuerduo.net.ResultHolder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.*
import com.jqyd.yuerduo.MyApplication
import org.jetbrains.anko.toast

/**
 * json类请求结果处理器
 * Created by zhangfan on 16-6-7.
 */
abstract class GsonTHttpCallback<T> : Callback {

    companion object {
        var error: HashMap<Int, String> = HashMap()

        init {
            error.put(1, "请求失败，请重试")
            error.put(2, "请求失败")
            error.put(3, "数据异常")
            error.put(4, "数据格式异常")
            error.put(5, "服务器异常")
        }
    }

    var hanlder = Handler()

    override final fun onFailure(call: Call, e: IOException) {
        hanlder.post { callFailure(1) }
    }

    override final fun onResponse(call: Call, response: Response) {
        if (!response.isSuccessful) {
            hanlder.post { callFailure(2) }
            return;
        }
        val str = response.body().string()
        if (str.isNullOrBlank()) {
            hanlder.post { callFailure(3) }
            return;
        }

        val result: ResultHolder<T>?
        try {
            result = ResultHolder.fromJson<T>(str, getTClass())
        } catch(e: Exception) {
            hanlder.post { callFailure(4) }
            return;
        }

        if (result.result == 0) {
            hanlder.post { callFailure(5, result.msg) }
            return;
        }

        hanlder.post {
            if (result.showMsg && !result.msg.isNullOrBlank()) {
                val context = MyApplication.instance
                context.toast(result.msg)
            }
            onSuccess(result)
            onFinish()
        }

    }

    fun callFailure(errorCode: Int, msg: String? = null) {
        onFailure(msg ?: error.get(errorCode) ?: "", errorCode)
        onFinish()
    }

    abstract fun onFailure(msg: String, errorCode: Int);

    abstract fun onSuccess(result: ResultHolder<T>);

    open fun onFinish() {
    }

    abstract fun getTClass(): Class<*>
}