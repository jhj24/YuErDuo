package com.jqyd.yuerduo.fragment.contacts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.constants.CustomerDetailActivity
import com.jqyd.yuerduo.activity.constants.StaffDetailActivity
import com.jqyd.yuerduo.activity.main.TopBar
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.fragment.BaseFragment
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.TreeHandleUtil
import com.nightfarmer.androidcommon.data.LocalData
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.activity_staff_choose.*
import kotlinx.android.synthetic.main.activity_staff_choose.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.fragment_contacts.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onTouch
import java.util.*

/**
 * 通讯录
 * Created by zhangfan on 2015/12/14.
 */
class ContactsFragment : BaseFragment() {
    private var customerListState = -1 //-1请求失败，0无数据，>0正常
    private var staffListState = -1 //-1请求失败，0无数据，>0正常

    override fun getTitle(): String {
        return "通讯录"
    }

    override fun getIconDefault(): Int {
        return R.drawable.main_contacts_0
    }

    override fun getIconSelected(): Int {
        return R.drawable.main_contacts_1
    }

    override fun doWithTopBar(topBar: TopBar) {
        super.doWithTopBar(topBar)
        topBar.contactsRadioGroup.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_contacts, container, false)
        TypefaceHelper.typeface(inflate)
        initView(inflate)
        requestContacts()
        inflate.searchBarMask.onClick { onSearchBarMaskClick() }
        inflate.contactsList.onTouch { view, motionEvent -> OnListTouch() }
        inflate.reload.onClick { onRetry() }
        return inflate
    }

    override fun onInvisible() {
        super.onInvisible()
        if (searchBarMask != null) {
            et_search?.setText("")
            searchBarMask?.visibility = View.VISIBLE
        }
    }

    internal var inputMethodManager: InputMethodManager? = null

    fun getInputMethodManager(): InputMethodManager? {
        if (inputMethodManager == null) {
            inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        }
        return inputMethodManager
    }

    fun onSearchBarMaskClick() {
        searchBarMask?.visibility = View.GONE
        getInputMethodManager()?.showSoftInput(et_search, 0)
    }

    fun OnListTouch(): Boolean {
        getInputMethodManager()?.hideSoftInputFromWindow(et_search?.windowToken, 0)
        return false
    }

    private var sortListView: ListView? = null
    private var sideBar: SideBar? = null
    private var dialog: TextView? = null
    private var adapter: SortAdapter? = null

    private var et_search: EditText? = null


    /**
     * 汉字转换成拼音的类
     */
    lateinit var characterUtil: CharacterUtil
    private var SourceDateList_staff: MutableList<SortModel>? = ArrayList()
    private var SourceDateList_customer: MutableList<SortModel>? = ArrayList()

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private var pinyinComparator: PinyinComparator? = null


    fun initView(view: View) {
        // 实例化汉字转拼音类
        characterUtil = CharacterUtil.getInstance()
        pinyinComparator = PinyinComparator()

        sideBar = view.findViewById(R.id.sidebar) as? SideBar
        dialog = view.findViewById(R.id.dialog) as? TextView
        et_search = view.findViewById(R.id.et_search) as? EditText
        sideBar?.setTextView(dialog)

        // 设置右侧触摸监听
        sideBar?.setOnTouchingLetterChangedListener { s ->
            // 该字母首次出现的位置
            val position = adapter?.getPositionForSection(s[0].toInt())
            if (position != -1) {
                sortListView?.setSelection(position ?: 0)
            }
        }

        sortListView = view.findViewById(R.id.contactsList) as? ListView
        sortListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
            val item = adapter?.getItem(position) as SortModel
            val intent: Intent
            if (item.type == 1) {
                intent = Intent(activity, CustomerDetailActivity::class.java)
            } else {
                intent = Intent(activity, StaffDetailActivity::class.java)
            }
            intent.putExtra("contactsDetail", item)
            startActivity(intent)
        }

        adapter = SortAdapter(context, SourceDateList_staff)
        sortListView?.adapter = adapter


        // 根据输入框输入值的改变来过滤搜索
        et_search?.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    /**
     * 给员工名称进行拼音处理

     * @return
     */
    private fun filledData(dataList: List<SortModel>, type: Int) {
        for (i in dataList.indices) {
            val sortModel = dataList[i]
            sortModel.type = type
            // 汉字转换成拼音
            //            Logger.json(sortModel.toGson());
            //val pinyin = characterParser?.getSelling(sortModel.name)
            val spelling = characterUtil.getStringSpelling(sortModel.name, true)
            val allSpelling = characterUtil.getStringSpelling(sortModel.name, false)
            val sortString = spelling?.substring(0, 1)?.toUpperCase()

            // 正则表达式，判断首字母是否是英文字母
            if (sortString?.matches("[A-Z]".toRegex()) ?: false) {
                sortModel.sortLetters = spelling?.toUpperCase()
            } else {
                sortModel.sortLetters = "#" + allSpelling
            }
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     */
    private fun filterData() {
        val filterStr = et_search?.text.toString().trim()
        var filterDateList: MutableList<SortModel>? = ArrayList()
        val dataSource: MutableList<SortModel>?
        if (contactsType == 0) {
            dataSource = SourceDateList_staff
        } else {
            dataSource = SourceDateList_customer
        }

        if (filterStr.isNullOrEmpty()) {
            filterDateList = dataSource
        } else {
            filterDateList?.clear()
            dataSource?.forEach { sortModel ->
                val name = sortModel.name
                if (name.contains(filterStr) || TreeHandleUtil.isFilter(characterUtil,filterStr,name)) {
                    filterDateList?.add(sortModel)
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator)
        adapter?.updateListView(filterDateList)
    }


    private var contactsType: Int = 0

    fun changeContactsType(type: Int) {
        contactsType = type
        if (0 == type) {
            if (adapter == null) adapter = SortAdapter(context, SourceDateList_staff)
            adapter?.updateListView(SourceDateList_staff)
            if (staffListState > 0) {
                view?.state_mask?.visibility = View.GONE
            } else if (staffListState < 0) {
                view?.state_mask?.visibility = View.VISIBLE
                view?.tv_mask?.text = "加载失败"
                view?.bt_reload?.visibility = View.VISIBLE
            } else {
                bt_reload.visibility = View.INVISIBLE
                tv_mask.text = "没有联系人"
            }
        } else {
            if (adapter == null) adapter = SortAdapter(context, SourceDateList_customer)
            adapter?.updateListView(SourceDateList_customer)
            if (customerListState > 0) {
                state_mask?.visibility = View.GONE
            } else if (customerListState < 0) {
                state_mask?.visibility = View.VISIBLE
                tv_mask?.text = "加载失败"
                bt_reload?.visibility = View.VISIBLE
            } else {
                bt_reload?.visibility = View.INVISIBLE
                tv_mask?.text = "没有联系人"
            }
        }
        filterData()
    }

    fun onRetry() {
        if (0 == contactsType) {
            requestStaff()
        } else {
            requestCustomer()
        }
    }

    override fun onVisible() {
        super.onVisible()
        requestContacts()
    }

    fun requestContacts() {
        requestCustomer()
        requestStaff()
    }

    internal var staffDataCalling = false

    private fun requestStaff() {
        val StaffLocalData = LocalData(activity, StaffLocal)
        if (!staffDataCalling) {
            staffDataCalling = true
            HttpCall.request(context, URLConstant.CONTACTS_STAFF, null, object : GsonHttpCallback<SortModel>() {
                override fun onSuccess(result: ResultHolder<SortModel>) {
                    val dataList = result.dataList
                    filledData(dataList, 0)
                    StaffLocalData.put(ArrayList(dataList))
                    setContactsData(dataList)
                }

                override fun onFailure(msg: String, errorCode: Int) {
                    val dataList = StaffLocalData.get<ArrayList<*>>(ArrayList::class.java)
                    if (dataList == null) {
                        staffListState = -1
                        Toast.makeText(this@ContactsFragment.context, "员工通讯录加载失败", Toast.LENGTH_SHORT).show()
                    } else {
                        setContactsData(dataList as? MutableList<SortModel>)
                    }
                }

                private fun setContactsData(dataList: MutableList<SortModel>?) {
                    SourceDateList_staff = dataList
                    Collections.sort(SourceDateList_staff, pinyinComparator)
                    staffListState = SourceDateList_staff?.size ?: 0
                    changeContactsType(contactsType)
                }

                override fun onFinish() {
                    super.onFinish()
                    staffDataCalling = false
                }
            })

        }
    }

    internal var customerDataCalling = false

    private fun requestCustomer() {
        val CustomerLocalData = LocalData(activity, CustomerLocal)
        if (!customerDataCalling) {
            customerDataCalling = true
            HttpCall.request(context, URLConstant.AllCustomer, null, object : GsonHttpCallback<SortModel>() {
                override fun onSuccess(result: ResultHolder<SortModel>) {
                    val dataList = result.dataList
                    filledData(dataList, 1)
                    CustomerLocalData.put(ArrayList(dataList))
                    setContactsData(dataList)
                }

                override fun onFailure(msg: String, errorCode: Int) {
                    val dataList = CustomerLocalData.get<ArrayList<*>>(ArrayList::class.java)
                    if (dataList == null) {
                        customerListState = -1
                        Toast.makeText(this@ContactsFragment.context, "客户通讯录加载失败", Toast.LENGTH_SHORT).show()
                    } else {
                        setContactsData(dataList as? MutableList<SortModel>)
                    }
                }

                private fun setContactsData(dataList: MutableList<SortModel>?) {
                    SourceDateList_customer = dataList
                    Collections.sort(SourceDateList_customer, pinyinComparator)
                    customerListState = SourceDateList_customer?.size ?: 0
                    changeContactsType(contactsType)
                }

                override fun onFinish() {
                    super.onFinish()
                    customerDataCalling = false
                }
            })
        }
    }

    companion object {

        private val StaffLocal = "StaffLocal"
        private val CustomerLocal = "CustomerLocal"
    }
}
