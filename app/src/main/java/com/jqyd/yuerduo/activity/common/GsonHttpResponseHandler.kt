package com.jqyd.yuerduo.activity.common

import com.jqyd.yuerduo.net.ResultHolder
import com.loopj.android.http.BaseJsonHttpResponseHandler
import com.orhanobut.logger.Logger
import cz.msebera.android.httpclient.Header

/**
 * gson解析处理器
 * Created by zhangfan on 2016/1/25.
 */
abstract class GsonHttpResponseHandler<T> : BaseJsonHttpResponseHandler<ResultHolder<T>>() {

    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, rawJsonResponse: String?, response: ResultHolder<T>?) {

    }

    override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, rawJsonData: String?, errorResponse: ResultHolder<T>?) {

    }

    abstract fun getCls():Class<*>

    @Throws(Throwable::class)
    override fun parseResponse(rawJsonData: String, isFailure: Boolean): ResultHolder<T>? {
        if (isFailure) return null
        return ResultHolder.fromJson<T>(rawJsonData, getCls())
    }

    override fun onRetry(retryNo: Int) {
        super.onRetry(retryNo)
        Logger.i(String.format("Request retry no. %d", retryNo))
    }
}
