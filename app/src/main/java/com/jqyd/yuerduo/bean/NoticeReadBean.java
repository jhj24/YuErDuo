package com.jqyd.yuerduo.bean;

/**
 * Created by lll on 2016/3/21.
 */
public class NoticeReadBean extends BaseBean {
    /**
     * "isread": 1,
     * "readtimeStr": "2016-03-15 16:56:55",
     * "readtime": 0,
     * "staffname": "盼盼"
     */
    public String isread;//1=已阅读

    public long readtime;

    public String staffname;

    public String readtimeStr;

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public long getReadtime() {
        return readtime;
    }

    public void setReadtime(long readtime) {
        this.readtime = readtime;
    }

    public String getStaffname() {
        return staffname;
    }

    public void setStaffname(String staffname) {
        this.staffname = staffname;
    }

    public String getReadtimeStr() {
        return readtimeStr;
    }

    public void setReadtimeStr(String readtimeStr) {
        this.readtimeStr = readtimeStr;
    }
}
