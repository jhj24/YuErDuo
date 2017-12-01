package com.jqyd.yuerduo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jqyd.yuerduo.bean.BaseBean;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class PreferenceUtil {

    public static String generateKey(String type, String id) {
        return type.concat("_" + id);
    }

    public static String[] parseKey(String key) {
        return key.split("_");
    }

    public static <T> boolean save(Context context, T entity, String key) {
        if (entity == null) {
            return false;
        }
        String prefFileName = entity.getClass().getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        SharedPreferences.Editor et = sp.edit();
        String json = toGson(entity);
        et.putString(key, json);
        return et.commit();
    }

    public static <T extends BaseBean> List<T> findAll(Context context, Class<T> clazz) {
        String prefFileName = clazz.getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        Map<String, String> values = (Map<String, String>) sp.getAll();

        List<BaseBean> results = new ArrayList<BaseBean>();

        if (values == null || values.isEmpty())
            return (List<T>) results;

        Collection<String> colles = values.values();

        for (String json : colles) {
            BaseBean bean = parseJson(json, clazz);
            results.add(bean);
        }
        return (List<T>) results;
    }

    public static <T> T find(Context context, String key, Class<T> clazz) {
        String prefFileName = clazz.getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        String json = sp.getString(key, null);
        if (json == null)
            return null;
        return parseJson(json, clazz);
    }

    public static <T extends Serializable> void delete(Context context, String key, Class<T> clazz) {
        String prefFileName = clazz.getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        if (sp.contains(key)) {
            sp.edit().remove(key).commit();
        }
    }

    public static <T extends Serializable> void deleteAll(Context context, Class<T> clazz) {
        String prefFileName = clazz.getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        sp.edit().clear().commit();
    }


    public static boolean save(Context context, String file, String key, String str) {
        if (str == null) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(file, 0);
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, str);
        return et.commit();
    }

    public static boolean saveIntValue(Context context, int memberId, String key, int vaule) {
        if (key == null) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences("yuerduo" + memberId, 0);
        SharedPreferences.Editor et = sp.edit();
        et.putInt(key, vaule);
        return et.commit();
    }

    public static int findIntValue(Context context, int memberId, String key) {
        if (key == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences("yuerduo" + memberId, 0);
        int num = sp.getInt(key, 0);
        return num;
    }


    public static <T> T parseJson(String json, Class cls) {
        try {
            return (T) new Gson().fromJson(json, cls);
        } catch (JsonSyntaxException e) {
            Logger.e("解析gson字符串失败");
        }
        return null;
    }

    public static String toGson(Object obj) {
        try {
            return new Gson().toJson(obj);
        } catch (Exception e) {
            Logger.e("数据生成gson字符串失败");
        }
        return null;
    }
}
