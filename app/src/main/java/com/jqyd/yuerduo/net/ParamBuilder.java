package com.jqyd.yuerduo.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.jqyd.yuerduo.bean.CustomerBean;
import com.jqyd.yuerduo.bean.GoodsBean;
import com.jqyd.yuerduo.bean.LocationBean;
import com.jqyd.yuerduo.bean.UserBean;
import com.jqyd.yuerduo.constant.SystemConstant;
import com.jqyd.yuerduo.util.SystemEnv;
import com.loopj.android.http.RequestParams;
import com.nightfarmer.androidcommon.device.DeviceInfo;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络请求参数
 * Created by zhangfan on 2016/1/26.
 */
public class ParamBuilder {

    /**
     * 获取客户端升级信息
     *
     * @param versionName 版本号
     * @param releaseTag  发行版本
     * @return 参数
     */
    public static RequestParams ClientUpdate(String versionName, String releaseTag) {
        RequestParams params = new RequestParams();
        params.put("versionName", versionName);
        params.put("releaseTag", releaseTag);
        return params;
    }


    /**
     * 登录
     *
     * @return 参数
     */
    public static Map<String, String> Login(Context context, String username, String password, String imsi) {
        HashMap<String, String> params = new HashMap<>();
        params.put("remember_me", "" + 1);
        params.put("username", username);
        params.put("password", password);
        params.put("imsi", imsi);

        params.put("releaseTag", SystemConstant.ReleaseTag);
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo;
            packInfo = packageManager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            String versionName = packInfo.versionName;
            params.put("versionName", versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("clientType", "" + 2);
        return params;
    }


    /**
     * 生成订单
     *
     * @param cartId 购物车id
     * @return 参数
     */
    public static RequestParams SubToOrder(String cartId) {
        RequestParams params = new RequestParams();
        params.put("cartId", cartId);
        return params;
    }

    /**
     * 保存订单
     *
     * @param addressId 收货地址id
     * @param cartIds   cartid拼接字符串
     * @param type      0-渠道下单，1-业代下单，2-铺货订单
     * @return 参数
     */
    public static RequestParams SaveOrder(String addressId, String cartIds, int customerId, int type, String memo) {
        RequestParams params = new RequestParams();
        params.put("couponId", "");
        params.put("invoiceId", "");
        params.put("isPd", "0");
        params.put("freight", "");
        params.put("paytype", "2");
//        params.put("addressId", addressId);
        params.put("addressId", addressId);
        params.put("cartIds", cartIds);
        params.put("customerId", "" + customerId);
        params.put("type", "" + type);
        params.put("memo", "" + memo);
        return params;
    }

    /**
     * 订单列表
     *
     * @return 参数
     */
    public static RequestParams GetOrderList(Date begin, Date end, String status, int type, int pageNo, int pageSize) {
        RequestParams params = new RequestParams();
        params.put("status", status);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        params.put("type", "" + type);
        if (begin != null) {
            params.put("begin", begin.getTime());
        }
        if (end != null) {
            end.setHours(23);
            end.setMinutes(59);
            end.setMinutes(59);
            params.put("end", end.getTime());
        }
        return params;
    }

    /**
     * 订单列表
     *
     * @return 参数
     */
    public static HashMap<String, String> GetOrderList2(Date begin, Date end, String status, int type, int pageNo, int pageSize) {
        HashMap<String, String> params = new HashMap<>();
        params.put("status", status);
        params.put("pageNo", "" + pageNo);
        params.put("pageSize", "" + pageSize);
        params.put("type", "" + type);
        if (begin != null) {
            params.put("begin", "" + begin.getTime());
        }
        if (end != null) {
            end.setHours(23);
            end.setMinutes(59);
            end.setMinutes(59);
            params.put("end", "" + end.getTime());
        }
        return params;
    }

    /**
     * 订单详情
     *
     * @return 参数
     */
    public static RequestParams GetOrderDetail(Long orderId) {
        RequestParams params = new RequestParams();
        params.put("orderid", orderId);
        return params;
    }

    /**
     * 员工消息列表
     *
     * @return 参数
     */
    public static RequestParams StaffNoticeList(int pageNo, int pageSize) {
        RequestParams params = new RequestParams();
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        return params;
    }

    /**
     * 消息阅读
     *
     * @return 参数
     */
    public static RequestParams NoticeRead(String id) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        return params;
    }

    /**
     * 添加购物车
     *
     * @return 参数
     */
    public static RequestParams AddCar(int customerId, int goodsId, int count) {
        RequestParams params = new RequestParams();
        params.put("specId", "");
        params.put("customerId", customerId);
        params.put("goodsId", "" + goodsId);
        params.put("count", "" + count);
        return params;
    }

    /**
     * 批量添加购物车
     *
     * @return 参数
     */
    public static RequestParams AddCars(int customerId, List<GoodsBean> goodsBeans, int count) {
        RequestParams params = new RequestParams();
        params.put("specId", "");
        params.put("customerId", customerId);
        String goodsIdStr = "";
        for (GoodsBean goods :
                goodsBeans) {
            goodsIdStr += goods.getGoodsId() + ",";
        }
        params.put("goodsIds", "" + goodsIdStr);
        params.put("count", "" + count);
        return params;
    }

    /**
     * 删除购物车商品
     *
     * @return 参数
     */
    public static RequestParams DeleteCart(String cartId) {
        RequestParams params = new RequestParams();
        params.put("cartId", cartId);
        return params;
    }

    /**
     * 获取所有渠道
     *
     * @return 参数
     */
    public static RequestParams AllCustomer() {
        return new RequestParams();
    }

    /**
     * 获取渠道商品
     *
     * @return 参数
     */
    public static RequestParams CustomerGoods(Context context, CustomerBean customerBean) {
        RequestParams params = new RequestParams();
//        int memberId = SystemEnv.getLogin(context).getMemberId();
//        params.put("memberId", "654");
        UserBean login = SystemEnv.getLogin(context);

        params.put("memberId", customerBean.getMemberId());
//        params.put("storeId", "173");
        params.put("storeId", login.getStoreId());
        params.put("pagesize", "9999");
        params.put("pageno", 1);
        return params;
    }

    /**
     * 更新购物车数量
     *
     * @return 参数
     */
    public static RequestParams UpdateCarCount(int count, String cartId) {
        RequestParams params = new RequestParams();
        params.put("count", "" + count);
        params.put("cartId", cartId);
        return params;
    }

    /**
     * 清空购物车
     *
     * @return 参数
     */
    public static RequestParams DeleteAllCart(CustomerBean customerBean) {
        RequestParams params = new RequestParams();
        params.put("memberId", customerBean.getMemberId());
        return params;
    }


    /**
     * 获取渠道收货地址列表
     *
     * @return 参数
     */
    public static RequestParams AddressList(String customerId) {
        RequestParams params = new RequestParams();
        params.put("memberId", customerId);
        params.put("OA", "1");
        return params;
    }

    /**
     * 获取所有功能
     *
     * @return 参数
     */
    public static RequestParams AllFunction() {
        return new RequestParams();
    }

    /**
     * 获取部门员工树
     *
     * @return 参数
     */
    public static RequestParams DeptStaff() {
        return new RequestParams();
    }

    /**
     * 获取渠道树
     *
     * @return 参数
     */
    public static RequestParams GET_CHANNEL_TREE() {
        return new RequestParams();
    }

    /**
     * 认领订单
     *
     * @return 参数
     */
    public static RequestParams CLAIM_ORDER(String orderSn) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("ordersn", orderSn);
        return requestParams;
    }

    /**
     * 送达订单
     *
     * @return 参数
     */
    public static RequestParams TO_SIGN_ORDER(String orderSn, List<GoodsBean> goodsList, LocationBean location) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("ordersn", orderSn);
        if (goodsList != null) {
//            requestParams.put("orderUpdate", "[{'goodsId':111,'num':2},{'goodsId':222,'num':1},{'goodsId':33,'num':5}]");
            requestParams.put("orderUpdate", new Gson().toJson(goodsList));
        }
        if (location != null) {
            requestParams.put("location", new Gson().toJson(location));
        }
        return requestParams;
    }

    /**
     * 新增员工通知
     *
     * @return 参数
     */
    public static RequestParams SaveNotice(String noticetitle, String content, String staffIds, String staffNames, String staffPhones) {
        RequestParams params = new RequestParams();
        params.put("noticetitle", noticetitle);
        params.put("content", content);
        params.put("sourcetype", 2 + "");
        params.put("staffIds", staffIds);
        params.put("staffNames", staffNames);
        params.put("phones", staffPhones);
        return params;
    }

    /**
     * 员工通知阅读情况
     *
     * @return 参数
     */
    public static RequestParams NoticeReadState(String noticeId) {
        RequestParams params = new RequestParams();
        params.put("id", noticeId);
        return params;
    }

    /**
     * 员工通讯录
     *
     * @return 参数
     */
    public static RequestParams CONTACT_STAFF() {
        return new RequestParams();
    }


    /**
     * 重置密码
     *
     * @param password 新密码，若为空字符串，则重置为123456
     * @return 参数
     */
    public static RequestParams RESET_PASSWORD(String phone, String password) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", phone);
        requestParams.put("newpassword", password);
        return requestParams;
    }

    /**
     * 获取验证码
     *
     * @param mobile
     * @return
     */
    public static RequestParams GetCode(String mobile) {
        RequestParams params = new RequestParams();
        params.put("mobile", mobile);
        return params;
    }


    /**
     * 上传位置
     *
     * @return
     */
    public static RequestParams UPLOAD_LOCATION(LocationBean locationBean, int operType, String devicesId) {
        RequestParams params = new RequestParams();
        params.put("createTime", locationBean.getCreateTime());
        params.put("lon", locationBean.getLon());
        params.put("lat", locationBean.getLat());
        params.put("provice", locationBean.getProvice());
        params.put("city", locationBean.getCity());
        params.put("district", locationBean.getDistrict());
        params.put("content", locationBean.getContent());
        params.put("lbstype", locationBean.getLbstype());
        params.put("radius", locationBean.getRadius());
        params.put("operType", operType);
        params.put("deviceId", devicesId);
        return params;
    }


    /**
     * 获取移动库存
     *
     * @return
     */
    public static RequestParams GET_STOCK() {
        return new RequestParams();
    }

    /**
     * 获取移动库存流水
     *
     * @return
     */
    public static RequestParams GET_STOCK_DETAIL(Long goodsId, int pageNo, int pageSize) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("goodsId", goodsId);
        requestParams.put("pageNo", pageNo);
        requestParams.put("pageSize", pageSize);
        return requestParams;
    }

    /**
     * 获取移动库存流水
     *
     * @return
     */
    public static HashMap<String, String> GET_STOCK_DETAIL2(Long goodsId, int pageNo, int pageSize) {
        HashMap<String, String> params = new HashMap<>();
        params.put("goodsId", "" + goodsId);
        params.put("pageNo", "" + pageNo);
        params.put("pageSize", "" + pageSize);
        return params;
    }


    /**
     * 收款
     *
     * @return
     */
    public static RequestParams RECEIVABLES(Long channelId, Double amount) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("channelId", channelId);
        requestParams.put("amount", amount);
        return requestParams;
    }


    /**
     * 发布渠道通知
     *
     * @return
     */
    public static RequestParams SEND_CHANNEL_NOTICE(String channelMemberIds, String channelIds, String tels, String channelNames, String noticeTitle, String content) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("channelMemberIds", channelMemberIds);
        requestParams.put("channelIds", channelIds);
        requestParams.put("tels", tels);
        requestParams.put("channelNames", channelNames);
        requestParams.put("noticeTitle", noticeTitle);
        requestParams.put("content", content);
        return requestParams;
    }


    /**
     * 查询渠道通知
     *
     * @return
     */
    public static RequestParams GET_CHANNEL_NOTICE(String pageNo) {
        RequestParams params = new RequestParams();
        params.put("pageNo", pageNo);
        params.put("pageSize", "20");
        return params;
    }

    /**
     * 渠道消息阅读情况
     *
     * @return 参数
     */
    public static RequestParams CHANNEL_NOTICE_READ(String id) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        return params;
    }

}