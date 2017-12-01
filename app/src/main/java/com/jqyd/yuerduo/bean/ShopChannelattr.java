package com.jqyd.yuerduo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 渠道属性bean
 * Created by jianhaojie on 2016/11/3.
 */
public class ShopChannelattr extends BaseTreeBean {
    public int id; //唯一id
    public String name; //名称
    public int storeid;//商店id
    public int sort;//排列序号
    public int pid;//上级分组
    public String pids;//路径
    public String memo;//备注
    public List<ShopChannelattr> shopChannelattrList;


    public List<ShopChannelattr> getAllShowedChildren() {
        List<ShopChannelattr> result = new ArrayList<>();
        result.addAll(shopChannelattrList);
        for (ShopChannelattr node : shopChannelattrList) {
            result.addAll(node.getAllShowedChildren());
        }
        return result;
    }

    public List<ShopChannelattr> getAllShowedChildren(boolean shown) {
        List<ShopChannelattr> result = new ArrayList<>();
        if (isShowChildren == shown) {
            result.addAll(shopChannelattrList);
            for (ShopChannelattr node : shopChannelattrList) {
                result.addAll(node.getAllShowedChildren(shown));
            }
        }
        return result;
    }

    /**
     * 当参数为false时，遍历list
     * 当参数为true时，直接return
     *
     * @param showChildren boolean
     */
    public void setShowChildren(boolean showChildren) {
        this.isShowChildren = showChildren;
        if (showChildren) return;
        for (ShopChannelattr node : shopChannelattrList) {
            node.setShowChildren(false);
        }
    }
}
