package com.jqyd.yuerduo.bean;

/**
 * 我的状态
 * Created by zhangfan on 16-8-26.
 */
public class PersonStateBean {
    public int type;//1:一次签到签退；2：两次签到签退...

    public String signIn1;//签到状态
    public String signOut1;//签退状态
    public String signIn2;//(第二次签到)
    public String signOut2;//（第二次签退）
}
