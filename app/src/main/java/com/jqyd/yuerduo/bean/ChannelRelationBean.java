package com.jqyd.yuerduo.bean;

import java.io.Serializable;

/**
 * Created by zhangfan on 2016/12/1 0001.
 */

public class ChannelRelationBean extends CustomerBean implements Serializable {

    public String groupName;//分组名称
    public String attrName;//属性名称
    public String channelSystemName;//客户系统名称
    public String channelTypeName;//客户类型名称
    public Integer isAgree;// 是否同意
    public String bakName;// 备注名称
    public String channelCompanyName;//客户公司名称
    public String companyAddress;//公司地址
    public String companyAddressDetail;//详细地址
    public String companyPhone;//公司电话
    public String contactsName;//联系人名称
    public String contactsPhone;//联系人电话
    public String contactsEmail;//联系人邮箱
    public String bak;//备注
    public int channelid;//客户id

    public String fixedTelephone;//固定电话
    public String businessLicenceNumber;//营业执照号码
    public String creator;//创建人


    public boolean visitFinish;//拜访完成


    /**
     * 本地字段，门店距离
     */
    public double distance = -1;
}
