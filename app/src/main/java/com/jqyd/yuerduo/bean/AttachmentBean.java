package com.jqyd.yuerduo.bean;

/**
 * 附件
 * Created by gjc on 2017/1/9.
 */

public class AttachmentBean extends BaseBean {
    /**
     * 附件名称
     */
    public String fileName;
    /**
     * 附件URL
     */
    public String fileUrl;
    /**
     * 附件大小（字节）
     */
    public int fileSize;

    @Override
    public boolean equals(Object obj) {
        return AttachmentBean.class.isInstance(obj) && ((AttachmentBean) obj).fileUrl != null && ((AttachmentBean) obj).fileUrl.equals(fileUrl);
    }

}
