package com.jqyd.yuerduo.bean;

/**
 * Created by lll on 2016/3/21.
 */
public class ChannelNoticeReadBean extends BaseBean {


    public String isread;//1=已阅读

    public long readTime;

    public String channelName;

    public String updateTimeStr;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public String getUpdateTimeStr() {
        return updateTimeStr;
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }
}
