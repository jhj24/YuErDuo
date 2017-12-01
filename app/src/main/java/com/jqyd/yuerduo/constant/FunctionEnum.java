package com.jqyd.yuerduo.constant;

import com.jqyd.yuerduo.activity.BaseActivity;
import com.jqyd.yuerduo.activity.ask.ActorTreeActivity;
import com.jqyd.yuerduo.activity.ask.AskListActivity;
import com.jqyd.yuerduo.activity.channel.ChannelNoticeListActivity;
import com.jqyd.yuerduo.activity.distribution.CustomerDistributionActivity;
import com.jqyd.yuerduo.activity.dues.DuesActivity;
import com.jqyd.yuerduo.activity.itinerary.ItineraryActivity;
import com.jqyd.yuerduo.activity.itinerary.ItineraryListActivity;
import com.jqyd.yuerduo.activity.leave.LeaveListActivity;
import com.jqyd.yuerduo.activity.message.MessageListActivity;
import com.jqyd.yuerduo.activity.order.OrderAddActivity;
import com.jqyd.yuerduo.activity.order.OrderDetailActivity;
import com.jqyd.yuerduo.activity.order.OrderListActivity;
import com.jqyd.yuerduo.activity.order.OrderManagerActivity;
import com.jqyd.yuerduo.activity.order.prize.PrizeOrderListActivity;
import com.jqyd.yuerduo.activity.receivables.ReceivablesActivity;
import com.jqyd.yuerduo.activity.restock.RestockActivity;
import com.jqyd.yuerduo.activity.sign.MySignStateActivity;
import com.jqyd.yuerduo.activity.sign.SignInActivity;
import com.jqyd.yuerduo.activity.sign.month.SignMonthActivity;
import com.jqyd.yuerduo.activity.staff.StaffNoticeListActivity;
import com.jqyd.yuerduo.activity.stock.StockActivity;
import com.jqyd.yuerduo.activity.store.StoreAddActivity;
import com.jqyd.yuerduo.activity.travel.TravelListActivity;
import com.jqyd.yuerduo.activity.visit.VisitApproveListActivity;
import com.jqyd.yuerduo.activity.visit.VisitCustomerListActivity;
import com.jqyd.yuerduo.activity.visit.VisitRecordListActivity;
import com.jqyd.yuerduo.extention.bill.SimpleBillActivity;
import com.jqyd.yuerduo.extention.bill.SimpleBillDataSelectActivity;

/**
 * 功能入口
 * Created by zhangfan on 2016/2/3.
 */
public enum FunctionEnum {

    MessageList(MessageListActivity.class),
    MyOrderList(OrderListActivity.class),
    OrderAdd(OrderAddActivity.class),
    OrderDetail(OrderDetailActivity.class),
    OrderManager(OrderManagerActivity.class),
    StaffNotice(StaffNoticeListActivity.class),
    ChannelNotice(ChannelNoticeListActivity.class),
    Stock(StockActivity.class),
    Receivables(ReceivablesActivity.class),
    Restock(RestockActivity.class),
    Dues(DuesActivity.class),
    SignIn(SignInActivity.class),
    SignMonth(SignMonthActivity.class),
    MyState(MySignStateActivity.class),
    SimpleBill(SimpleBillActivity.class),
    SimpleBillDataSelect(SimpleBillDataSelectActivity.class),
    VisitCustomer(VisitCustomerListActivity.class),
    StoreAdd(StoreAddActivity.class),
    ActorTree(ActorTreeActivity.class),
    AskList(AskListActivity.class),
    LeaveList(LeaveListActivity.class),
    TravelList(TravelListActivity.class),
    VisitRecord(VisitRecordListActivity.class),
    CustomerDistribution(CustomerDistributionActivity.class),
    VisitApprove(VisitApproveListActivity.class),
    PrizeList(PrizeOrderListActivity.class),
    ItineraryList(ItineraryListActivity.class),
    ItineraryEdit(ItineraryActivity.class),

    Test(SimpleBillActivity.class),
    //// TODO: 2016/2/3 新增其他功能入口
    ;

    public Class<? extends BaseActivity> activity;

    FunctionEnum(Class<? extends BaseActivity> activity) {
        this.activity = activity;
    }
}


//phxq  bhxq 配货需求 补货需求

//rkqr 入库确认

//

