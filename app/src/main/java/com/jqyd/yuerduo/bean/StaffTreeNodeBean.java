package com.jqyd.yuerduo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangfan on 2016/3/17.
 */
public class StaffTreeNodeBean extends BaseTreeBean {
    private String id;
    private String name;
    private String pId;
    private String isStaff;//1员工
    private String phone;
    private List<StaffTreeNodeBean> children;


    @Override
    public boolean equals(Object o) {
        return StaffTreeNodeBean.class.isInstance(o) && this.id.equals(((StaffTreeNodeBean) o).id) && this.name.equals(((StaffTreeNodeBean) o).name);
    }

    public void setChildrenLevel() {
        if (children == null) return;
        for (StaffTreeNodeBean bean : children) {
            bean.levels = levels + 1;
        }
    }

    public Boolean isStaff() {
        return "1".equals(isStaff);
    }

    public int setChildrenChecked(boolean checked) {
        if (children == null) return 0;
        int count = children.size();
        for (StaffTreeNodeBean bean : children) {
            bean.isChecked = checked;
            count += bean.setChildrenChecked(checked);
        }
        return count;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }


    public void setShowChildren(boolean showChildren) {
        this.isShowChildren = showChildren;
        if (showChildren) return;
        for (StaffTreeNodeBean node : getChildren()) {
            node.setShowChildren(false);
        }
    }



    public List<StaffTreeNodeBean> getChildren() {
        return children == null ? new ArrayList<StaffTreeNodeBean>() : children;
    }

    public List<StaffTreeNodeBean> getAllShowedChildren(boolean shown) {
        List<StaffTreeNodeBean> result = new ArrayList<>();
        if (isShowChildren == shown) {
            result.addAll(getChildren());
            for (StaffTreeNodeBean node : getChildren()) {
                result.addAll(node.getAllShowedChildren(shown));
            }
        }
        return result;
    }

    public List<StaffTreeNodeBean> getAllShowedChildren() {
        List<StaffTreeNodeBean> result = new ArrayList<>();
        result.addAll(getChildren());
        for (StaffTreeNodeBean node : getChildren()) {
            result.addAll(node.getAllShowedChildren());
        }
        return result;
    }

    public void setChildren(List<StaffTreeNodeBean> children) {
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

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }


}
