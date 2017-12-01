package com.jqyd.yuerduo.bean;

import java.io.Serializable;
import java.util.List;

public class LeaveBean extends BaseBean implements Serializable {

    public String id;
    public String reason;//请假原因
    public String startDate;//开始日期
    public String endDate;//结束日期
    public int leaveDayNum; //请假天数
    public int leaveHourNum; //请假时长
    public int leaveType;//请假类型
    public String leaveTypeName;//请假类型
    public int state;//当前状态 0-待审批，1-已同意，2-已驳回
    public List<AttachmentBean> attachmentList;//附件类列表
    public boolean editable;//是否可以修改
    public long createTime;//请假提交时间
    public String creatorName;//申请人名字
    public List<WorkFlowBean> workFlowList;//审批流程记录列表
    public String nextActorName;//下级审批人
    public String nextActorId;//下级审批人
    public int examinePermissions;//1同意，2驳回，4转发，拥有多个权限则相加，如：7代表拥有全部审批操作权限，0无任何审批权限
    public boolean revocable;//是否可删除

}

