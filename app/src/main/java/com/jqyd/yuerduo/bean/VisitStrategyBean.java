package com.jqyd.yuerduo.bean;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangfan on 2016/12/1 0001.
 */
public class VisitStrategyBean implements Serializable {
    public Integer id;
    public String strategyId;        // 策略编号
    public String strategyName;        // 策略名称
    public boolean checkPosition;        // 是否地点检测
    public Integer range1;        // 地点误差范围
    public String memo;        // 备注
    public String isUse;        // 是否启用
    public List<VisitItem> visitList;//拜访项列表

    public static class VisitItem implements Serializable {
        public String title;//标题
        public boolean necessary;//是否是必需的
        public HashMap billDefine;//单据

        /**
         * 本地字段，拜访项已完成
         */
        public boolean finished;//已完成
    }

}
