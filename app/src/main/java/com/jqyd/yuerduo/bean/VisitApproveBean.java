package com.jqyd.yuerduo.bean;

/**
 * 拜访审核列表
 * Created by gjc on 2017/3/28.
 */

public class VisitApproveBean extends BaseBean {
    public int id;
    public String storeName;//门店名称
    public String visitorName;//拜访者名字
    public long visitStartTime;//拜访开始时间
    public int state;//审核状态，0未审核，1已审核（合格）2已审核（不合格）
}
