package com.jqyd.yuerduo.bean;

/**
 * Created by liushiqi on 2017/3/14,0014.
 */

public class SignMonthMessageBean {
    //1,正常签到 2正常签退,3非正常签到,4非正常签退,5迟到,6早退,7请假,8出差,9,旷工
    public int type;//类型标示
    public int num;//类型考勤数据
    public String unit;//单位，如 天/次 等
}
