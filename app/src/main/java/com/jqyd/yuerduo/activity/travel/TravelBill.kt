package com.jqyd.yuerduo.activity.travel


class TravelBill  {


    companion object {

        val json = """
        {
            "postUrl":"/xxx/xxx",
            "title":"新增差旅",
            "itemList":[

                {
                    "type":2,
                    "define":{
                        "id" : "trafficType",
                        "label" : "交通方式",
                        "necessary" : true,
                        "hint" : "请选择"
                    }
                },
                {
                    "type": 5,
                    "define":{
                        "id" : "nextActorId",
                        "label" : "审  批  人",
                        "hint" : "请选择",
                        "necessary" : true,
                        "funcId":"ActorTree",
                        "jsonParam":{
                            "title":"请  选  择",
                            "billType" : "1",
                            "billId":"%s"
                        }
                    }
                },
                {
                    "type":1,
                    "define":{
                        "id" : "startDateStr",
                        "label" : "开始日期",
                        "hint" : "请选择",
                        "necessary" : true,
                        "type" : "YEAR_MONTH_DAY",
                        "format" : "yyyy-MM-dd"
                    }
                },
                {
                    "type":1,
                    "define":{
                        "id" : "endDateStr",
                        "label" : "结束日期",
                        "hint" : "请选择",
                        "necessary" : true,
                        "type" : "YEAR_MONTH_DAY",
                        "format" : "yyyy-MM-dd"
                    }
                },
                {
                    "type":2,
                    "define":{
                        "id" : "startPlace",
                        "label" : "出发地址",
                        "necessary" : true,
                        "hint" : "请选择"
                    }
                },
                {
                    "type":2,
                    "define":{
                        "id" : "endPlace",
                        "label" : "目的地址",
                        "hint" : "请选择",
                        "necessary" : true
                    }
                },
                {
                    "type":0,
                    "define":{
                        "label" : "差旅原因",
                        "hint" : "请输入",
                        "id" : "reason",
                        "necessary" : true,
                        "singleLine" : false
                    }
                },
                {
                    "type":9,
                    "define":{
                        "label" : "添加附件",
                        "hint" : "请选择",
                        "id" : "attachmentKey",
                        "urlJsonList" : %s
                    }
                }
            ]
        }

        """

        val approveJson = """


        {
            "postUrl":"/xxx/xxx",
            "title":"差旅审批",
            "itemList":[

                {
                    "type":2,
                    "define":{
                        "id" : "operation",
                        "label" : "操作",
                        "necessary" : true,
                        "hint" : "请选择"
                    }
                },
                {
                    "type": 5,
                    "define":{
                        "id" : "nextActorId",
                        "label" : "转发对象",
                        "hint" : "请选择",
                        "funcId":"ActorTree",
                        "jsonParam":{
                            "title":"转发对象",
                            "billType" : "1",
                            "billId":"%s"
                        }
                    }
                },
                {
                    "type":0,
                    "define":{
                        "label" : "审批意见",
                        "hint" : "请输入",
                        "id" : "message",
                        "singleLine" : false
                    }
                }
            ]
        }

        """
    }

}
