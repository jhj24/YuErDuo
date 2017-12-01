package com.jqyd.yuerduo.bean;

/**
 * 位置上报策略信息
 * <p>
 * Created by jhj on 17-8-24.
 */

public class LocationStrategy extends BaseBean {
    /**
     * 上次获取策略时间
     */
    public Long lastGetStrategyTime;
    /**
     * 位置上报开始时间
     */
    public String startTime;
    /**
     * 位置上报结束时间
     */
    public String endTime;
    /**
     * 上报间隔
     */
    public int interval;

}
