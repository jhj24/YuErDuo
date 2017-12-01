package com.jqyd.yuerduo.activity.ask

/**
 *
 * Created by jianhaojie on 2017/1/18.
 */
class AskBill {
    companion object {
        /**
         * 我的请示新增、修改布局json数据
         */
        val myAsk = """
        {
            "title":"新增请示",
            "itemList":[
                {
                    "type":0,
                    "define":{
                        "label" : "请示标题",
                        "hint" : "请输入",
                        "id" : "title",
                        "necessary" : true
                    }
                },
                 {
                    "type":0,
                    "define":{
                        "id" : "content",
                        "label" : "请示内容",
                        "hint" : "请输入",
                        "necessary" : true,
                        "singleLine" : false
                    }
                },
                {
                    "type": 5,
                    "define":{
                        "id" : "nextActorId",
                        "label" : "请示对象",
                        "hint" : "请选择",
                        "necessary" : true,
                        "funcId":"ActorTree",
                        "jsonParam":{
                            "title":"请  选  择",
                            "billType" : "0",
                            "billId":"%s"
                        }
                    }
                },
                {
                    "type":2,
                    "define":{
                        "id" : "instructionType",
                        "label" : "请示类型",
                        "hint" : "请选择"
                    }
                },
                {
                    "type": 9,
                    "define":{
                        "id" : "attachmentKey",
                        "label" : "添加附件",
                        "hint" : "请选择",
                        "urlJsonList" : %s
                    }
                }
            ]
        }

        """
        /**
         * 请示审批布局json数据
         */
        val checkAsk = """
        {
            "title":"请示审批",
            "itemList":[
                {
                    "type":2,
                    "define":{
                        "id" : "operation",
                        "label" : "操作",
                        "hint" : "请选择",
                         "necessary" : true,
                        "list" :[{"id":"2", "value":"转发"},{"id":"1", "value":"驳回"},{"id":"0", "value":"同意"}]
                    }
                },
                {
                    "type": 5,
                    "define":{
                        "id" : "nextActorId",
                        "label" : "转发对象",
                        "hint" : "请输入",
                        "funcId":"ActorTree",
                        "jsonParam":{
                            "title":"转发对象",
                            "billType" : "0",
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

        /**
         * 请示审批筛选布局json数据
         */
        val filter = """
          {
          "title":"筛选",
            "itemList":[
                {
                    "type":0,
                    "define":{
                        "label" : "提交人",
                        "hint" : "请输入",
                        "id" : "creatorName"
                    }
                },
                {
                    "type":1,
                    "define":{
                        "id" : "startTime",
                        "label" : "开始时间",
                        "hint" : "请选择",
                        "type" : "YEAR_MONTH_DAY",
                        "format" : "yyyy-MM-dd"
                    }
                },
                {
                    "type":1,
                    "define":{
                        "id" : "endTime",
                        "label" : "结束时间",
                        "hint" : "请选择",
                        "type" : "YEAR_MONTH_DAY",
                        "format" : "yyyy-MM-dd"
                    }
                },
                 {
                    "type":2,
                    "define":{
                        "id" : "states",
                        "label" : "审批状态",
                        "hint" : "请选择",
                        "list" :[{"id":"0", "value":"待审批"},{"id":"1", "value":"已同意"},{"id":"2", "value":"已驳回"},{"id":"3", "value":"已转发"}]
                    }
                }
            ]
        }
        """
        /**
         * 请示审批筛选布局json数据
         */
        val filter2 = """
          {
          "title":"筛选",
            "itemList":[
                {
                    "type":1,
                    "define":{
                        "id" : "startTime",
                        "label" : "开始时间",
                        "hint" : "请选择",
                        "type" : "YEAR_MONTH_DAY",
                        "format" : "yyyy-MM-dd"
                    }
                },
                {
                    "type":1,
                    "define":{
                        "id" : "endTime",
                        "label" : "结束时间",
                        "hint" : "请选择",
                        "type" : "YEAR_MONTH_DAY",
                        "format" : "yyyy-MM-dd"
                    }
                },
                 {
                    "type":2,
                    "define":{
                        "id" : "states",
                        "label" : "审批状态",
                        "hint" : "请选择",
                        "list" :[{"id":"0", "value":"待审批"},{"id":"1", "value":"已同意"},{"id":"2", "value":"已驳回"}]
                    }
                }
            ]
        }
        """
    }
}