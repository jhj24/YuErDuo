package com.jqyd.yuerduo.bean;


public class CustomerDistributionBean extends BaseBean {

    public String customerID; //客户id
    public String customerName;//客户名称
    public String customerAddress;//客户地址
    public String areaName;//已分配销售区域名称
    public String areaID;//已分配销售区域id
    public ChannelRelationBean channelRelation;//客户信息
    public int state;//分配状态 0未分配，1已分配

    public boolean checked = false;

}

