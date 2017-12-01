package com.jqyd.yuerduo.bean;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.Serializable;

/**
 * bean基类
 * Created by zhangfan on 2016/1/29.
 */
public class BaseBean implements Serializable {

    public String toGson() {
        try {
            return new Gson().toJson(this);
        } catch (Exception e) {
            Logger.e("数据生成gson字符串失败");
        }
        return null;
    }

}
