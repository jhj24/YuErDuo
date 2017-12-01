package com.jqyd.yuerduo.bean;

/**
 * 拜访记录
 * Created by gic on 2017/2/17.
 */

public class VisitRecordBean extends BaseBean {
    public int id;
    public String storeName;//门店名称
    public long visitTime;//拜访时间
    public int state;//审核状态，0未审核，1已审核（合格）2已审核（不合格）
}
