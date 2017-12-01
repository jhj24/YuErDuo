package com.jqyd.yuerduo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 渠道系统
 * Created by jianhaojie on 2016/11/9.
 */

public class QuDaoSystem extends BaseTreeBean {
    public int id; //唯一id
    public String name; //名称
    public int pid;//父级id
    public int sort;//排序
    public String pids;//路径
    public int storeid;//企业id
    public String memo;//备注
    public List<QuDaoSystem> quDaoSystemList;


    public List<QuDaoSystem> getAllShowedChildren() {
        List<QuDaoSystem> result = new ArrayList<>();
        result.addAll(quDaoSystemList);
        for (QuDaoSystem node : quDaoSystemList) {
            result.addAll(node.getAllShowedChildren());
        }
        return result;
    }

    public List<QuDaoSystem> getAllShowedChildren(boolean shown) {
        List<QuDaoSystem> result = new ArrayList<>();
        if (isShowChildren == shown) {
            result.addAll(quDaoSystemList);
            for (QuDaoSystem node : quDaoSystemList) {
                result.addAll(node.getAllShowedChildren(shown));
            }
        }

        return result;
    }

    public void setShowChildren(boolean showChildren) {
        this.isShowChildren = showChildren;
        if (showChildren) return;
        for (QuDaoSystem node : quDaoSystemList) {
            node.setShowChildren(false);
        }
    }


}
