package com.jqyd.yuerduo.bean;

/**
 * Created by zhangfan on 2016/3/9.
 */
public class CustomerAddressBean extends BaseBean {

    private String memberId;//Integer	否		会员id
    private String areaId;    //Integer	否		区id
    private String telPhone;//	String	是		座机电话
    private String isDefault;   //Integer	否		是否是默认地址 1是默认，0相反
    private String trueName; //Integer	否		收货人名字
    private String areaInfo;//String	是		省市区字符串
    private String addressId;//Integer	是		主键
    private String provinceId;//	Integer	否		省id
    private String cityId;//String	是		城市id
    private String mobPhone;//	Integer	否		手机
    private String address;    //Integer	是		地址
    private String zipCode;//	String	否		邮编

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getTelPhone() {
        return telPhone;
    }

    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getAreaInfo() {
        return areaInfo;
    }

    public void setAreaInfo(String areaInfo) {
        this.areaInfo = areaInfo;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getMobPhone() {
        return mobPhone;
    }

    public void setMobPhone(String mobPhone) {
        this.mobPhone = mobPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
