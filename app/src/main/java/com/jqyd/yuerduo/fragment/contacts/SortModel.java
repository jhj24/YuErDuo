package com.jqyd.yuerduo.fragment.contacts;

import com.jqyd.yuerduo.bean.CustomerBean;
import com.jqyd.yuerduo.bean.UserBean;

import java.io.Serializable;

public class SortModel extends CustomerBean {

    public int type;//0员工, 1渠道
    //	private String name; // 显示的数据
    private String sortLetters; // 显示数据拼音的首字母


    // 员工独有属性
    /**
     * 员工id
     */
    private Integer staffId;
    /**
     * 员工姓名
     */
    private String staffName;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 电话号码
     */
    private String phone;

    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 帐号id
     */
    private Integer sellerId;

    /**
     * 员工号
     */
    private String staffNum;
    /**
     * 部门id
     */
    private Integer dept;
    /**
     * 管理范围
     */
    private String manageScope;
    /**
     * 性别
     */
    private String sex;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPhone() {
        return phone != null ? phone : getMemberName();
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getStaffNum() {
        return staffNum;
    }

    public void setStaffNum(String staffNum) {
        this.staffNum = staffNum;
    }

    public Integer getDept() {
        return dept;
    }

    public void setDept(Integer dept) {
        this.dept = dept;
    }

    public String getManageScope() {
        return manageScope;
    }

    public void setManageScope(String manageScope) {
        this.manageScope = manageScope;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        if (type == 1) {
            return getStoreName();
        } else {
            return getStaffName();
        }
    }

    public void setName(String name) {
        if (type == 1) {
            setStoreName(name);
        } else {
            setStaffName(name);
        }

    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
