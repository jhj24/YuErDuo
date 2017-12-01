package com.jqyd.yuerduo.bean;

/**
 * Created by zhangfan on 2016/1/29.
 */
public class UserBean extends BaseBean {

    /**
     * memberName : 15736990173
     * noFilledOrder : 0
     * noReceiveOrder : 0
     * memberSex : 1
     * favStoreCount : 0
     * finishOrder : 0
     * availablePredeposit : 0.00
     * favGoodsCount : 0
     * memberAvatar : /upload/img/avatar/01.jpg
     * memberNameCode : 15736990173
     * noPayOrder : 0
     * memberConsumePoints : 50
     * memberId : 333
     */

    private String memberName;
    private String password;
    private int noFilledOrder;
    private int noReceiveOrder;
    private int favStoreCount;
    private int finishOrder;
    private String availablePredeposit;
    private int favGoodsCount;
    private String memberAvatar;
    private String memberNameCode;
    private int noPayOrder;
    private String memberConsumePoints;
    private int memberId;
    private int storeId;
    private String storeName;
    private String memberTruename;
    private int memberSex;//1-男  2-女
    private String companyName;// 该账号所属 公司名称
    private LocationStrategy locationStrategy; //位置上报策略

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getMemberSex() {
        return memberSex;
    }

    public void setMemberSex(int memberSex) {
        this.memberSex = memberSex;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getMemberTruename() {
        return memberTruename;
    }

    public void setMemberTruename(String memberTruename) {
        this.memberTruename = memberTruename;
    }


    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setNoFilledOrder(int noFilledOrder) {
        this.noFilledOrder = noFilledOrder;
    }

    public void setNoReceiveOrder(int noReceiveOrder) {
        this.noReceiveOrder = noReceiveOrder;
    }

    public void setFavStoreCount(int favStoreCount) {
        this.favStoreCount = favStoreCount;
    }

    public void setFinishOrder(int finishOrder) {
        this.finishOrder = finishOrder;
    }

    public void setAvailablePredeposit(String availablePredeposit) {
        this.availablePredeposit = availablePredeposit;
    }

    public void setFavGoodsCount(int favGoodsCount) {
        this.favGoodsCount = favGoodsCount;
    }

    public void setMemberAvatar(String memberAvatar) {
        this.memberAvatar = memberAvatar;
    }

    public void setMemberNameCode(String memberNameCode) {
        this.memberNameCode = memberNameCode;
    }

    public void setNoPayOrder(int noPayOrder) {
        this.noPayOrder = noPayOrder;
    }

    public void setMemberConsumePoints(String memberConsumePoints) {
        this.memberConsumePoints = memberConsumePoints;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMemberName() {
        return memberName;
    }

    public int getNoFilledOrder() {
        return noFilledOrder;
    }

    public int getNoReceiveOrder() {
        return noReceiveOrder;
    }

    public int getFavStoreCount() {
        return favStoreCount;
    }

    public int getFinishOrder() {
        return finishOrder;
    }

    public String getAvailablePredeposit() {
        return availablePredeposit;
    }

    public int getFavGoodsCount() {
        return favGoodsCount;
    }

    public String getMemberAvatar() {
        return memberAvatar;
    }

    public String getMemberNameCode() {
        return memberNameCode;
    }

    public int getNoPayOrder() {
        return noPayOrder;
    }

    public String getMemberConsumePoints() {
        return memberConsumePoints;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocationStrategy getLocationStrategy() {
        return locationStrategy;
    }

    public void setLocationStrategy(LocationStrategy locationStrategy) {
        this.locationStrategy = locationStrategy;
    }
}
