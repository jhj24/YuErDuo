package com.jqyd.yuerduo.bean;

import com.jqyd.yuerduo.constant.FunctionName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gjc on 2017/5/5.
 */

public class FunctionNumBean implements Serializable {
    public int visitApproveNum;//拜访审核
    public int leaveApproveNum;//请假审批
    public int travelApproveNum;// 差旅审批
    public int askApproveNum;// 请示审批

    public int myMessageMum;//消息通知

    public int myVisitRecordNum;// 拜访历史
    public int myLeaveNum;//我的请假
    public int myTravelNum;// 我的差旅
    public int myAskNum;// 我的请示

    public List<String> leaveIdList = new ArrayList<>();//我的请假 id
    public List<String> travelIdList = new ArrayList<>();//我的差旅 id
    public List<String> askIdList = new ArrayList<>();// 我的请示 id
    public List<String> visitIdList = new ArrayList<>();// 拜访历史 id

    /**
     * 计算总数
     *
     * @param dataList
     * @return
     */
    public int sum(ArrayList<FunctionBean> dataList) {
        int sum = 0;
        for (FunctionBean bean : dataList) {
            if (bean.funcName.equals(FunctionName.VisitApprove)) {
                sum += visitApproveNum;
            } else if (bean.funcName.equals(FunctionName.VisitRecord)) {
                sum += myVisitRecordNum;
            } else if (bean.funcName.equals(FunctionName.LeaveList)) {
                if (bean.getType().equals("0")) {
                    sum += myLeaveNum;
                } else if (bean.getType().equals("1")) {
                    sum += leaveApproveNum;
                }
            } else if (bean.funcName.equals(FunctionName.TravelList)) {
                if (bean.getType().equals("0")) {
                    sum += myTravelNum;
                } else if (bean.getType().equals("1")) {
                    sum += travelApproveNum;
                }
            } else if (bean.funcName.equals(FunctionName.AskList)) {
                if (bean.getType().equals("0")) {
                    sum += myAskNum;
                } else if (bean.getType().equals("1")) {
                    sum += askApproveNum;
                }
            } else if (bean.funcName.equals(FunctionName.MessageList)) {
                sum += myMessageMum;
            }
        }
        return sum;
    }
}
