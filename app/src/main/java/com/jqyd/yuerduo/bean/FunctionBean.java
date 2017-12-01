package com.jqyd.yuerduo.bean;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jqyd.yuerduo.R;
import com.jqyd.yuerduo.constant.FunctionEnum;
import com.jqyd.yuerduo.constant.URLConstant;
import com.jqyd.yuerduo.extention.anko.BillDefineX;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangfan on 2015/12/18.
 */
public class FunctionBean implements Serializable {
    public String id;//功能id，唯一标示
    public String funcTitle;
    public String jsonParam;//参数
    public String funcName;//功能标示
    public int levels = 1;//1级为功能分组，2级为功能
    public List<FunctionBean> children = new ArrayList<>();
    public int icon;
    private String iconName;
    public FunctionEnum functionEnum;
    public long sort;//在服务器的排序权重

    public boolean checked;

    private String type;//类型

    /**
     * 获取功能图标id，0为获取失败
     *
     * @param context
     * @return
     */
    private int getIconResource(Context context) {
//        iconName = "xxtz";
        if (iconName == null) return 0;
        return context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
    }

    public void bindImageView(ImageView imageView) {
        int iconResource = getIconResource(imageView.getContext());
        if (iconResource > 0) {
            Glide.clear(imageView);
            imageView.setImageResource(iconResource);
        } else {
//            imageView.setImageResource(R.drawable.func_placeholder);
            Glide.with(imageView.getContext())
                    .load(URLConstant.ICONS_DOWNLOAD_HOST + "/" + iconName + ".png")
                    .placeholder(R.drawable.func_placeholder)
                    .crossFade()
                    .into(imageView);
        }
    }

    public void startActivity(Context context) {
        if (!TextUtils.isEmpty(funcName)) {
            try {
                functionEnum = FunctionEnum.valueOf(funcName);
            } catch (Exception ignored) {
            }
        }
        if (functionEnum != null && context != null) {
            try {
                Intent intent = new Intent(context, functionEnum.activity);
                try {
                    if (jsonParam != null) {
                        jsonParam = jsonParam.replace("&quot;", "\"");
                    }
                    JSONObject jsonObj = new JSONObject(jsonParam);
                    Iterator it = jsonObj.keys();
                    while (it.hasNext()) {
                        String key = it.next().toString();
                        if ("billDefine".equals(key)) {
                            intent.putExtra(key, new Gson().fromJson(jsonObj.getString(key), BillDefineX.class));
                            continue;
                        }
                        intent.putExtra(key, jsonObj.getString(key));
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                context.startActivity(intent);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        } else {
            if (context != null) Toast.makeText(context, "功能未开发", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取类型
     *
     * @return
     */
    public String getType() {
        try {
            if (jsonParam != null) {
                jsonParam = jsonParam.replace("&quot;", "\"");
            }
            JSONObject jsonObj = new JSONObject(jsonParam);
            Iterator it = jsonObj.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if (key.equals("type")) {
                    this.type = jsonObj.getString(key);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    @Override
    public boolean equals(Object o) {
        return this.id.equals(((FunctionBean) o).id);
    }
}
