package com.jqyd.yuerduo.bean;

import java.util.List;

/**
 * Created by zhangfan on 2016/3/7.
 * 商品
 */
public class GoodsBean extends BaseBean {
    /*
     * goodsId : 48
     * goodsStorePrice : 19.89
     * goodsImage : /upload/img/store/0/1456397439818.jpg
     * storeName : 河南中沃
     * goodsName : 体质能量维生素饮料盒装玛卡型
     */

    /**
     * goodsId : 63
     * goodsBarterNum : 0
     * isCustomer : 1
     * evaluationStatus : 0
     * gcId : 231
     * goodsPayPrice : 44
     * recId : 1097
     * buyerId : 666
     * goodsImage : /upload/img/store/0/1456397336362.jpg
     * goodsNum : 2
     * goodsReturnNum : 0
     * goodsPreAmount : 0
     * specId : 977
     * goodsPrice : 44
     * commisRate : 1
     * storeId : 175
     * orderId : 1084
     * goodsName : 老村长五谷生香
     * specInfo : 净含量:450ML&nbsp;酒精度:42%&nbsp;香型:浓香型&nbsp;
     */

    private double goodsStorePrice;
    private String storeName;
    private String cartIds;
    private int count;//下单数量

    private String goodsId;
    private int goodsBarterNum;
    private int isCustomer;
    private int evaluationStatus;
    private int gcId;
    private double goodsPayPrice;
    private int recId;
    private int buyerId;
    private String goodsImage;
    private int goodsNum;
    private int goodsReturnNum;
    private double goodsPreAmount;
    private int specId;
    private double goodsPrice;
    private int commisRate;
    private int storeId;
    private int orderId;
    private String goodsName;
    private String specInfo;

    private int realNum;//实发数量

    private int num;//送达操作参数
    private double totalPrice;//小计
    public String currenctUnit;

    public boolean gift;//商品是否为礼包
    public List<GoodsBean> giftGoodsList;//礼包中包含商品
    public int numOfGift;//一个礼包中包含改商品的数量

    public boolean checked = false;

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getRealNum() {
        return realNum;
    }

    public void setRealNum(int realNum) {
        this.realNum = realNum;
    }

    @Override
    public boolean equals(Object o) {
        return this.goodsId.equals(((GoodsBean) o).getGoodsId());
    }

    public int getGoodsBarterNum() {
        return goodsBarterNum;
    }

    public void setGoodsBarterNum(int goodsBarterNum) {
        this.goodsBarterNum = goodsBarterNum;
    }

    public int getIsCustomer() {
        return isCustomer;
    }

    public void setIsCustomer(int isCustomer) {
        this.isCustomer = isCustomer;
    }

    public int getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(int evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }

    public int getGcId() {
        return gcId;
    }

    public void setGcId(int gcId) {
        this.gcId = gcId;
    }

    public double getGoodsPayPrice() {
        return goodsPayPrice;
    }

    public void setGoodsPayPrice(double goodsPayPrice) {
        this.goodsPayPrice = goodsPayPrice;
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    public int getGoodsReturnNum() {
        return goodsReturnNum;
    }

    public void setGoodsReturnNum(int goodsReturnNum) {
        this.goodsReturnNum = goodsReturnNum;
    }

    public double getGoodsPreAmount() {
        return goodsPreAmount;
    }

    public void setGoodsPreAmount(double goodsPreAmount) {
        this.goodsPreAmount = goodsPreAmount;
    }

    public int getSpecId() {
        return specId;
    }

    public void setSpecId(int specId) {
        this.specId = specId;
    }

    public double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public int getCommisRate() {
        return commisRate;
    }

    public void setCommisRate(int commisRate) {
        this.commisRate = commisRate;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getSpecInfo() {
        return specInfo;
    }

    public void setSpecInfo(String specInfo) {
        this.specInfo = specInfo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCartIds() {
        return cartIds;
    }

    public void setCartIds(String cartIds) {
        this.cartIds = cartIds;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public void setGoodsStorePrice(double goodsStorePrice) {
        this.goodsStorePrice = goodsStorePrice;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public double getGoodsStorePrice() {
        return goodsStorePrice;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getGoodsName() {
        return goodsName;
    }
}
