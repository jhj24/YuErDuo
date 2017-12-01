package com.jqyd.yuerduo.net;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.orhanobut.logger.Logger;

import java.lang.reflect.ParameterizedType;

import cz.msebera.android.httpclient.Header;

/**
 * gson解析处理器
 * Created by zhangfan on 2016/1/25.
 */
public abstract class GsonHttpResponseHandler<T> extends BaseJsonHttpResponseHandler<ResultHolder<T>> {

    @Override
    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, ResultHolder<T> response) {
//        Logger.i(""+rawJsonResponse);
//        Logger.json(rawJsonResponse);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResultHolder<T> errorResponse) {
//        Logger.i(""+rawJsonData);
//        Logger.e(throwable.getMessage());
    }

    @Override
    protected final ResultHolder<T> parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
        if (isFailure) return null;
        Class<T> entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return ResultHolder.fromJson(rawJsonData, entityClass);
    }

    @Override
    public void onRetry(int retryNo) {
        super.onRetry(retryNo);
        Logger.i(String.format("Request retry no. %d", retryNo));
    }
}
