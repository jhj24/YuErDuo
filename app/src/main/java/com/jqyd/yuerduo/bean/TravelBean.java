package com.jqyd.yuerduo.bean;

import java.util.List;

public class TravelBean extends BaseBean {
    public int id;        // id
    public String startPlace;        // 出发地
    public String endPlace;        // 目的地
    public int state;        // 差旅状态   0-待审核    1-已同意    2-已驳回
    public String startDate;        // 出差开始时间
    public String endDate;        // 出差结束时间
    public boolean revocable;//是否可以取消
    public boolean editable;//是否可以修改
    public int trafficType;// 1-火车，2-汽车，3-轮船，4-飞机，5-自驾6-其他
    public String trafficTypeName;//差旅类型名称
    public String creatorName;        // 出差人名称
    public String nextActorName;//下级审批人
    public String nextActorId;//下级审批人
    public List<WorkFlowBean> workFlowList;//审批流程记录列表
    public String reason;//出差原因
    public long createTime;    // 申请时间
    public int examinePermissions;//1同意，2驳回，4转发，拥有多个权限则相加，如：7代表拥有全部审批操作权限，0无任何审批权限
    public List<AttachmentBean> attachmentList;//附件类列表
}
