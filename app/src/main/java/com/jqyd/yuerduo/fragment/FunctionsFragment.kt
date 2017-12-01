package com.jqyd.yuerduo.fragment

import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.MyApplication
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.main.TopBar
import com.jqyd.yuerduo.adapter.FunctionPageAdapter
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import kotlinx.android.synthetic.main.fragment_functions.view.*
import java.io.File
import java.util.*

/**
 * 主界面第三页
 * Created by zhangfan on 2015/12/14.
 */
class FunctionsFragment : BaseFragment() {
    private var adapter: FunctionPageAdapter? = null

    override fun getTitle(): String {
        return "功能"
    }

    override fun getIconDefault(): Int {
        return R.drawable.main_function0
    }

    override fun getIconSelected(): Int {
        return R.drawable.main_function1
    }


    override fun doWithTopBar(topBar: TopBar) {
        super.doWithTopBar(topBar)
        topBar.contactsRadioGroup.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    internal var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_functions, container, false)
        recyclerView = inflate.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(context)
        initData()

//        val sdPath: File
//        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {// SD卡
//            sdPath = Environment.getExternalStorageDirectory()
//        } else {// 内存
//            sdPath = activity.getFilesDir()
//        }
//
//        val file = File(sdPath, "did")
//        val ff = HashMap<String, List<File>>()
//        ff.put("keyList1", arrayListOf(file, file))
//        ff.put("keyList2", arrayListOf(file, file))
//        HttpCall.post(activity, URLConstant.FUNCTION_LIST, null, ff, object : GsonHttpCallback<BaseBean>() {
//            override fun onFailure(msg: String, errorCode: Int) {
//                Log.i("xxx", "$msg,$errorCode")
//            }
//
//            override fun onSuccess(result: ResultHolder<BaseBean>) {
//                Log.i("xxx", "success")
//            }
//        })


        return inflate
    }

    override fun onDataChanged() {
        super.onDataChanged()
        initData()
    }

    private fun initData() {
        activity?.let {
            if (adapter == null) {
                adapter = FunctionPageAdapter()
                recyclerView?.adapter = adapter
            }
            adapter?.dataList = (activity.application as MyApplication).allFunction
            adapter?.notifyDataSetChanged()
        }
    }
}
