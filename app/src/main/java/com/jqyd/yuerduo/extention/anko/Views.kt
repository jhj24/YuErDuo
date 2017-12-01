package com.jqyd.yuerduo.extention.anko

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.constant.FunctionEnum
import com.jqyd.yuerduo.extention.MoneyFilter
import com.jqyd.yuerduo.extention.MoneyInput
import com.jqyd.yuerduo.extention.bill.SimpleBillActivity
import com.jqyd.yuerduo.extention.bill.SimpleBillDataSelectActivity
import com.jqyd.yuerduo.extention.bill.pagerbill.PagerBillActivity
import com.jqyd.yuerduo.extention.bill.pagerbill.PagerBillIndexActivity
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.widget.BillLayout
import com.jqyd.yuerduo.widget.BillLine
import com.jqyd.yuerduo.widget.BillLineX
import com.jqyd.yuerduo.widget.attachment.AttachmentLayout
import com.jqyd.yuerduo.widget.camera.CameraLayout
import com.nightfarmer.draggablerecyclerview.DraggableRecyclerView
import com.nightfarmer.draggablerecyclerview.ProgressStyle
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.picker.TimePickerView
import com.norbsoft.typefacehelper.TypefaceHelper
import com.orhanobut.logger.Logger
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * 布局扩展
 * Created by zhangfan on 2016/4/25 0025.
 */

fun ViewManager.recyclerView(init: (RecyclerView.() -> Unit) = {}) = ankoView({ RecyclerView(it) }, init)
//public inline fun ViewManager.recyclerView(init: RecyclerView.() -> Unit) = include(R.layout.layout_anko_recyclerview, init)

fun ViewManager.draggableRecyclerView(init: (DraggableRecyclerView.() -> Unit) = {}) = ankoView({ DraggableRecyclerView(it) }) {
    setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
    setLaodingMoreProgressStyle(ProgressStyle.BallRotate)
    setArrowImageView(R.drawable.iconfont_downgrey)
    init()
}

fun View.setTopBarTitle(title: String) {
    find<TextView>(R.id.topBar_title).text = title
}

fun ViewManager.topBar(title: String) = topBar(title) {}
fun ViewManager.topBar(title: String, init: View.() -> Unit) = include<View>(R.layout.layout_top_bar) {
    setTopBarTitle(title)
    find<ImageButton>(R.id.topBar_back).onClick { (it?.context as? Activity)?.finish() }
    init()
}


fun ViewManager.commit(text: String = "", buttonId: String = "", init: (Button.() -> Unit) = {}) = button {
    this@button.text = if (text.isNullOrEmpty()) "提交" else text
    init()
    backgroundResource = R.drawable.btn_bg
    textColor = 0xffffff.opaque
    textSizeDimen = R.dimen.font_big
    if (!buttonId.isNullOrBlank()) {
        tag = "button_$buttonId"
    }
}.apply {
    val layoutParams = layoutParams as? LinearLayout.LayoutParams
    layoutParams?.topMargin = dip(10)
    layoutParams?.bottomMargin = dip(10)
    layoutParams?.leftMargin = dip(15)
    layoutParams?.rightMargin = dip(15)
    layoutParams?.width = matchParent
    layoutParams?.height = dip(50)
    TypefaceHelper.typeface(this)
}

fun ViewManager.bill(billDefine: MutableList<BillItem> = ArrayList(), editable: Boolean = true, init: (_LinearLayout.() -> Unit) = {}) = ankoView({ BillLayout(it, billDefine = billDefine) }) {
    verticalLayout {
        for (item in billDefine) {
            when (item.type) {
                0 -> {
                    billInput(item.title, item.hint, item.text, item.singleLine, item.id)
                }
                2 -> {
                    billSelect(item.title, item.hint, editable, item.items, item.id)
                }
                3 -> {
                    billCamera(item.title, editable, item.id)
                }
                4 -> {
                    billAttachment(item.title, editable, item.id)
                }
            }
        }
        init()
    }.apply {
        TypefaceHelper.typeface(this)
        //        topPadding = dip(10)
        //    }.lparams {
        //        width = matchParent
    }.apply {
        if (childCount == 0) return@ankoView;
        var maxIndex = childCount - 1
        var hasMoreButton: Boolean;
        do {
            if (getChildAt(maxIndex) is Button) {
                maxIndex -= 1
                hasMoreButton = true
            } else {
                hasMoreButton = false
            }
        } while (hasMoreButton)
        for (i in 1..maxIndex - 1) {
            getChildAt(i)?.let {
                if (it !is Button) it.setBackgroundResource(R.drawable.bgd_bill_line_center)
            }
        }
        if (childCount == 1) {
            getChildAt(0)?.let {
                if (it !is Button) it.setBackgroundResource(R.drawable.bgd_bill_line_single)
            }
        } else {
            getChildAt(0)?.let {
                if (it !is Button) it.setBackgroundResource(R.drawable.bgd_bill_line_top)
            }
            getChildAt(maxIndex)?.let {
                if (it !is Button) it.setBackgroundResource(R.drawable.bgd_bill_line_bottom)
            }
        }
        for (i in 0..childCount) {
            val view = getChildAt(i)
            view?.let {
                if (view is BillLine) {
                    view.billDefine = billDefine
                    view.addToBill()
                }
            }
        }
    }
}.apply {
    val layoutParams = layoutParams as? LinearLayout.LayoutParams
    layoutParams?.weight = 1f
    layoutParams?.width = LinearLayout.LayoutParams.MATCH_PARENT
}

//fun <T : LinearLayout> T.borderStyle(): T {
//
//    return this
//}

//==========================================================================================================================================

fun ViewManager.xBill(billDefineX: BillDefineX = BillDefineX(), billId: String = "", init: (_LinearLayout.() -> Unit) = {}) = ankoView({ BillLayoutX(it, billDefine = billDefineX) }) {
    if (!billId.isNullOrBlank()) {
        tag = "bill_$billId"
    }
    verticalLayout {
        for (item in billDefine.itemList) {
            when (item.type) {
                0 -> {
                    billInput(item.define)
                }
                1 -> {
                    billDate(item.define)
                }
                2 -> {
                    billSelect(item.define)
                }
                3 -> {
                    billListSelect(item.define)
                }
                4 -> {
                    billCamera(item.define)
                }
                5 -> {
                    billOpenAct(item.define)
                }
                6 -> {
                    billNewBill(item.define)
                }
                7 -> {
                    billCompetInput(item.define)
                }
                8 -> {
                    billPagerLine(item.define)
                }
                9 -> {
                    billAttachment(item.define)
                }
            }
        }
        init()
    }.apply {
        TypefaceHelper.typeface(this)
        //        topPadding = dip(10)
        //    }.lparams {
        //        width = matchParent
    }.apply {
        if (childCount == 0) return@ankoView;
        var maxIndex = childCount - 1
        var hasMoreButton: Boolean;
        do {
            if (getChildAt(maxIndex) is Button) {
                maxIndex -= 1
                hasMoreButton = true
            } else {
                hasMoreButton = false
            }
        } while (hasMoreButton)
        for (i in 1..maxIndex - 1) {
            getChildAt(i)?.let {
                if (it !is Button) it.setBackgroundResource(R.drawable.bgd_bill_line_center)
            }
        }
        if (childCount == 1) {
            getChildAt(0)?.let {
                if (it !is Button) it.setBackgroundResource(R.drawable.bgd_bill_line_single)
            }
        } else {
            getChildAt(0)?.let {
                if (it !is Button) it.setBackgroundResource(R.drawable.bgd_bill_line_top)
            }
            getChildAt(maxIndex)?.let {
                if (it !is Button) it.setBackgroundResource(R.drawable.bgd_bill_line_bottom)
            }
        }
        for (i in 0..childCount) {
            val view = getChildAt(i)
            view?.let {
                if (view is BillLineX) {
                    val dataType = view.dataType
                    val define = view.define
                    val element = BillItemX(dataType, define)
                    val index = billDefine.itemList.indexOf(element)
                    if (index != -1) {
                        billDefine.itemList[index].define = element.define
                        billDefine.itemList[index].type = element.type
                    } else {
                        billDefine.itemList.add(element)
                    }
//                    view.billDefine = billDefine
//                    view.addToBill()
                }
            }
        }
    }
}.apply {
    val layoutParams = layoutParams as? LinearLayout.LayoutParams
    layoutParams?.weight = 1f
    layoutParams?.width = LinearLayout.LayoutParams.MATCH_PARENT
}

fun ViewManager.billInput(vararg pair: Pair<String, Any>) {
    return billInput(pair.toMap().toHashMap())
}

fun ViewManager.billInput(params: HashMap<String, Any>, init: TextView.(BillLineX?) -> Unit = {}) {
    billLine(params, 0) {
        params.put("billLine", this)
//        val hashMap = params.toHashMap()
        input(params, 0, init)
    }
}


fun ViewManager.billCompetInput(vararg pair: Pair<String, Any>) {
    return billCompetInput(pair.toMap().toHashMap())
}

fun ViewManager.billCompetInput(params: HashMap<String, Any>, init: TextView.(BillLineX?) -> Unit = {}) {
    billCompetLine(params, 7, {
        input(params, -1, init)
    }, {
        input(params, 2, init)
    }).apply {
        params.put("billLine", this)
    }
}

fun <K, V> Map<K, V>.toHashMap(): HashMap<K, V> {
    return HashMap(this)
}

private fun Map<String, Any>.getText(key: String): String {
    return (get(key) as? String).orEmpty()
}

private fun Map<String, Any>.getBoolean(key: String, default: Boolean = false): Boolean {
    return (get(key) as? Boolean) ?: default
}

private fun Map<String, Any>.getInt(key: String, default: Int): Int {
    var i = get(key) as? Int
    if (i == null) {
        try {
            i = (get(key) as? String)?.toInt()
        } catch(e: Exception) {
        }
    }
    return i ?: default
}

private fun Map<String, Any>.getColor(context: Context, key: String, default: Any): Int {
    val get = get(key)
    when (get) {
        is String -> {
            if (get.isNullOrBlank()) return parseColor(default, context)
            var parseColor: Int = 0
            try {
                parseColor = Color.parseColor(get)
            } catch(e: Exception) {
            }
            return parseColor
        }
        is Int -> {
            try {
                return context.getResColor(get)
            } catch(e: Exception) {
                return get
            }
        }
        null -> {
            return parseColor(default, context)
        }
    }
    return 0
}

private fun parseColor(default: Any, context: Context): Int {
    when (default) {
        is String -> {
            var parseColor: Int = 0
            try {
                parseColor = Color.parseColor(default)
            } catch(e: Exception) {
            }
            return parseColor
        }
        is Int -> {
            try {
                return context.getResColor(default)
            } catch(e: Exception) {
                return default
            }
        }
    }
    return 0
}

fun ViewManager.input(params: HashMap<String, Any>, index: Int = 0, init: TextView.(BillLineX?) -> Unit = {}): EditText {
    val hz = if (index <= 0) "" else index.toString()
    return editText {
        this.hint = params.getText("hint$hz")
        setHintTextColor(params.getColor(context, "hintTextColor$hz", R.color.bill_line_hint))
        setText(params.getText("text$hz"))
        if (!params.getText("data$hz").isNullOrBlank()) {
            setText(params.getText("data$hz"))
        }
        setSingleLine(params.getBoolean("singleLine", true))

        setSelectAllOnFocus(params.getBoolean("selectAllOnFocus", false))

        val userInputType = params.getInt("inputType$hz", 0)
        when (userInputType) {
//            0 -> inputType = InputType.TYPE_NULL
            1 -> inputType = InputType.TYPE_CLASS_NUMBER
            2 -> inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
        }

        val editable = params.getBoolean("editable", true)
        isFocusable = editable


        val userMaxLength = params.getInt("maxLength$hz", 0)
        if (userMaxLength > 0) {
            filters = arrayOf(InputFilter.LengthFilter(userMaxLength))
        }

        id = ID_BILL_LINE_TEXT
        textColor = params.getColor(context, "textColor$hz", R.color.bill_line_text)
        textSizeDimen = R.dimen.bill_line_title
        backgroundColor = params.getColor(context, "backgroundColor$hz", 0xffffff)
        leftPadding = dip(params.getInt("leftPadding$hz", 10))
        val billLine = params["billLine"] as? BillLineX
        params["view$hz"] = this
        init(billLine)
    }.apply {
        topPadding = dip(15)
        bottomPadding = dip(15)
        val layoutParams = layoutParams as? LinearLayout.LayoutParams
        layoutParams?.width = matchParent
        //-1和2都是对比输入传进来的标识
        if (!params.getBoolean("singleLine", true) && index != -1 && index != 2) {
            layoutParams?.topMargin = dip(4)
            layoutParams?.bottomMargin = dip(4)
            layoutParams?.rightMargin = dip(10)
            gravity = Gravity.TOP
            padding = dip(10)
            backgroundResource = R.drawable.border_line_gray
            layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
            minimumHeight = dip(120)
        }
        val defineId = params["id"]?.toString() ?: "undefined"
        if (!defineId.isNullOrBlank()) {
            tag = "edit_text_$defineId$hz"
        }
    }
}

fun ViewManager.billLine(params: HashMap<String, Any>, dataType: Int = 0, init: (BillLineX.() -> Unit) = {}) = ankoView({
    BillLineX(it, dataType, params)
}) {
    val defineId = params["id"]?.toString() ?: "undefined"
    isClickable = true
    val labelStr: String
    if (params.getText("label").isNullOrBlank()) {
        labelStr = ""
    } else {
        labelStr = "${params.getText("label")}："
    }
    textView(labelStr) {
        id = ID_BILL_LINE_TITLE
        textColor = params.getColor(context, "labelColor", R.color.bill_line_title)
        textSizeDimen = R.dimen.bill_line_title
    }.lparams {
        height = dip(50)
    }.apply {
        val layoutParams = layoutParams as? LinearLayout.LayoutParams
        layoutParams?.leftMargin = dip(15)
        layoutParams?.gravity = Gravity.TOP
        gravity = Gravity.CENTER
        if (!defineId.isNullOrBlank()) {
            tag = "label_$defineId"
        }
    }
    if (!defineId.isNullOrBlank()) {
        tag = defineId
    }
    init()
}.apply {
    setGravity(Gravity.CENTER_VERTICAL)
    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
}

fun ViewManager.billCompetLine(params: HashMap<String, Any>, dataType: Int = 0, init: (LinearLayout.() -> Unit) = {}, init2: (LinearLayout.() -> Unit) = {}) = ankoView({
    BillLineX(it, dataType, params)
}) {
    val defineId = params["id"]?.toString() ?: "undefined"
    if (!defineId.isNullOrBlank()) {
        tag = defineId
    }
    isClickable = true

    linearLayout {
        val labelStr: String
        if (params.getText("label").isNullOrBlank()) {
            labelStr = ""
        } else {
            labelStr = "${params.getText("label")}："
        }
        textView(labelStr) {
            id = ID_BILL_LINE_TITLE
            textColor = params.getColor(context, "labelColor", R.color.bill_line_title)
            textSizeDimen = R.dimen.bill_line_title
        }.lparams {
            height = dip(50)
        }.apply {
            val layoutParams = layoutParams as? LinearLayout.LayoutParams
            layoutParams?.leftMargin = dip(10)
            layoutParams?.gravity = Gravity.TOP
            gravity = Gravity.CENTER
            if (!defineId.isNullOrBlank()) {
                tag = "label_$defineId"
            }
        }

        init()
    }.lparams {
        weight = 1f
        width = 0
    }

    linearLayout {
        val labelStr2: String
        if (params.getText("label2").isNullOrBlank()) {
            labelStr2 = ""
        } else {
            labelStr2 = "${params.getText("label2")}："
        }
        textView(labelStr2) {
            id = ID_BILL_LINE_TITLE
            textColor = params.getColor(context, "labelColor2", R.color.bill_line_title)
            textSizeDimen = R.dimen.bill_line_title
        }.lparams {
            height = dip(50)
        }.apply {
            val layoutParams = layoutParams as? LinearLayout.LayoutParams
            layoutParams?.leftMargin = dip(5)
            layoutParams?.gravity = Gravity.TOP
            gravity = Gravity.CENTER
            if (!defineId.isNullOrBlank()) {
                tag = "label_${defineId}2"
            }
        }

        init2()
    }.lparams {
        weight = 1f
        width = 0
        gravity = Gravity.TOP
    }

}.apply {
    setGravity(Gravity.CENTER_VERTICAL)
    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
}

/**
 * 单据日期时间选择
 */
fun ViewManager.billDate(vararg pair: Pair<String, Any>) {
    return billDate(pair.toMap().toHashMap())
}

/**
 * 单据日期时间选择
 */
fun ViewManager.billDate(params: HashMap<String, Any>, init: (TextView.(BillLineX?) -> Unit) = {}) {
    billLine(params, 1) {
//        val hashMap = params.toHashMap()
        params.put("billLine", this)
        date(params, init)
    }
}

fun ViewManager.date(params: HashMap<String, Any>, init: TextView.(BillLineX?) -> Unit = {}): LinearLayout = touchView(params, {
    onClick {
        val editable = params.getBoolean("editable", true)
        if (!editable) return@onClick
        var type = TimePickerView.Type.ALL
        val typeData = params["type"]
        if (typeData is TimePickerView.Type) {
            type = typeData
        } else if (typeData is String) {
            try {
                type = TimePickerView.Type.valueOf(typeData)
            } catch(e: Exception) {
            }
        }
        val pvTime = TimePickerView(it?.context, type)
        //        var calendar = Calendar.getInstance();
        //        pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));
        pvTime.setTime(Date())
        pvTime.setCyclic(true)
        pvTime.setCancelable(true)
        var format = params.getText("format")
        if (format.isNullOrBlank()) {
            format = "yyyy-MM-dd HH:mm"
        }
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        if (!this@touchView.text.isNullOrEmpty()) {
            val dataStr = this@touchView.text.toString()
            try {
                pvTime.setTime(sdf.parse(dataStr))
            } catch(e: Exception) {
            }
        }
        pvTime.setOnTimeSelectListener({ this@touchView.text = sdf.format(it ?: Date()) })
        pvTime.show()
    }
    init(params["billLine"] as? BillLineX)
})

inline fun ViewManager.datatextView(init: DataTextView.() -> Unit): DataTextView {
    return ankoView({ ctx -> DataTextView(ctx) }) { init() }
}

fun ViewManager.touchView(params: HashMap<String, Any>, init: (DataTextView.(BillLineX?) -> Unit) = {}): LinearLayout {
    return linearLayout {
        datatextView {
            this.hint = params.getText("hint")
            this.setHintTextColor(params.getColor(context, "hintTextColor", R.color.bill_line_hint))
            this.text = params.getText("text")
            if (!params.getText("data").isNullOrBlank()) {
                setText(params.getText("data"))
            }
            id = ID_BILL_LINE_TEXT
            textColor = params.getColor(context, "textColor", R.color.bill_line_text)
            textSizeDimen = R.dimen.bill_line_title
            backgroundColor = 0xffffff
            ellipsize = TextUtils.TruncateAt.END
            singleLine = true
            gravity = Gravity.CENTER_VERTICAL
            params["view"] = this
            init(params["billLine"] as? BillLineX)
            val defineId = params["id"]?.toString() ?: "undefined"
            if (!defineId.isNullOrBlank()) {
                tag = "text_view_$defineId"
            }
        }.lparams {
            leftPadding = dip(10)
            weight = 1f
            height = matchParent
        }
        imageView {
            setImageResource(R.drawable.arrow_right)
        }.lparams {
            width = dip(7)
            height = matchParent
        }.apply {
            val layoutParams = layoutParams as? LinearLayout.LayoutParams
            layoutParams?.leftMargin = dip(10)
            layoutParams?.rightMargin = dip(12)
        }
    }.apply {
        layoutParams.width = matchParent
        layoutParams.height = matchParent
    }
}

fun ViewManager.billTouchLine(vararg pair: Pair<String, Any>) {
    return billTouchLine(pair.toMap().toHashMap())
}

fun ViewManager.billTouchLine(params: HashMap<String, Any>, init: (TextView.(BillLineX?) -> Unit) = {}) {
    billLine(params, 0) {
        params.put("billLine", this)
//        val hashMap = params.toHashMap()
        touchView(params, init)
    }
}

fun ViewManager.billSelect(vararg pair: Pair<String, Any>, init: (TextView.(BillLineX) -> Unit) = {}) {
    billSelect(pair.toMap().toHashMap())
}

fun ViewManager.billSelect(params: HashMap<String, Any>, init: (TextView.(BillLineX) -> Unit) = {}) {
    billLine(params, 2) {
        params.put("billLine", this)
        select(params, init)
    }
}

class ID_VALUE(val id: String, val value: String) : Serializable

fun ViewManager.select(params: HashMap<String, Any>, init: (TextView.(BillLineX) -> Unit) = {}): LinearLayout = touchView(params, {
    this@touchView.text = params.getText("text")
    onClick {
        val editable = params.getBoolean("editable", true)
        if (!editable) return@onClick
        val list = params["list"]
        val ID_VALUE_List: List<ID_VALUE>
        val valueList: List<String>
        if (list is Array<*>) {
            ID_VALUE_List = list.map { obj ->
                Gson().fromJson(Gson().toJson(obj), ID_VALUE::class.java)
            }.filter { obj ->
                obj != null
            }
            valueList = ID_VALUE_List.map { obj ->
                obj.value
            }
        } else {
            ID_VALUE_List = (list as? List<*>)?.map { obj ->
                Gson().fromJson(Gson().toJson(obj), ID_VALUE::class.java)
            }?.filter { obj ->
                obj != null
            }.orEmpty()
            valueList = ID_VALUE_List.map { obj ->
                obj.value
            }.orEmpty()
        }
        AlertView(params.getText("label"), null, "取消", null, valueList.toTypedArray(), context, AlertView.Style.ActionSheet, { obj, index ->
            Log.i("xxx", "$obj, $index")
            if (index != -1) {
                this@touchView.text = valueList.get(index)
                this@touchView.data = ID_VALUE_List.get(index).id
//                this@touchView.tag = arrayListOf(items?.get(index)?.first)
            }
        }).show()
    }
    this@touchView.id = ID_BILL_LINE_SELECT
//    init(billLine)
})


fun ViewManager.billListSelect(vararg pair: Pair<String, Any>) {
    billListSelect(pair.toMap().toHashMap())
}

fun ViewManager.billListSelect(params: HashMap<String, Any>) {
    billLine(params, 3) {
        listSelect(params)
    }
}

fun ViewManager.listSelect(params: HashMap<String, Any>): LinearLayout = touchView(params, {
    val owner = context
    if (owner is BaseActivity) {
        owner.activityResultListeners.add({ requestCode, resultCode, data ->
            if (Activity.RESULT_OK == resultCode && this@listSelect.hashCode() and 0xffff == requestCode) {
                // todo ..
                val resultData = data?.getSerializableExtra("data")
                resultData?.let {
//                    params["data"] = it
                    if (it is ID_VALUE) {
                        this@touchView.setValue(it.id, it.value)
                    }
                }
            }
        })
        onClick {
            val url = params["dataUrl"]
            if (url == null) {
                owner.toast("数据获取地址不存在")
                return@onClick
            }
            Logger.i(url.toString())
            owner.startActivityForResult<SimpleBillDataSelectActivity>(this@listSelect.hashCode() and 0xffff, "url" to url, "title" to params.getText("label"), "multiselect" to params.getBoolean("multiselect"))
        }
    }

})


fun ViewManager.billCamera(vararg pair: Pair<String, Any>, init: (CameraLayout.(BillLineX?) -> Unit) = {}) {
    billCamera(pair.toMap().toHashMap(), init)
}

fun ViewManager.billCamera(params: HashMap<String, Any>, init: (CameraLayout.(BillLineX?) -> Unit) = {}) {
    billLine(params, 4) {
        camera(params, init)
    }
}

fun ViewManager.camera(params: HashMap<String, Any>, init: CameraLayout.(BillLineX?) -> Unit = {}): CameraLayout = ankoView({ CameraLayout(it) }) {
    this.editable = params.getBoolean("editable", true)
    this.params = params
    params["view"] = this
    id = ID_BILL_LINE_IMAGE
    init(params["billLine"] as? BillLineX)
}.apply {
    layoutParams.width = matchParent
    topPadding = dip(10)
    bottomPadding = dip(10)
    leftPadding = dip(5)
    rightPadding = dip(10)
    layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
    minimumHeight = dip(80)
}

/**
 * 使用的LinearLayout
 */
fun ViewManager.billAttachment(vararg pair: Pair<String, Any>, init: (AttachmentLayout.(BillLineX?) -> Unit) = {}) {
    billAttachment(pair.toMap().toHashMap(), init)
}
fun ViewManager.billAttachment(params: HashMap<String, Any>, init: (AttachmentLayout.(BillLineX?) -> Unit) = {}) {
    billLine(params, 9) {
        attachment(params, init)
    }
}

fun ViewManager.attachment(params: HashMap<String, Any>, init: AttachmentLayout.(BillLineX?) -> Unit = {}): AttachmentLayout = ankoView({ AttachmentLayout(it) }) {
    this.params = params //必须先赋值params 进行初始化数据
    this.editable = params.getBoolean("editable", true)
    params["view"] = this
    id = ID_BILL_LINE_ATTACH
    init(params["billLine"] as? BillLineX)
}.apply {
    layoutParams.width = matchParent
    leftPadding = dip(10)
    rightPadding = dip(10)
    topPadding = dip(2)
    layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
}
fun ViewManager.billOpenAct(vararg pair: Pair<String, Any>) {
    billOpenAct(pair.toMap().toHashMap())
}

fun ViewManager.billOpenAct(params: HashMap<String, Any>) {
    billLine(params, 5) {
        openAct(params)
    }
}

fun ViewManager.openAct(params: HashMap<String, Any>): LinearLayout = touchView(params, {
    val owner = context
    if (owner is BaseActivity) {
        owner.activityResultListeners.add({ requestCode, resultCode, data ->
            if (Activity.RESULT_OK == resultCode && this@openAct.hashCode() and 0xffff == requestCode) {
                // todo ..
                val resultData = data?.getSerializableExtra("data")
                resultData?.let {
                    if (it is ID_VALUE) {
                        this@touchView.setValue(it.id, it.value)
                        params["finish"] = true
                    }
                }
            }
        })
        onClick {
            val funcid = params.getText("funcId")
            try {
                val function = FunctionEnum.valueOf(funcid)
                val intent = Intent(owner, function.activity)
                val jsonParam = params.get("jsonParam")
                if (jsonParam is Map<*, *>) {
                    for ((k, v) in jsonParam) {
                        intent.putExtra(k.toString(), v.toString())
                    }
                }
                owner.startActivityForResult(intent,this@openAct.hashCode() and 0xffff)
            } catch(e: Exception) {
                owner.toast("功能id未定义")
            }
        }

    }
})

fun ViewManager.billNewBill(vararg pair: Pair<String, Any>) {
    billNewBill(pair.toMap().toHashMap())
}

fun ViewManager.billNewBill(params: HashMap<String, Any>) {
    billLine(params, 6) {
        newBill(params)
    }
}

fun ViewManager.newBill(params: HashMap<String, Any>): LinearLayout = touchView(params, {
    val owner = context
    if (owner is BaseActivity) {
        owner.activityResultListeners.add({ requestCode, resultCode, data ->
            if (Activity.RESULT_OK == resultCode && this@newBill.hashCode() and 0xffff == requestCode) {
                val billData = data?.getSerializableExtra("billDefine") as? BillDefineX
                billData?.let {
                    params["billDefine"] = it
                }
                params["finish"] = true
//                Logger.i("VVVVVV_result" + billData)
            }
        })
        onClick {
            val def = params["billDefine"]
            if (def == null) {
                owner.toast("单据未定义")
                return@onClick
            }
            var billDefine: BillDefineX? = null
            if (def is String) {
                try {
                    billDefine = Gson().fromJson(def, BillDefineX::class.java)
                } catch(e: Exception) {
                }
            } else if (def is BillDefineX) {
                billDefine = def
            } else if (def is Map<*, *>) {
                val toJson = Gson().toJson(def)
                billDefine = Gson().fromJson(toJson, BillDefineX::class.java)
            }
            billDefine?.let {
                Logger.i("vvvvvvvvv" + this@newBill)
                owner.startActivityForResult<SimpleBillActivity>(this@newBill.hashCode() and 0xffff, "billDefine" to it)
            }

        }
    }

})

fun ViewManager.billPagerLine(vararg pair: Pair<String, Any>) {
    billPagerLine(pair.toMap().toHashMap())
}

fun ViewManager.billPagerLine(params: HashMap<String, Any>) {
    billLine(params, 8) {
        billPager(params)
    }
}

fun ViewManager.billPager(params: HashMap<String, Any>): LinearLayout = touchView(params, {
    val owner = context
    if (owner is BaseActivity) {
        owner.activityResultListeners.add({ requestCode, resultCode, data ->
            if (Activity.RESULT_OK == resultCode && this@billPager.hashCode() and 0xffff == requestCode) {
                val billData = data?.getSerializableExtra("billDefine") as? BillDefineX
                val billList = data?.getSerializableExtra("billList") as? List<BillDefineX>
                billData?.let {
                    params["billDefine"] = it
                }
                billList?.let {
                    val value = params["billList"] as? ArrayList<Any>
                    it.forEachIndexed { i, billDefineX ->
                        val get = value?.get(i)
                        if (get?.javaClass != billDefineX.javaClass) {
                            val gson = Gson()
                            val toJson = gson.toJson(billDefineX)
                            val fromJson = gson.fromJson(toJson, get?.javaClass)
                            value?.set(i, fromJson)
                        } else {
                            value?.set(i, billDefineX)
                        }
                    }
                }
                params["finish"] = true
                Logger.i("ppppp_result" + billData)
                Logger.i("ppppp_result" + billList)
            }
        })
        onClick {
            val def = params["billDefine"]
            val defList = params["billList"]
            if (def == null && defList == null) {
                owner.toast("单据未定义")
                return@onClick
            }
            var billDefine: BillDefineX? = null
            var billList: List<BillDefineX>? = null
            if (def is String) {
                try {
                    billDefine = Gson().fromJson(def, BillDefineX::class.java)
                } catch(e: Exception) {
                }
            } else if (def is BillDefineX) {
                billDefine = def
            } else if (def is Map<*, *>) {
                val toJson = Gson().toJson(def)
                billDefine = Gson().fromJson(toJson, BillDefineX::class.java)
            }
            if (defList is String) {
                try {
                    billList = Gson().fromJson(defList, object : TypeToken<List<BillDefineX>>() {}.type)
                } catch(e: Exception) {
                }
            } else if (defList is List<*>) {
                if (defList.size > 0 && defList[0] is Map<*, *>) {
                    val toJson = Gson().toJson(defList)
                    billList = Gson().fromJson(toJson, object : TypeToken<List<BillDefineX>>() {}.type)
                } else {
                    billList = defList as? List<BillDefineX>
                }
            }

            Logger.i("vvvvvvvvv" + this@billPager)
            val intent: Intent
            if (params.getBoolean("hasIndexPage", true)) {
                intent = Intent(owner, PagerBillIndexActivity::class.java)
                var title = params.getText("label")
                if (title.isNullOrBlank()) {
                    title = params.getText("text")
                }
                if (title.isNullOrBlank()) {
                    title = params.getText("hint")
                }
                intent.putExtra("title", title)
            } else {
                intent = Intent(owner, PagerBillActivity::class.java)
                val index = params.getInt("index", 0)
                intent.putExtra("index", index)
            }
            billDefine?.let {
                intent.putExtra("billDefine", it)
            }
            billList?.let {
                intent.putExtra("billList", ArrayList(it))
            }
            owner.startActivityForResult(intent, this@billPager.hashCode() and 0xffff)

        }
    }
})


//==========================================================================================================================================
fun ViewManager.billInput(title: String, hint: String = "", text: String = "", singleLine: Boolean = true
                          , defineId: String = "", init: (TextView.(BillLine) -> Unit) = {}) {
    billLine(title, 0, defineId) {
        input(hint, text, singleLine, init, this)
    }
}

fun ViewManager.billDate(title: String, type: TimePickerView.Type, format: String = "yyyy-MM-dd", hint: String = "", text: String = ""
                         , defineId: String = "", init: (TextView.(BillLine) -> Unit) = {}) {
    billLine(title, 0, defineId) {
        date(type, format, hint, text, init, this)
    }
}

fun ViewManager.billJump(title: String, hint: String = "", text: String = ""
                         , defineId: String = "", init: (TextView.(BillLine) -> Unit) = {}) {
    billLine(title, 0, defineId) {
        jump(hint, text, init, this)
    }
}

fun ViewManager.billCamera(title: String, editable: Boolean = true, defineId: String = "", init: (CameraLayout.(BillLine) -> Unit) = {}) {
    billLine(title, 3, defineId) {
        camera(editable, init, this)
    }
}

fun ViewManager.billSelect(title: String, hint: String = "", editable: Boolean = true, items: List<Pair<String, String>>?, defineId: String = "", init: (TextView.(BillLine) -> Unit) = {}) {
    billLine(title, 2, defineId) {
        select(title, hint, items, arrayOf(""), init, this)
    }
}

fun <T : _LinearLayout> T.title(title: String) {
    find<TextView>(ID_BILL_LINE_TITLE).text = "${title.toString()}："
}

fun ViewManager.billLine(title: String = "", dataType: Int = 0, defineId: String = "", init: (BillLine.() -> Unit) = {}) = ankoView({ BillLine(it, title, dataType, defineId) }) {
    isClickable = true
    textView("$title：") {
        id = ID_BILL_LINE_TITLE
        textColor = context.getResColor(R.color.bill_line_title)
        textSizeDimen = R.dimen.bill_line_title
        this@ankoView.titleView = this
    }.lparams {
        //        leftPadding = dip(15)
        height = dip(50)
    }.apply {
        val layoutParams = layoutParams as? LinearLayout.LayoutParams
        layoutParams?.leftMargin = dip(15)
        layoutParams?.gravity = Gravity.TOP
        gravity = Gravity.CENTER
    }
    if (!defineId.isNullOrBlank()) {
        tag = defineId
    }
    //    addItemDefine(title, defineId)
    init()
}.apply {
    setGravity(Gravity.CENTER_VERTICAL)
    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
}

fun <T : EditText> T.text(title: String) {
    find<EditText>(ID_BILL_LINE_TEXT).setText("$title")
}

fun <T : EditText> T.hint(hint: String) {
    find<EditText>(ID_BILL_LINE_TEXT).hint = "$hint"
}

fun ViewManager.input(hint: String = "", text: String = "", singleLine: Boolean = true, init: TextView.(BillLine) -> Unit = {}, billLine: BillLine): EditText {
    return editText {
        this.hint = "$hint"
        this.setHintTextColor(context.getResColor(R.color.bill_line_hint))
        setText("$text")
        setSingleLine(singleLine)
        id = ID_BILL_LINE_TEXT
        textColor = context.getResColor(R.color.bill_line_text)
        textSizeDimen = R.dimen.bill_line_title
        backgroundColor = 0xffffff
        leftPadding = dip(10)
        init(billLine)
    }.apply {
        topPadding = dip(0)
        bottomPadding = dip(0)
        val layoutParams = layoutParams as? LinearLayout.LayoutParams
        layoutParams?.width = matchParent
        if (!singleLine) {
            layoutParams?.topMargin = dip(4)
            layoutParams?.bottomMargin = dip(4)
            layoutParams?.rightMargin = dip(10)
            gravity = Gravity.TOP
            padding = dip(10)
            backgroundResource = R.drawable.border_line_gray
            layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
            minimumHeight = dip(120)
        }
    }
}


fun <T : LinearLayout> T.text(text: String) {
    find<TextView>(ID_BILL_LINE_TEXT).text = "$text"
}

fun <T : LinearLayout> T.hint(hint: String) {
    find<TextView>(ID_BILL_LINE_TEXT).hint = "$hint"
}


fun ViewManager.jump(hint: String = "", text: String = "", init: (TextView.(BillLine) -> Unit) = {}, billLine: BillLine): LinearLayout {
    return linearLayout {
        textView {
            this.hint = "$hint"
            this.setHintTextColor(context.getResColor(R.color.bill_line_hint))
            this.text = text
            id = ID_BILL_LINE_TEXT
            textColor = context.getResColor(R.color.bill_line_text)
            textSizeDimen = R.dimen.bill_line_title
            backgroundColor = 0xffffff
            ellipsize = TextUtils.TruncateAt.END
            singleLine = true
            gravity = Gravity.CENTER_VERTICAL
            init(billLine)
        }.lparams {
            leftPadding = dip(10)
            weight = 1f
            height = matchParent
        }
        imageView {
            setImageResource(R.drawable.arrow_right)
        }.lparams {
            width = dip(7)
            height = matchParent
        }.apply {
            val layoutParams = layoutParams as? LinearLayout.LayoutParams
            layoutParams?.leftMargin = dip(10)
            layoutParams?.rightMargin = dip(12)
        }
    }.apply {
        layoutParams.width = matchParent
        layoutParams.height = matchParent
    }
}

fun ViewManager.date(type: TimePickerView.Type, format: String = "yyyy-MM-dd", hint: String = "", text: String = "", init: TextView.(BillLine) -> Unit = {}, billLine: BillLine): LinearLayout = jump(hint, text, {
    onClick {
        val pvTime = TimePickerView(it?.context, type)
        //        var calendar = Calendar.getInstance();
        //        pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));
        pvTime.setTime(Date())
        pvTime.setCyclic(true)
        pvTime.setCancelable(true)
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        pvTime.setOnTimeSelectListener({ this@jump.text = sdf.format(it ?: Date()) })
        pvTime.show()
    }
    init(billLine)
}, billLine)


fun ViewManager.select(title: String, hint: String = "", items: List<Pair<String, String>>?, selected: Array<String> = arrayOf(), init: (TextView.(BillLine) -> Unit) = {}, billLine: BillLine): LinearLayout = jump(hint, "", {
    onClick {
        AlertView(title, null, "取消", null, items?.map { it.second }?.toTypedArray(), it?.context, AlertView.Style.ActionSheet, { obj, index ->
            Log.i("xxx", "$obj, $index")
            if (index != -1) {
                this@jump.text = items?.get(index)?.second
                this@jump.tag = arrayListOf(items?.get(index)?.first)
            }
        }).show()
    }
    this@jump.id = ID_BILL_LINE_SELECT
//    init(billLine)
}, billLine)


fun ViewManager.camera(editable: Boolean = true, init: CameraLayout.(BillLine) -> Unit = {}, billLine: BillLine): CameraLayout = ankoView({ CameraLayout(it) }) {
    this.editable = editable
    id = ID_BILL_LINE_IMAGE
    init(billLine)
}.apply {
    layoutParams.width = matchParent
    topPadding = dip(10)
    bottomPadding = dip(10)
    leftPadding = dip(5)
    rightPadding = dip(10)
    layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
    minimumHeight = dip(80)
}

fun ViewManager.billAttachment(title: String, editable: Boolean = true, defineId: String = "", init: (AttachmentLayout.(BillLine) -> Unit) = {}) {
    billLine(title, 4, defineId) {
        attachment(editable, init, this)
    }
}

fun ViewManager.attachment(editable: Boolean = true, init: AttachmentLayout.(BillLine) -> Unit = {}, billLine: BillLine): AttachmentLayout = ankoView({ AttachmentLayout(it) }) {
    this.editable = editable
    id = ID_BILL_LINE_ATTACH
    init(billLine)
}.apply {
    layoutParams.width = matchParent
    leftPadding = dip(5)
    rightPadding = dip(10)
    layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
}

val ID_BILL_LINE_ATTACH = 10005

val ID_BILL_LINE_TITLE = 10001
val ID_BILL_LINE_TEXT = 10002
val ID_BILL_LINE_SELECT = 10003
val ID_BILL_LINE_IMAGE = 10004

fun EditText.moneyInputFilter() {
    filters = arrayOf(MoneyFilter())
    addTextChangedListener(MoneyInput(this))
}
