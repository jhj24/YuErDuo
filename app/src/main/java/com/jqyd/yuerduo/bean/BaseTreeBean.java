package com.jqyd.yuerduo.bean;

/**
 * 树形数据基类
 * Created by jianhaojie on 2017/3/24.
 */

public class BaseTreeBean extends BaseBean {
    public int levels;//显示级别
    public int childrenSize; //是否有子类
    public String spelling; //名称首字符拼音
    public boolean isShowChildren;
    public boolean isChecked;
}
