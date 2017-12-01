package com.jqyd.yuerduo.bean;

import java.util.List;

/**
 * 返物券Bean
 * Created by jhj on 17-10-25.
 */

public class ReturnGoodsBean extends BaseBean {
    public String id; // 唯一标示
    public String name; // 名称
    public long startTimeDate; //开始时间
    public long endTimeDate; //结束时间
    public double limitAmount; //条件金额
    public boolean multipleUse; // 是否可以多次使用
    public List<CouponsGoodsBean> goodsList; //商品信息

    //local
    public List<CouponsGoodsBean> selectGoodsList; //选择后的商品列表
}
