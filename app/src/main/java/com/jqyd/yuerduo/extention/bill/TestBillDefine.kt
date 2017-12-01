package com.jqyd.yuerduo.extention.bill

/**
 * Created by zhangfan on 16-11-4.
 */
class TestBillDefine {

    companion object {

        val json = """


        {
            "postUrl":"/xxx/xxx",
            "title":"我的单据1",
            "itemList":[
                {
                    "type":0,
                    "define":{
                        "label" : "名称",
                        "hint" : "请输入",
                        "hintTextColor" : "#00FF00",
                        "id" : "name",
                        "singleLine" : false,
                        "textColor" : "#FF0000",
                        "labelColor" : "#0000FF",
                        "text" : "默认内容\n内容"
                    }
                },
                {
                    "type":0,
                    "define":{
                        "label" : "文本1",
                        "hint" : "请输入。。。",
                        "hintTextColor" : "#00FF00",
                        "id" : "text1",
                        "textColor" : "#FF0000",
                        "labelColor" : "#0000FF"
                    }
                },
                {
                    "type":1,
                    "define":{
                        "id" : "testTime",
                        "label" : "全时间",
                        "hint" : "选个时间吧",
                        "type" : "ALL",
                        "format" : "yyyy-MM-dd HH-mm-ss"
                    }
                },
                {
                    "type":2,
                    "define":{
                        "textColor" : "#FF0000",
                        "id" : "myselect",
                        "label" : "单选",
                        "list" :[{"id":"11", "value":"A"},{"id":"22", "value":"B"}]
                    }
                },
                {
                    "type":3,
                    "define":{
                        "id" : "myselect2",
                        "label" : "列表选择",
                        "multiselect" : false,
                        "dataUrl": "/download/testlist.json"
                    }
                },
                {
                    "type":4,
                    "define":{
                        "id" : "myPic1",
                        "necessary" : true,
                        "maxNum" : 5,
                        "minNum": 2,
                        "label" : "拍照测试"
                    }
                },
                {
                    "type": 5,
                    "define":{
                        "id": "func1",
                        "label":"打开某个功能",
                        "funcId":"Restock",
                        "jsonParam":{
                            "title":"自定义的标题"
                        }
                    }
                },
                {
                    "type":6,
                    "define":{
                        "id" : "mybill2",
                        "text" : "打开新单据2",
                        "billDefine" : {
                            "title":"单据标题2",
                            "itemList":[
                                {
                                    "type":0,
                                    "define":{
                                        "label" : "文本1",
                                        "hint" : "请输入。。。",
                                        "hintTextColor" : "#00FF00",
                                        "id" : "text1",
                                        "textColor" : "#FF0000",
                                        "labelColor" : "#0000FF"
                                    }
                                },
                                {
                                    "type":1,
                                    "define":{
                                        "id" : "testTime",
                                        "label" : "只有时间",
                                        "hint" : "选个时间吧",
                                        "type" : "HOURS_MINS",
                                        "format" : "HH-mm"
                                    }
                                }
                            ]
                        }
                    }
                },{
                    "type":7,
                    "define":{
                        "id" : "myCompteInput1",
                        "singleLine" : true,
                        "label" : "A输入",
                        "label2" : "B输入",
                        "text" : "A的默认内容",
                        "selectAllOnFocus": true,
                        "text2" : "B的默认内容",
                        "hint" : "请输入",
                        "hint2" : "请输入",
                        "hintTextColor" : "#00FF00",
                        "hintTextColor2" : "#00FF00",
                        "textColor" : "#FF0000",
                        "textColor2" : "#FF0000",
                        "labelColor" : "#0000FF",
                        "labelColor2" : "#0000FF",
                        "maxLength": 10,
                        "maxLength2": 10,
                        "inputType": 0,
                        "inputType2": 0,
                        "editable" : true,
                        "editable2" : true
                    }
                },{
                    "type": 8,
                    "define":{
                        "id": "myBillList",
                        "text": "点击打开 type8",
                        "billList":[
                            {
                                "title":"第一页标题",
                                "itemList":[
                                    {
                                      "type":0,
                                      "define":{
                                           "label" : "文本1",
                                            "hint" : "请输入。。。",
                                            "hintTextColor" : "#00FF00",
                                            "id" : "text1",
                                            "necessary" : true,
                                            "textColor" : "#FF0000",
                                          "labelColor" : "#0000FF"
                                        }
                                    },
                                    {
                                        "type":1,
                                        "define":{
                                            "id" : "testTime",
                                            "label" : "只有时间",
                                            "hint" : "选个时间吧",
                                            "type" : "HOURS_MINS",
                                            "format" : "HH:mm"
                                        }
                                    }
                                ]
                            },{
                                "title":"第二页标题",
                                "itemList":[
                                    {
                                      "type":0,
                                      "define":{
                                           "label" : "文本1",
                                            "hint" : "请输入。。。",
                                            "hintTextColor" : "#00FF00",
                                            "id" : "text1",
                                            "textColor" : "#FF0000",
                                          "labelColor" : "#0000FF"
                                        }
                                    },
                                    {
                                        "type":1,
                                        "define":{
                                            "id" : "testTime",
                                            "label" : "只有时间",
                                            "hint" : "选个时间吧",
                                            "type" : "HOURS_MINS",
                                            "format" : "HH:mm"
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                }
            ]
        }



        """

        val json2 = """


{
    "id": "-21",
    "title": "销量上报",
    "itemList": [
        {
            "type": 6,
            "define": {
                "cid": "5",
                "mid": "",
                "sort": "",
                "required": "",
                "label": "白象大骨面袋装",
                "hint": "",
                "hintTextColor": "",
                "id": "0",
                "textColor": "",
                "labelColor": "",
                "text": "",
                "singleLine": "",
                "necessary" : true,
                "editable": true,
                "defineType": "",
                "format": "",
                "list": "",
                "dataUrl": "",
                "multiselect": "",
                "funcId": "",
                "jsonParam": "",
                "billdefine": "",
                "billDefine": {
                    "title": "销量上报",
                    "itemList": [
                        {
                            "type": 7,
                            "define": {
                                "cid": "5",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "本品",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "-1",
                                "textColor": "",
                                "labelColor": "",
                                "text": "白象大骨面袋装",
                                "singleLine": true,
                                "editable": false,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "5",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第1周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "1",
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "5",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第2周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "2",
                                "necessary" : true,
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "5",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第3周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "3",
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "5",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第4周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "4",
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "5",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第5周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "5",
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        }
                    ]
                },
                "billdefineName": "",
                "maxNum": "",
                "minNum": "",
                "operationType": "",
                "maxLength": "",
                "inputType": "",
                "label2": "",
                "hint2": "",
                "hintTextColor2": "",
                "textColor2": "",
                "labelColor2": "",
                "ediTable2": "",
                "text2": "",
                "maxLength2": "",
                "inputType2": "",
                "data": "",
                "data2": "",
                "defineList": "",
                "userInput": ""
            }
        },
        {
            "type": 6,
            "define": {
                "cid": "6",
                "mid": "",
                "sort": "",
                "required": "",
                "label": "白象大骨面桶装",
                "hint": "",
                "hintTextColor": "",
                "id": "-1",
                "textColor": "",
                "labelColor": "",
                "text": "",
                "singleLine": "",
                "editable": true,
                "defineType": "",
                "format": "",
                "list": "",
                "dataUrl": "",
                "multiselect": "",
                "funcId": "",
                "jsonParam": "",
                "billdefine": "",
                "billDefine": {
                    "title": "销量上报",
                    "itemList": [
                        {
                            "type": 7,
                            "define": {
                                "cid": "6",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "本品",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "-1",
                                "textColor": "",
                                "labelColor": "",
                                "text": "白象大骨面桶装",
                                "singleLine": true,
                                "editable": false,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "6",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第1周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "1",
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "6",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第2周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "2",
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "6",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第3周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "3",
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "6",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第4周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "4",
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        },
                        {
                            "type": 7,
                            "define": {
                                "cid": "6",
                                "mid": "",
                                "sort": "",
                                "required": "",
                                "label": "第5周销量",
                                "hint": "",
                                "hintTextColor": "",
                                "id": "5",
                                "textColor": "",
                                "labelColor": "",
                                "text": "",
                                "singleLine": true,
                                "editable": true,
                                "defineType": "",
                                "format": "",
                                "list": "",
                                "dataUrl": "",
                                "multiselect": "",
                                "funcId": "",
                                "jsonParam": "",
                                "billdefine": "",
                                "billDefine": "",
                                "billdefineName": "",
                                "maxNum": "",
                                "minNum": "",
                                "operationType": "",
                                "maxLength": "",
                                "inputType": "1",
                                "label2": "",
                                "hint2": "",
                                "hintTextColor2": "",
                                "textColor2": "",
                                "labelColor2": "",
                                "ediTable2": "",
                                "text2": "",
                                "maxLength2": "",
                                "inputType2": "",
                                "data": "",
                                "data2": "",
                                "defineList": "",
                                "userInput": ""
                            }
                        }
                    ]
                },
                "billdefineName": "",
                "maxNum": "",
                "minNum": "",
                "operationType": "",
                "maxLength": "",
                "inputType": "",
                "label2": "",
                "hint2": "",
                "hintTextColor2": "",
                "textColor2": "",
                "labelColor2": "",
                "ediTable2": "",
                "text2": "",
                "maxLength2": "",
                "inputType2": "",
                "data": "",
                "data2": "",
                "defineList": "",
                "userInput": ""
            }
        }
    ]
}

        """
    }
}