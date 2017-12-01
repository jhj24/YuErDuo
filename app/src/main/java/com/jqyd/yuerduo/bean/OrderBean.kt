package com.jqyd.yuerduo.bean

/**
 * Created by zhangfan on 2016/3/4.
 */
class OrderBean : BaseBean() {


    /**
     * createTime : 1457427074721
     * goodsAmount : 88
     * operatorId : 724
     * paymentId : 2
     * invoice : 不开发票
     * shippingExpressId : 0
     * evaluationStatus : 0
     * type : 2
     * shippingCode :
     * orderAmount : 88
     * returnNum : 0
     * orderGoodsList : [{"goodsId":63,"goodsBarterNum":0,"isCustomer":1,"evaluationStatus":0,"gcId":231,"goodsPayPrice":44,"recId":1097,"buyerId":666,"goodsImage":"/upload/img/store/0/1456397336362.jpg","goodsNum":2,"goodsReturnNum":0,"goodsPreAmount":0,"specId":977,"goodsPrice":44,"commisRate":1,"storeId":175,"orderId":1084,"goodsName":"老村长五谷生香","specInfo":"净含量:450ML&nbsp;酒精度:42%&nbsp;香型:浓香型&nbsp;"}]
     * lockState : 0
     * shippingName :
     * predepositAmount : 0
     * orderId : 1084
     * paymentName : 货到付款
     * paySn : P20160308165114688
     * channelName : 桃园路顺和超市
     * orderState : 50
     * storeName : 盼盼食品代理
     * orderType : 0
     * orderSn : 20160308165114704
     * buyerId : 666
     * buyerName : 13303710777
     * paymentCode : 2
     * shippingFee : 0
     * refundState : 0
     * payId : 1096
     * paymentDirect : 1
     * operatorName : 18790312612
     * shippingExpressCode :
     * returnState : 0
     * storeId : 175
     * paymentState : 0
     * addTime : 2016-03-08 16:51:14
     */
    var lat: Double = 0.0
    var lon: Double = 0.0
    val imagesList: String = ""
    val createTime: Long = 0
    val goodsAmount: Double = 0.toDouble()
    val operatorId: Int = 0
    val paymentId: Int = 0
    val invoice: String? = null
    val shippingExpressId: Int = 0
    val evaluationStatus: Int = 0
    val type: Int = 0
    val shippingCode: String? = null
    val orderAmount: Double = 0.toDouble()
    val returnNum: Int = 0
    val lockState: Int = 0
    val shippingName: String? = null
    val predepositAmount: Double = 0.toDouble()
    val orderId: Int = 0
    val paymentName: String? = null
    val paySn: String? = null
    val channelName: String? = null
    var orderState: Int = 0
    val storeName: String? = null
    val orderType: Int = 0
    val orderSn: String? = null
    val buyerId: Int = 0
    val buyerName: String? = null
    val paymentCode: String? = null
    val shippingFee: Int = 0
    val refundState: Int = 0
    val payId: Int = 0
    val paymentDirect: String? = null
    val operatorName: String? = null
    val shippingExpressCode: String? = null
    val returnState: Int = 0
    val storeId: Int = 0
    val paymentState: Int = 0
    val addTime: String? = null
    val address: CustomerAddressBean? = null
    val memo: String? = null

    val shipper: String? = null//送货人
    val shipperPhone: String? = null//送货人手机号

    var actualAmount: String? = null//实际金额

    val orderGoodsList: List<GoodsBean>? = null

    val deliveryNoteType: Int? = null //送货单类型 1:普通订单 2：兑奖单


    // 订单管理
    val stateId: Int? = null //订单状态id
    val stateName: String? = null //订单状态名称
    val operationItemList: List<OperationItemBean>? = null //操作
    val receivedAmount: Double = 0.toDouble() //已收款金额
    val needInvoice: Boolean = false //是否有发票
    val invoiceTitle: String? = null //发票抬头
    val taxpayerNum: String? = null // 纳税人识别号
    val payAccountType: Int? = null //付款账户(1业代账户,2-门店账户)
    val priority: Int? = null //优先级;1-低,2-中,3-高
    val returnGoodsList: List<CouponsGoodsBean>? = null //返现券商品信息
    val returnCashList: List<CouponsGoodsBean>? = null //返物券商品信息


}
