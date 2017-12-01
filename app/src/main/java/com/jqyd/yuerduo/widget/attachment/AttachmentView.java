package com.jqyd.yuerduo.widget.attachment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.jqyd.yuerduo.bean.AttachmentBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gjc on 2017/2/6.
 */

public class AttachmentView extends FrameLayout {
    private boolean editable = true;//是否可编辑
    private int maxNum = 0;//附件最大数量

    private List<File> fileList = new ArrayList<File>(); //添加的文件列表
    private List<AttachmentBean> attachList = new ArrayList<AttachmentBean>();//下载附件列表
    private AttachmentLayout attachmentLayout;
    private HashMap<String, Object> params = new HashMap<String, Object>();

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
        params.put("maxNum", "" + maxNum);
        attachmentLayout.setParams(params);
    }

    public List<AttachmentBean> getAttachList() {
        attachList = attachmentLayout.getAttachList();
        return attachList;
    }

    public void setAttachList(List<AttachmentBean> attachList) {
        this.attachList = attachList;
        attachmentLayout.setAttachList((ArrayList<AttachmentBean>) attachList);
        attachmentLayout.setEditable(isEditable());//设置attachList后需赋值editable，attachmentLayout中进行附件布局时需要editable值
    }

    public List<File> getFileList() {
        fileList = attachmentLayout.getFileList();
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        attachmentLayout.setEditable(editable);
    }

    public AttachmentView(Context context) {
        super(context);
        initView(context, null);
    }

    public AttachmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public AttachmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        attachmentLayout = new AttachmentLayout(context, attrs, 0);
        this.addView(attachmentLayout);
        attachmentLayout.setEditable(editable);
    }

}
