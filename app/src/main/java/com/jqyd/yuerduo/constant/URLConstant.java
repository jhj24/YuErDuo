package com.jqyd.yuerduo.constant;

/**
 * 网络请求接口地址
 * Created by zhangfan on 2016/1/25.
 */
public class URLConstant {

//        public static final String DefaultServiceHost = "http://192.168.5.137:8080";
//    public static final String DefaultServiceHost = "http://www.97gyl.com";


    public static final String ServiceHost = ServerHost.INSTANCE.getValue();
//    public static final String ServiceHost = ApkBuildUtil.INSTANCE.getServiceHost();

//    public static final String ServiceHost = "http://192.168.5.107:8080";
//    public static final String ServiceHost = "http://120.27.107.170/localdebug";
//    public static final String ServiceHost = "http://192.168.5.134:8080";
//    public static final String ServiceHost = "http://192.168.5.132:8080";
//    public static final String ServiceHost = "http://192.168.5.123:8080";

    public static final String DEFAULT_SERVER = ServiceHost + "/yuerduo-front";
    public static final String SELLER_SERVER = ServiceHost + "/yuerduo-seller/sellerApi";

    public static final String ICONS_DOWNLOAD_HOST = ServiceHost + "/download/icons";

    /**
     * 登录
     */
    public static final String Login = DEFAULT_SERVER + "/loginapi/login";


    /**
     * 客户端更新信息
     */
    public static final String ClientUpdate = SELLER_SERVER + "/mobileupgradeapi/getupgrademessage";

    public static final String GoodsDetail = DEFAULT_SERVER + "/goods/api/goodsdetail";


    /**
     * 生成订单
     **/
    public static final String SUB_TO_ORDER = DEFAULT_SERVER + "/cartapi/subToOrder";

    /**
     * 保存订单
     **/
    public static final String SAVE_ORDER = DEFAULT_SERVER + "/orderapi/saveorder";


    /**
     * 订单详情
     **/
    public static final String ORDER_DETAIL = DEFAULT_SERVER + "/orderapi/orderdetail";

    /**
     * 订单列表
     **/
    public static final String ORDER_LIST = DEFAULT_SERVER + "/orderapi/orderlist";

    /**
     * 送货单列表
     **/
    public static final String DELIVERY_LIST = DEFAULT_SERVER + "/orderapi/deliverylist";

    /**
     * 兑奖单列表
     **/
    public static final String PRIZE_ORDER_LIST = SELLER_SERVER + "/lotteryApi/list";

    /**
     * 消息列表
     **/
    public static final String STAFF_NOTICE_LIST = SELLER_SERVER + "/allnoticeapi/staffnoticelist";
    /**
     * 获取消息详情
     */
    public static final String GET_MESSAGE_DETAIL = SELLER_SERVER + "/allnoticeapi/goToStaffNoticeDetail";

    /**
     * 员工通知更新阅读状态
     **/
    public static final String NOTICE_READ = SELLER_SERVER + "/allnoticeapi/setnoticeread";


    /**
     * 增加购物车商品
     */
    public static final String AddCar = DEFAULT_SERVER + "/cartapi/addCart";
    /**
     * 批量增加购物车商品
     */
    public static final String AddCars = DEFAULT_SERVER + "/cartapi/addCarts";
    /**
     * 删除购物车商品
     **/
    public static final String DELETE_CART = DEFAULT_SERVER + "/cartapi/deleteCart";
    /**
     * 获取所有渠道
     */
    public static final String AllCustomer = SELLER_SERVER + "/staffapi/getAllChannel";

    /**
     * 获取渠道商品
     */
    public static final String CustomerGoods = DEFAULT_SERVER + "/storeapi/storegoods";

    /**
     * 更改购物车数量
     **/
    public static final String UPDATE_CARCOUNT = DEFAULT_SERVER + "/cartapi/updateCartCount";

    /**
     * 清空购物车
     **/
    public static final String DELETE_ALL_CAR = DEFAULT_SERVER + "/cartapi/deleteAllCart";

    /**
     * 获取渠道收货地址列表
     **/
    public static final String MANAGER_ADDRESS = DEFAULT_SERVER + "/address/api/addressList";

    /**
     * 获取功能树
     **/
    public static final String FUNCTION_LIST = SELLER_SERVER + "/workbenchapi/workbenchlist";
    /**
     * 获取各功能未读消息
     */
    public static final String FUNCTION_UNREAD_NUM = SELLER_SERVER + "/functionNumApi/getFunctionNum";
    /**
     * 新增员工通知
     **/
    public static final String STAFF_NOTICE_SAVE = SELLER_SERVER + "/allnoticeapi/saveStaffNotice";
    /**
     * 获取员工通知阅读情况
     */
    public static final String STAFF_NOTICE_READ = SELLER_SERVER + "/allnoticeapi/getNoticeRead";
    /**
     * 我发送的员工通知
     **/
    public static final String STAFF_NOTICE_CREATE_BY_ME = SELLER_SERVER + "/allnoticeapi/getNoticeList";
    /**
     * 获取部门员工树
     **/
    public static final String DEPT_STAFF = SELLER_SERVER + "/allnoticeapi/deptStaff";

    /**
     * 取消订单
     **/
    public static final String CANCEL_ORDER = DEFAULT_SERVER + "/orderapi/cancleorder";

    /**
     * 认领订单
     **/
    public static final String CLAIM_ORDER = DEFAULT_SERVER + "/orderapi/claimOrder";

    /**
     * 送达订单
     **/
    public static final String TO_SIGN_ORDER = DEFAULT_SERVER + "/orderapi/tosignorder";
    /**
     * 发送验证码url
     **/
    public static final String SEND_CODE = DEFAULT_SERVER + "/floor/api/verifyCode";
    /**
     * 员工通讯录
     **/
    public static final String CONTACTS_STAFF = SELLER_SERVER + "/staffContacts/list";

    /**
     * 重置密码
     **/
    public static final String RESET_PASSWORD = DEFAULT_SERVER + "/memberapi/resetPassword";

    /**
     * 修改密码
     **/
    public static final String UPDATE_PASSWORD = DEFAULT_SERVER + "/memberapi/updatePassword";

    /**
     * 上传位置
     */
    public static final String UPLOAD_LOCATION = SELLER_SERVER + "/positionapi/savePosition";

    /**
     * 查询移动库存
     */
    public static final String GET_STOCK = SELLER_SERVER + "/mobiledepotapi/getMobileDepot";

    /**
     * 查询移动库存流水
     */
    public static final String GET_STOCK_DETAIL = SELLER_SERVER + "/mobiledepotapi/getGoodslog";

    /**
     * 查询我的应收款
     */
    public static final String GET_RECEIVABLES = SELLER_SERVER + "/financeapi/debtList";

    /**
     * 收款
     */
    public static final String RECEIVABLES = SELLER_SERVER + "/financeapi/reportcustomerpay";

    /**
     * 查询补货需求
     */
    public static final String GET_RESTOCK = SELLER_SERVER + "/mobiledepotapi/replenishment";

    /**
     * 查询我的应缴款
     */
    public static final String GET_DUES = SELLER_SERVER + "/financeapi/getMyDues";

    /**
     * 发布渠道通知
     */
    public static final String SEND_CHANNEL_NOTICE = SELLER_SERVER + "/allnoticeapi/addChannelNotice";

    /**
     * 查询我发布的渠道通知
     */
    public static final String GET_CHANNEL_NOTICE = SELLER_SERVER + "/allnoticeapi/getChannelNotice";
    /**
     * 获取渠道接口
     */
    public static final String GET_CHANNEL_TREE = SELLER_SERVER + "/allnoticeapi/getChannelTree";
    /**
     * 渠道通知阅读状态
     **/
    public static final String CHANNEL_NOTICE_READ = SELLER_SERVER + "/allnoticeapi/getReadCondition";

    /**
     * 获取考勤地点
     **/
    public static final String GET_SIGN_ADDRESS = SELLER_SERVER + "/attendanceApi/getAttendanceLocations";

    /**
     * 获取考勤策略
     **/
    public static final String GET_SIGN_Strategy = SELLER_SERVER + "/attendanceApi/getStrategy";

    /**
     * 考勤操作
     **/
    public static final String DO_SIGN = SELLER_SERVER + "/attendanceApi/getAttendanceOperation";

    /**
     * 考勤月报获取
     **/
    public static final String GET_SIGN_MONTH_INFO = SELLER_SERVER + "/attendanceApi/getSignInOutMonthInfo/v2";

    /**
     * 考勤我的状态
     **/
    public static final String GET_PERSON_STATE = SELLER_SERVER + "/attendanceApi/getPersonState";
    /**
     * 修改个人信息
     **/
    public static final String UPDATE_MEMBER = DEFAULT_SERVER + "/memberapi/updateMember";
    /**
     * 用户详细
     **/
    public static final String MEMBER_DETAIL_URL = DEFAULT_SERVER + "/memberapi/memberDetail";
    /**
     * 获取客户分组
     */
    public static final String STORE_GROUP_URL = SELLER_SERVER + "/ChannelAcquisitionApi/getChannelGroup";
    /**
     * 获取客户属性
     */
    public static final String STORE_ATTR_URL = SELLER_SERVER + "/ChannelAcquisitionApi/getChannelAttr";
    /**
     * 获取客户类别
     */
    public static final String STORE_TYPE_URL = SELLER_SERVER + "/ChannelAcquisitionApi/getChannelType";
    /**
     * 获取客户系统
     */
    public static final String STORE_SYSTEM_URL = SELLER_SERVER + "/ChannelAcquisitionApi/getChannelSystem";
    /**
     * 这是客户采集数据上传
     */
    public static final String STORE_ACQUISITION_URL = SELLER_SERVER + "/ChannelAcquisitionApi/channelAcquisition";
    /**
     * 信息完善列表
     */
    public static final String STORE_PLETE_URl = SELLER_SERVER + "/ChannelAcquisitionApi/getNeedCompleteChannelList";
    /**
     * 查询已激活客户详细
     */
    public static final String STORE_CODE_URL = SELLER_SERVER + "/ChannelAcquisitionApi/IsBusinessLicenseExist";
    /**
     * 客户拜访客户列表
     **/
    public static final String GET_VISIT_CUSTOMERS = SELLER_SERVER + "/visitStrategyApi/getVisitChannelList";

    /**
     * 临时客户拜访客户列表
     **/
    public static final String GET_TEMPORARY_VISIT_CUSTOMERS = SELLER_SERVER + "/visitStrategyApi/getAllVisitChannelList";

    /**
     * 拜访详情及策略接口
     **/
    public static final String GET_VISIT_DETAIL = SELLER_SERVER + "/visitStrategyApi/getVisistRecordDetail";
    /**
     * 提交拜访数据
     **/
    public static final String SAVE_VISIT_DATA = SELLER_SERVER + "/visitStrategyApi/saveVisitData";
    /**
     * 拜访记录列表
     */
    public static final String GET_VISIT_RECORD_DATA_LIST = SELLER_SERVER + "/visitStrategyApi/getNewVisitDataList";
    /**
     * 拜访记录详情 拜访审核详情
     */
    public static final String GET_VISIT_RECORD_DATA_DETAIL = SELLER_SERVER + "/visitStrategyApi/getVisitDataDetail";
    /**
     * 拜访审核列表
     */
    public static final String GET_VISIT_APPROVE_DATA_LIST = SELLER_SERVER + "/visitStrategyApi/getNewVisitApproveDataList";
    /**
     * 提交拜访审核数据
     */
    public static final String SAVE_VISIT_APPROVE_DATA = SELLER_SERVER + "/visitStrategyApi/saveApproveVisitData";
    /**
     * 我的请示类别
     */
    public static final String MY_ASK_TYPE = SELLER_SERVER + "/askInstructionApi/getInstructionType";
    /**
     * 获取请示详情
     */
    public static final String GET_ASK_DETAIL = SELLER_SERVER + "/askInstructionApi/goToAskInstructionDetail";
    /**
     * 我的请示列表
     */
    public static final String MY_ASK_LIST = SELLER_SERVER + "/askInstructionApi/queryInstructionListByMe";
    /**
     * 请示审批列表
     */
    public static final String CHECK_ASK_LIST = SELLER_SERVER + "/askInstructionApi/queryInstructionList";
    /**
     * 获取下级审批人列表,
     */
    public static final String GET_ACTOR_LIST = SELLER_SERVER + "/askInstructionApi/getStaff";
    /**
     * 上传我的请示
     */
    public static final String UPLOAD_MY_ASK = SELLER_SERVER + "/askInstructionApi/addInstruction ";
    /**
     * 请示审批
     */
    public static final String CHECK_ASK = SELLER_SERVER + "/askInstructionApi/checkInstruction";
    /**
     * 删除我的请示
     */
    public static final String DELETE_ASK_TASK = SELLER_SERVER + "/askInstructionApi/cancelAskInstruction";

    /**
     * 新增接口
     */
    public static final String ADD_ASK_LEAVE = SELLER_SERVER + "/askLeaveApi/addAskLeave";
    /**
     * 获取请假类别
     */
    public static final String GET_LEAVE_TYPE = SELLER_SERVER + "/askLeaveApi/getLeaveType";
    /**
     * 获取请假详情
     */
    public static final String GET_LEAVE_DETAIL = SELLER_SERVER + "/askLeaveApi/goToAskLeaveDetail";
    /**
     * 请假审批
     */
    public static final String GET_ASK_LEAVE = SELLER_SERVER + "/askLeaveApi/getAskLeaveList";
    /**
     * 我的请假
     */
    public static final String GET_ASK_LEAVE_ME = SELLER_SERVER + "/askLeaveApi/getAskLeaveListByMe";
    /**
     * 审批
     */
    public static final String CHECK_ASK_LEAVE = SELLER_SERVER + "/askLeaveApi/checkAskLeave";
    /**
     * 获取审批人
     */
    public static final String GET_STAFF = SELLER_SERVER + "/askLeaveApi/getStaff";
    /**
     * 删除我的请假
     */
    public static final String DELETE_LEAVE_TASK = SELLER_SERVER + "/askLeaveApi/cancelAskLeave";
    /**
     * 获取区域分配客户列表
     */
    public static final String GET_DISTRIBUTION_CUSTOMER_LIST = SELLER_SERVER + "/SaleAreaApi/getChannelForParam";
    /**
     * 获取销售区域列表
     */
    public static final String GET_SALES_AREA_LIST = SELLER_SERVER + "/SaleAreaApi/getSaleArea";
    /**
     * 分配区域
     */
    public static final String DISTRIBUTE_AREA = SELLER_SERVER + "/SaleAreaApi/addChannelToSaleArea";

    /**
     * 新增差旅
     */
    public static final String ADD_ASK_TRAVEL = SELLER_SERVER + "/askTravelApi/addAskTravel";
    /**
     * 获取差旅类别
     */
    public static final String GET_TRAVEL_TYPE = SELLER_SERVER + "/askTravelApi/getTravelType";
    /**
     * 差旅列表
     */
    public static final String GET_ASK_TRAVEL_LIST = SELLER_SERVER + "/askTravelApi/getAskTravelList";
    /**
     * 获取差旅详情
     */
    public static final String GET_TRAVEL_DETAIL = SELLER_SERVER + "/askTravelApi/goToAskTravelDetail";
    /**
     * 审批
     */
    public static final String CHECK_ASK_TRAVEL = SELLER_SERVER + "/askTravelApi/checkAskTravel";
    /**
     * 删除
     */
    public static final String CANCEL_ASK_TRAVEL = SELLER_SERVER + "/askTravelApi/cancelAskTravel";
    /**
     * 新增行程
     */
    public static final String ADD_ITINERARY = SELLER_SERVER + "/tItineraryManagement/create";
    /**
     * 修改行程
     */
    public static final String MODIFY_ITINERARY = SELLER_SERVER + "/tItineraryManagement/updateItineraryManagement";
    /**
     * 查询某月中某个员工的行程信息
     */
    public static final String GET_ITINERARY_LIST_BY_MONTH = SELLER_SERVER + "/tItineraryManagement/staffListByMonth";
    /**
     * 行程管理--查询管理范围下的所有员工
     */
    public static final String GET_ITINERARY_STAFF_LIST = SELLER_SERVER + "/shopStaff/findManagementStaffs";
    /**
     * 行程管理--该员工管理范围下所有员工的行程数据
     */
    public static final String GET_ITINERARY_STAFF_LIST_DAY = SELLER_SERVER + "/tItineraryManagement/staffListByDay";

    /**
     * 行程管理-- 查询当月该员工行程 数据集合
     */
    public static final String GET_ITINERARY_STAFF_LIST_MONTH = SELLER_SERVER + "/tItineraryManagement/staffListByMonth";

    /**
     * 返物券
     */
    public static final String GET_RETURN_GOODS_LIST = DEFAULT_SERVER + "/orderapi/returnGoods";
    /**
     * 删除返物券
     */
    public static final String DELETE_RETURN_GOODS = DEFAULT_SERVER + "/orderapi/deleteReturnGoods";

    /**
     * 返现券
     */
    public static final String GET_RETURN_CASH_LIST = DEFAULT_SERVER + "/orderapi/returnCash";
    /**
     * 删除返现券
     */
    public static final String DELETE_RETURN_CASH =  DEFAULT_SERVER + "/orderapi/deleteReturnCash";
    /**
     * 订单管理列表
     */
    public static final String ORDER_MANAGER_LIST = DEFAULT_SERVER + "/orderapi/orderManagermentList";
    /**
     * 订单状态
     */
    public static final String ORDER_STATE = DEFAULT_SERVER + "/orderapi/orderState";
    /**
     * 订单配送人
     */
    public static final String ORDER_DISTRIBUTION = DEFAULT_SERVER + "/orderapi/orderDistribution";
    /**
     * 订单审核
     */
    public static final String ORDER_EXAMINE = DEFAULT_SERVER + "/orderapi/orderExamine";


}
