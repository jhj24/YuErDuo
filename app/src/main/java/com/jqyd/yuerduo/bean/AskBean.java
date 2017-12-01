package com.jqyd.yuerduo.bean;

import java.util.List;

/**
 * 请示审批列表
 * Created by jianhaojie on 2017/1/17.
 */

public class AskBean extends BaseBean {
    public int id; //id
    public String title; //提交请求的标题
    public long createTime; //请求提交的时间
    public String creatorName; //请求的提交人
    public int state; //当前状态，0-待审批，1-已同意，2-已驳回
    public String content; //请求的内容
    public List<AttachmentBean> attachmentList;//附件类列表
    public List<WorkFlowBean> workFlowList;//审批流程记录列表
    public String nextActorName;//下级审批人
    public int nextActorId;//下级审批人id
    public int examinePermissions;//1同意，2驳回，4转发，拥有多个权限则相加，如：7代表拥有全部审批操作权限，0无任何审批权限
    public boolean editable;//是否可修改
    public boolean revocable;//是否可删除
    public int instructionType;   //请示类型id
    public String instructionTypeName;//请示类型名称
}
