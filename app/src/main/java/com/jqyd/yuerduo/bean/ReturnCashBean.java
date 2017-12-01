package com.jqyd.yuerduo.bean;

import java.util.List;

/**
 * 返现券Bean
 * Created by jhj on 17-10-25.
 */

public class ReturnCashBean extends BaseBean {
    public String id; // 唯一标示
    public String name; // 名称
    public int cashType; // 返现券类型（	1-充值，2-预付，3-返现）
    public double balance; // 剩余金额
    public long startTimeDate; //开始时间
    public long endTimeDate; //结束时间
    public double limitAmount; //条件金额
    public boolean multipleUse; // 是否可以多次使用
    public double usePercent; //可用订单比例
    public boolean isSpecialPrice; //特价商品是否可用
    public List<CouponsGoodsBean> goodsList; //商品信息

    //local
    public List<CouponsGoodsBean> selectGoodsList; //选择后的商品列表
}
