package com.jqyd.yuerduo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 渠道类别bean
 * Created by jianhaojie on 2016/11/9.
 */

public class QuDaoType extends BaseTreeBean {
    public int id; //唯一id
    public String name; //名称
    public int pid;//父级id
    public int sort;//排序
    public String pids;//路径
    public int storeid;//企业id
    public String memo;//备注
    public List<QuDaoType> quDaoTypeList;


    public List<QuDaoType> getAllShowedChildren() {
        List<QuDaoType> result = new ArrayList<>();
        result.addAll(quDaoTypeList);
        for (QuDaoType node : quDaoTypeList) {
            result.addAll(node.getAllShowedChildren());
        }
        return result;
    }

    public List<QuDaoType> getAllShowedChildren(boolean shown) {
        List<QuDaoType> result = new ArrayList<>();
        if (isShowChildren == shown) {
            result.addAll(quDaoTypeList);
            for (QuDaoType node : quDaoTypeList) {
                result.addAll(node.getAllShowedChildren(shown));
            }
        }
        return result;
    }

    public void setShowChildren(boolean showChildren) {
        this.isShowChildren = showChildren;
        if (showChildren) return;
        for (QuDaoType node : quDaoTypeList) {
            node.setShowChildren(false);
        }
    }
}
