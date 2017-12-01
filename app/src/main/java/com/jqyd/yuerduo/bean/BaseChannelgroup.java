package com.jqyd.yuerduo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 渠道分组bean
 * Created by jianhaojie on 2016/11/3.
 */
public class BaseChannelgroup extends BaseTreeBean {
    public int id; //唯一id
    public String name; //名称
    public int org;//渠道id
    public int sort;//顺序
    public int pid;//父id
    public String pids;//pids
    public String ordernumber;//序号
    public String memo;//备注
    public String code;//编号
    public List<BaseChannelgroup> children;
    public List<BaseChannelgroup> baseChannelgroupList;


    public void setChildren(List<BaseChannelgroup> baseChannelgroupList) {
        this.children = baseChannelgroupList;
    }
    public List<BaseChannelgroup> getChildren() {
        return baseChannelgroupList;
    }

    public List<BaseChannelgroup> getAllShowedChildren() {
        List<BaseChannelgroup> result = new ArrayList<>();
        result.addAll(baseChannelgroupList);
        for (BaseChannelgroup node : baseChannelgroupList) {
            result.addAll(node.getAllShowedChildren());
        }
        return result;
    }

    public List<BaseChannelgroup> getAllShowedChildren(boolean shown) {
        List<BaseChannelgroup> result = new ArrayList<>();
        if (isShowChildren == shown) {
            result.addAll(baseChannelgroupList);
            for (BaseChannelgroup node : baseChannelgroupList) {
                result.addAll(node.getAllShowedChildren(shown));
            }
        }
        return result;
    }

    public void setShowChildren(boolean showChildren) {
        this.isShowChildren = showChildren;
        if (showChildren) return;
        for (BaseChannelgroup node : baseChannelgroupList) {
            node.setShowChildren(false);
        }
    }

}
