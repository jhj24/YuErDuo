package com.jqyd.yuerduo.bean;

import java.util.List;

/**
 * Created by zhangfan on 2015/12/21.
 */
public class MessageBean extends BaseBean {

    public String noticetitle;
    public String content;
    /**
     * startDateStr : 0
     * org : 19
     * createTime : 1454328119790
     * sourcetype : 1
     * id : 28
     * createTimeStr : 2016-02-01 20:01:59
     * endDateStr : 0
     */

    public long startDateStr;
    public long endDateStr;
    public int org;
    public long createTime;
    public int sourcetype;
    public int id;
    public String createTimeStr;

    public String creator;
    public int isread;//0:未读  1:已读
    public int count;//总数量
    public int countReaded;//阅读数量
    public List<AttachmentBean> attachmentList;//附件集合
}
