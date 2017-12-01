package com.jqyd.yuerduo.activity.travel

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.TravelBean
import com.jqyd.yuerduo.bean.TravelType
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.anko.DataTextView
import com.jqyd.yuerduo.extention.anko.ID_VALUE
import com.jqyd.yuerduo.extention.anko.commitBillX
import com.jqyd.yuerduo.extention.toArrayList
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.test.BasePresenter
import com.jqyd.yuerduo.util.AreaUtil
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.picker.OptionsPickerView
import okhttp3.Call
import org.jetbrains.anko.async
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liushiqi on 2017/4/11,0011.
 * 通过type来判断，当前进行的操作
 * type为 0，新增
 * type为 1，修改
 */
class TravelAddBillPresenter(activity: Activity, val billDefine: BillDefineX?, travelBean: TravelBean?, type: String) : BasePresenter(activity) {

    var httpCall: Call? = null
    var isCalling = false
    var days: Int = 0
    var typeList: List<ID_VALUE>? = null
    var hasTravelType: Boolean = false
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    var provinceList = ArrayList<AreaUtil.AreaNode>()
    var cityList = ArrayList<ArrayList<AreaUtil.AreaNode>>()
    val area = AreaUtil.getAreaList(activity)
    val optionStartPlace = OptionsPickerView<AreaUtil.AreaNode>(activity)
    val optionEndPlace = OptionsPickerView<AreaUtil.AreaNode>(activity)

    var hasOkArea = false

    init {
        val bill = bill("travelBill")
        if (bill == null) {
            activity.toast("单据定义异常")
            activity.finish()
        }
        getTravelType()
        selectLocation()
        if (type == "1") {
            textView("startDateStr")?.text = travelBean?.startDate
            textView("endDateStr")?.text = travelBean?.endDate
            textView("trafficType")?.text = travelBean?.trafficTypeName
            (textView("trafficType") as DataTextView).data = travelBean?.trafficType.toString()
            textView("creatorName")?.text = travelBean?.creatorName
            textView("startPlace")?.text = travelBean?.startPlace
            (textView("startPlace") as DataTextView).data = travelBean?.startPlace
            textView("endPlace")?.text = travelBean?.endPlace
            (textView("endPlace") as DataTextView).data = travelBean?.endPlace
            textView("nextActorId")?.text = travelBean?.nextActorName
            (textView("nextActorId") as DataTextView).data = travelBean?.nextActorId
            editText("reason")?.setText(travelBean?.reason)
        }

        textView("startDateStr")?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val startDateString = textView("startDateStr")?.text.toString()
                val endDateString = textView("endDateStr")?.text.toString()
                val start = sdf.parse(startDateString).time
                val end = sdf.parse(endDateString).time
                days = (end - start).div(1000 * 60 * 60 * 24).toInt()
                val startDate = sdf.parse(startDateString)
                val endDate = sdf.parse(endDateString)
                if (startDate.after(endDate)) {
                    activity.toast("结束日期早于开始日期")
                    textView("startDateStr")?.text = endDateString
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        textView("endDateStr")?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val startDateString = textView("startDateStr")?.text.toString()
                val endDateString = textView("endDateStr")?.text.toString()
                val start = sdf.parse(startDateString).time
                val end = sdf.parse(endDateString).time
                days = (end - start).div(1000 * 60 * 60 * 24).toInt()
                val startDate = sdf.parse(startDateString)
                val endDate = sdf.parse(endDateString)
                if (startDate.after(endDate)) {
                    activity.toast("结束日期早于开始日期")
                    textView("endDateStr")?.text = startDateString
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })


        textView("trafficType")?.onClick {
            if (hasTravelType) {
                showAlertView(activity, typeList)
            } else {
                textView("trafficType")?.hint = "数据加载中..."
                getTravelType()
            }
        }


        textView("startPlace")?.onClick {
            optionStartPlace.setPicker(provinceList, cityList, true)
            optionStartPlace.setCyclic(false, false, false)
            optionStartPlace.setTitle("选择城市")
            optionStartPlace.setOnoptionsSelectListener({ pos1, pos2, pos3 ->
                val mLocation: String
                val province = provinceList[pos1].areaName
                var city = cityList[pos1][pos2].areaName

                //返回的分别是三个级别的选中位置
                if (city == "省直辖县") {
                    city = ""
                }
                if (province == city) {
                    mLocation = province
                } else {
                    mLocation = province + city
                }
                if (mLocation != textView("startPlace")?.text.toString()) {
                    textView("startPlace")?.text = mLocation
                    (textView("startPlace") as DataTextView).data = mLocation
                }

            })
            if (hasOkArea) {
                LocationLink(optionStartPlace, textView("startPlace")?.text.toString())
                optionStartPlace.show()
            } else {
                activity.toast("数据解析中...")
            }

        }

        textView("endPlace")?.onClick {
            optionEndPlace.setPicker(provinceList, cityList, true)
            optionEndPlace.setCyclic(false, false, false)
            optionEndPlace.setTitle("选择城市")
            optionEndPlace.setOnoptionsSelectListener({ pos1, pos2, pos3 ->
                val mLocation: String
                val province = provinceList[pos1].areaName
                var city = cityList[pos1][pos2].areaName

                //返回的分别是三个级别的选中位置
                if (city == "省直辖县") {
                    city = ""
                }
                val provinceSubstring = province.substring(0,2)
                val citySubstring = city.substring(0,2)
                if (provinceSubstring == citySubstring) {
                    mLocation = province
                } else {
                    mLocation = province + city
                }
                if (mLocation != textView("endPlace")?.text.toString()) {
                    textView("endPlace")?.text = mLocation
                    (textView("endPlace") as DataTextView).data = mLocation
                }

            })
            if (hasOkArea) {
                LocationLink(optionEndPlace, textView("endPlace")?.text.toString())
                optionEndPlace.show()
            } else {
                activity.toast("数据解析中...")
            }
        }

        if (bill?.billDefine?.postUrl.isNullOrBlank()) {
            button("commit")?.text = "提交"
        }
        button("commit")?.onClick {
            var params: HashMap<String, String>? = null
            if (bill == null) return@onClick
            val billData = bill.buildData()

            if (type == "1") {//修改时
                if (travelBean?.id != null) {
                    params = hashMapOf("id" to travelBean?.id.toString())
                    //判断当审批人一栏不为空时，设置该栏为填写装态
                    if (!textView("nextActorId")?.text.isNullOrEmpty()) {
                        billData.itemList[1].define["finish"] = true
                    }
                }
            }
            val checkResult = billData.checkNecessaryFiled(activity)
            if (!checkResult) return@onClick
            if (billData.postUrl.isNullOrBlank()) {
                val intent = Intent()
                intent.putExtra("billDefine", billData)
                activity.setResult(Activity.RESULT_OK, intent)
                activity.finish()
                return@onClick
            }
            isCalling = true
            httpCall = commitBillX(activity, URLConstant.ADD_ASK_TRAVEL, billData, params, object : GsonProgressHttpCallback<BaseBean>(activity, "正在提交") {
                override fun onSuccess(result: ResultHolder<BaseBean>) {
                    super.onSuccess(result)
                    activity.toast("提交成功")
                    activity.setResult(Activity.RESULT_OK)
                    activity.finish()
                }

                override fun onFailure(msg: String, errorCode: Int) {
                    super.onFailure(msg, errorCode)
                    if (!(httpCall?.isCanceled ?: false)) {
                        activity.toast(msg)
                    } else {
                        activity.toast("取消提交")
                    }
                }

                override fun onFinish() {
                    super.onFinish()
                    isCalling = false
                }
            })
        }
    }

    fun getTravelType() {
        HttpCall.request(activity, URLConstant.GET_TRAVEL_TYPE, null, object : GsonDialogHttpCallback<TravelType>(activity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<TravelType>) {
                super.onSuccess(result)
                val typeData = result.dataList
                typeList = (0..result.dataList.size - 1).map { ID_VALUE(typeData[it].id, typeData[it].name) }
                hasTravelType = true
                textView("trafficType")?.hint = "请选择"
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                activity.toast(msg)
                textView("trafficType")?.hint = "交通方式加载失败，请点击重试"
            }
        })
    }

    private fun showAlertView(activity: Activity, typeList: List<ID_VALUE>?) {
        typeList?.let {
            AlertView("交通方式", null, "取消", null, typeList.map { it.value }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                p0, index ->
                if (index != -1) {
                    textView("trafficType")?.text = typeList[index].value
                    (textView("trafficType") as DataTextView).data = typeList[index].id
                }
            }).show()
        }
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isCalling) {
            httpCall?.cancel()
            return true
        }
        return false
    }


    /**
     * 省市县三级联动
     */
    fun selectLocation() {
        async() {
            provinceList = area.filter { it.areaDeep == 1 }.toArrayList()
            cityList = provinceList.map { provinceBean ->
                area.filter { it.areaDeep == 2 && it.areaParentId == provinceBean.areaId }.toArrayList()
            }.toArrayList()
            hasOkArea = true
        }
    }


    private fun LocationLink(option: OptionsPickerView<AreaUtil.AreaNode>, address: String) {

        if (!address.isNullOrEmpty()) {
            val loc1: Int
            val loc2: Int
            val province: String? //截取的省

            val provinceSubstring = address.substring(0, 2)//截取地址数据中的省（前两个字）

            val provinceResultList = provinceList.filter { it.areaName.substring(0, 2) == provinceSubstring }.orEmpty()

            if (provinceResultList.isNotEmpty()) {
                province = provinceResultList[0].areaName
                val index = provinceList.indexOf(provinceResultList[0])
                loc1 = if (index != -1) index else 0
            } else {
                province = provinceList[0].areaName
                loc1 = 0
            }


            if (provinceSubstring == "台湾" || provinceSubstring == "香港"
                    || provinceSubstring == "澳门" || provinceSubstring == "海外"
                    || provinceSubstring == "北京" || provinceSubstring == "天津"
                    || provinceSubstring == "上海" || provinceSubstring == "重庆") {
                loc2 = 0
            } else {
                val citySubstring = address.replaceFirst(province.orEmpty(), "").substring(0, 2)//截取地址数据中的市（前两个字）
                //市
                if (!citySubstring.isNullOrBlank()) {
                    val cityResultList = cityList[loc1].filter { it.areaName.substring(0, 2) == citySubstring }.orEmpty()
                    if (cityResultList.isNotEmpty()) {
                        val index = cityList[loc1].indexOf(cityResultList[0])
                        loc2 = if (index != -1) index else 0
                    } else {
                        loc2 = 0
                    }
                } else {
                    loc2 = 0
                }

            }


            option.setSelectOptions(loc1, loc2)

        }
    }
}