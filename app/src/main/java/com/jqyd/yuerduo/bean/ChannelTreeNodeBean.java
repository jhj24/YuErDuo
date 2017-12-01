package com.jqyd.yuerduo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lll on 2016/3/17.
 * 渠道树形
 */
public class ChannelTreeNodeBean extends BaseTreeBean {
    private String id;
    private String name;
    private String PId;
    private String isChannel;//1 渠道
    private String tel;
    private int channelMemberId;
    private List<ChannelTreeNodeBean> children;


    @Override
    public boolean equals(Object o) {
        return ChannelTreeNodeBean.class.isInstance(o) && this.id.equals(((ChannelTreeNodeBean) o).id);
    }

    public void setChildrenLevel() {
        if (children == null) return;
        for (ChannelTreeNodeBean bean : children) {
            bean.levels = levels + 1;
        }
    }


    public Boolean isChannel() {
        return "1".equals(isChannel);
    }

    public int setChildrenChecked(boolean checked) {
        if (children == null) return 0;
        int count = children.size();
        for (ChannelTreeNodeBean bean : children) {
            bean.isChecked = checked;
            count += bean.setChildrenChecked(checked);
        }
        return count;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setIsChannel(String isChannel) {
        this.isChannel = isChannel;
    }


    public void setShowChildren(boolean showChildren) {
        this.isShowChildren = showChildren;
        if (showChildren) return;
        for (ChannelTreeNodeBean node : getChildren()) {
            node.setShowChildren(false);
        }
    }


    public List<ChannelTreeNodeBean> getChildren() {
        return children == null ? new ArrayList<ChannelTreeNodeBean>() : children;
    }

    public List<ChannelTreeNodeBean> getAllShowedChildren(boolean shown) {
        List<ChannelTreeNodeBean> result = new ArrayList<>();
        if (isShowChildren == shown) {
            result.addAll(getChildren());
            for (ChannelTreeNodeBean node : getChildren()) {
                result.addAll(node.getAllShowedChildren(shown));
            }
        }
        return result;
    }

    public List<ChannelTreeNodeBean> getAllShowedChildren() {
        List<ChannelTreeNodeBean> result = new ArrayList<>();
        result.addAll(getChildren());
        for (ChannelTreeNodeBean node : getChildren()) {
            result.addAll(node.getAllShowedChildren());
        }
        return result;
    }

    public void setChildren(List<ChannelTreeNodeBean> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPId() {
        return PId;
    }

    public void setPId(String PId) {
        this.PId = PId;
    }

    public int getChannelMemberId() {
        return channelMemberId;
    }

    public void setChannelMemberId(int channelMemberId) {
        this.channelMemberId = channelMemberId;
    }
}
