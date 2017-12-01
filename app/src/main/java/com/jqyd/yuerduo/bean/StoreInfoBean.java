package com.jqyd.yuerduo.bean;

/**
 * 客户采集根据营业执照返回的信息
 * Created by jianhaojie on 2017/3/15.
 */

public class StoreInfoBean extends BaseBean {

    public boolean exist;//是否存在激活用户

    //已激活的客户增加返回字段，回显到界面
    public int channelId;
    public String companyName;// 客户名称
    public String province;//省
    public String city;//市
    public String district;// 区县
    public String address;// 联系地址
    public String lat;//
    public String lon;//

}
