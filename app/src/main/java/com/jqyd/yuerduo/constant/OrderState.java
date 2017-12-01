package com.jqyd.yuerduo.constant;

import java.util.HashMap;

/**
 * Created by zhangfan on 2016/3/21.
 */
public class OrderState {

    private static final HashMap<Integer, String> StateMap = new HashMap<>();

    static {
        StateMap.put(0, "已驳回");
        StateMap.put(5, "已取消");
        StateMap.put(10, "待支付");
        StateMap.put(20, "待发货");
//        StateMap.put(24, "待配送");
        StateMap.put(25, "配送中");
        StateMap.put(30, "待签收");
        StateMap.put(40, "已完成");
        StateMap.put(50, "待审批");
        StateMap.put(60, "已确认");
        StateMap.put(100, "全部");
    }

    public static String getState(int state){
        String stateName = StateMap.get(state);
        if (stateName==null){
            return "未知状态";
        }
        return stateName;
    }
}
