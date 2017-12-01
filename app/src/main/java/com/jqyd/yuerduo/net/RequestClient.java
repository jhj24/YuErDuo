package com.jqyd.yuerduo.net;

import android.content.Context;

import com.jqyd.yuerduo.bean.UserBean;
import com.jqyd.yuerduo.util.SystemEnv;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by zhangfan on 2016/1/26.
 */
@Deprecated
public class RequestClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
//        client.setTimeout(30*1000);
//        client.setMaxRetriesAndTimeout(30, 10000);
    }

    @Deprecated
    public static RequestHandle request(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        UserBean login = SystemEnv.getLogin(context);
        if (login != null) {
            if (!params.has("memberId")) {
                params.put("memberId", login.getMemberId());
            }
            if (!params.has("storeId")) {
                params.put("storeId", login.getStoreId());
            }
        }
        return client.post(context, url, params, responseHandler);
    }

}
