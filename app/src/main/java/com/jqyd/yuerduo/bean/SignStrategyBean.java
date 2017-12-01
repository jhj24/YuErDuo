package com.jqyd.yuerduo.bean;

import java.util.List;

/**
 * 考勤策略
 * Created by zhangfan on 16-8-8.
 */
public class SignStrategyBean {
    /**
     * 是否需要选择签到地点
     */
    public boolean needSelectArea;
    /**
     * 需要拍照数量
     */
    public int needImgNum;
    /**
     * 是否需要经纬度
     */
    public boolean needLatLon;
    /**
     * 考勤地点数据
     */
    public List<AttendanceLocation> area;
    /**
     * 是否已经签过到
     */
    public boolean record;
    /**
     *
     */
    public String msg;
    /**
     * 误差范围
     */
    public  int range;
    /**
     * 策略id
     */
    public  int strategyId;

}
