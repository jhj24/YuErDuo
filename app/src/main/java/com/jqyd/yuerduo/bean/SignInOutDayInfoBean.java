package com.jqyd.yuerduo.bean;

/**
 * 签到信息,天情况
 * Created by zhangfan on 16-8-24.
 */
public class SignInOutDayInfoBean {
    public int type;//1:一次签到签退；2：两次签到签退...
    public SignInOutDayInfoType signIn1;//签到状态 0无签到签退信息，1正常签到签退，2迟到早退，3旷工，5请假，6出差
    public SignInOutDayInfoType signOut1;//签退状态
    public SignInOutDayInfoType signIn2;//(第二次签到)
    public SignInOutDayInfoType signOut2;//（第二次签退）

}
