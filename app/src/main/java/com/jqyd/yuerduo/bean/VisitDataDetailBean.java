package com.jqyd.yuerduo.bean;

import java.io.Serializable;

/**
 * Created by zhangfan on 2016/12/1 0001.
 */

public class VisitDataDetailBean implements Serializable {

    public long visitDate;//本次拜访时间
    public long visitPreDate; //上次拜访时间
    public int approveType; // 0.未审核 1.合格，2.不合格
    public long approveTime; // 审核时间
    public String remark; // 审核 具体说明
    public String visitPreSummary;//上次拜访小结

    public long visitEndDate;// 本次拜访结束时间
    public String approverName;//审核人姓名
    public boolean isPush;//是否推送 true 推送 false 不推送
    public String visitorName; // 拜访者名字
}
