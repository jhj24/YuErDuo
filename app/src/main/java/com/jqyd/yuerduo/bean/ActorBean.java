package com.jqyd.yuerduo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 下级审批人员列表
 * Created by jianhaojie on 2017/1/18.
 */

public class ActorBean extends BaseTreeBean {
    public int id; //唯一id
    public String name; //名称
    public int isStaff;//级别
    public String phone;//电话
    public String Pid;//路径
    public List<ActorBean> children;

    public void setChildrenLevel() {
        if (children == null) return;
        for (ActorBean bean :
                children) {
            bean.levels = levels + 1;
        }
    }

    public List<ActorBean> getAllShowedChildren() {
        List<ActorBean> result = new ArrayList<>();
        if (children != null) {
            result.addAll(children);
            for (ActorBean node : children) {
                result.addAll(node.getAllShowedChildren());
            }
        }

        return result;
    }

    public List<ActorBean> getAllShowedChildren(boolean shown) {
        List<ActorBean> result = new ArrayList<>();
        if (isShowChildren == shown) {
            result.addAll(children);
            for (ActorBean node : children) {
                result.addAll(node.getAllShowedChildren(shown));
            }
        }
        return result;
    }

    public void setShowChildren(boolean showChildren) {
        this.isShowChildren = showChildren;
        if (showChildren) return;
        for (ActorBean node : children) {
            node.setShowChildren(false);
        }
    }
}
