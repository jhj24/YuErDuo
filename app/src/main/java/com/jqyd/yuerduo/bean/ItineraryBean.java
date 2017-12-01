package com.jqyd.yuerduo.bean;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushiqi on 2017/9/20,0020.
 */

public class ItineraryBean extends BaseBean {

    public int id; // 人员的id
    public String itineraryContent; // 行程内容
    public String staffId; // 人员Id
    public List<String> imgList; // 图片url； 服务器地址
    public String itineraryDate;// 时间
    //    public int storageState; //0-服务器，1-本地
    public List<String> imgLocalPathList; // 本地地址

    public boolean imgChanged = false;

    @Override
    public boolean equals(Object obj) {
        if (obj == null && TextUtils.isEmpty(this.itineraryContent)) return true;
        if (!ItineraryBean.class.isInstance(obj)) {
            return false;
        }
        ItineraryBean it = (ItineraryBean) obj;
        boolean b = !TextUtils.isEmpty(it.itineraryContent) && it.itineraryContent.equals(this.itineraryContent) || TextUtils.isEmpty(it.itineraryContent) && TextUtils.isEmpty(this.itineraryContent);
        return b;

    }

    public ItineraryBean copyOne() {
        ItineraryBean itineraryBean = new ItineraryBean();
        itineraryBean.id = this.id;
        itineraryBean.itineraryContent = this.itineraryContent;
        itineraryBean.staffId = this.staffId;
        itineraryBean.itineraryDate = this.itineraryDate;
        itineraryBean.imgList = new ArrayList<>();
        if (this.imgList != null) {
            itineraryBean.imgList.addAll(this.imgList);
        }
        itineraryBean.imgLocalPathList = new ArrayList<>();
        if (this.imgLocalPathList != null) {
            itineraryBean.imgLocalPathList.addAll(this.imgLocalPathList);
        }
        return itineraryBean;
    }
}
