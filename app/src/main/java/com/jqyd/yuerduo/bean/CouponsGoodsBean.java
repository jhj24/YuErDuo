package com.jqyd.yuerduo.bean;

/**
 * 返现券或返物券中包含商品的数据类
 */

public class CouponsGoodsBean extends BaseBean {

    public String id;// 行标识
    public String goodsName; //商品名字
    public String goodsImage; //商品图片
    public String goodsSpec; //商品规格(返物劵)
    public int counts; //商品总数量(返物劵)
    public int reNum; //每次使用数量(返物劵)
    public double goodsPrice; //商品价格(返现券)
    public int goodsSelectMaxNum; //商品最大可选数量(返现券)
    public int goodsNum;//选择的商品数量(返现劵)
    public boolean bag;//商品是否为礼包

    //local
    public boolean checked; //返物劵判断该商品是否选中
}
