package com.jqyd.yuerduo.net;

import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 网络请求返回结果
 * Created by zhangfan on 2016/1/27.
 */
public class ResultHolder<T> implements Serializable {

    /**
     * 1、成功，0、失败
     */
    public int result;
    /**
     * 展示msg提示信息
     */
    public boolean showMsg;
    /**
     * 提示信息，result为0或showMsg为true时生效
     */
    public String msg;

    private int pageNo;
    private int pageSize;
    private int total;

    private List<T> data;

    public T getData() {
        if (data != null && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    public List<T> getDataList() {
        return data;
    }

    public static <T> ResultHolder<T> fromJson(String json, Class clazz) {
        Gson gson = new Gson();
        Type objectType = type(ResultHolder.class, clazz);
        return gson.fromJson(json, objectType);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    private static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
}
