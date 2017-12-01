package com.jqyd.yuerduo.bean;

/**
 * 审批内容
 * Created by jianhaojie on 2017/1/17.
 */
public class WorkFlowBean extends BaseBean{
    public int memberId;//id
    public String actorName; //审批人
    public String operation;//提交、同意、驳回、转发、待审批
    public long actTime;//审批时间
    public String message;//审批内容
}
