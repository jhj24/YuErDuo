package com.jqyd.yuerduo.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.jqyd.yuerduo.MyApplication
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.FunctionBean
import com.norbsoft.typefacehelper.TypefaceHelper
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_main_function_add.*
import kotlinx.android.synthetic.main.layout_grid_item_function_add.view.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import java.util.*

/**
 * Created by liushiqi on 2016/8/11 0011.
 * 常用功能界面
 */
class MainFunctionAddActivity : BaseActivity() {
    var allFunctions: ArrayList<FunctionBean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_function_add)
        topBar_right_button.visibility = View.VISIBLE
        topBar_title.text = "常用功能"
        //获取已经选择的功能列表
        val selectedList = intent.getSerializableExtra("functionList") as ArrayList<FunctionBean>
        //获取全部的功能列表
        allFunctions = getAllFunctions((application as MyApplication).allFunctionCopies, selectedList)
        //使用RecyclerView以瀑布流实现界面显示
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = MyAdapter(allFunctions)
        recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(this).color(0x00000000).size(30).build())

        topBar_right_button.onClick {
            val dataList: ArrayList<FunctionBean> = ArrayList()
            for (functionBean in allFunctions) {
                if (functionBean.checked && functionBean.levels != 1) {
                    dataList.add(functionBean)
                }
            }
            intent.putExtra("dataList", dataList);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    fun getAllFunctions(functionBeanList: List<FunctionBean>, selectedList: ArrayList<FunctionBean>?): ArrayList<FunctionBean> {
        val result = ArrayList<FunctionBean>()
        val functionDel: ArrayList<FunctionBean> = ArrayList()
        for (function: FunctionBean in functionBeanList) {
            result.add(function)
            if (function.children.size != 0) {
                var allChecked = true
                for (child in function.children) {
                    if (isSplitItem(child)) {
                        functionDel.add(child)
                        continue
                    }
                    child.levels = 2
                    if (selectedList == null) continue
                    for (selectedFunc in selectedList) {
                        if (child == selectedFunc) {
                            child.checked = true
                            break
                        }
                    }
                    allChecked = allChecked && child.checked
                }
                function.checked = allChecked
                function.children.removeAll(functionDel)
                result.addAll(function.children)
            }
        }
        return result
    }

    inner class MyAdapter(functionBeanList: List<FunctionBean>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        var functionBeanList: List<FunctionBean>

        init {
            this.functionBeanList = functionBeanList
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val layoutInflater: LayoutInflater = LayoutInflater.from(this@MainFunctionAddActivity)
            val view: View
            if (viewType == 1) {
                view = layoutInflater.inflate(R.layout.layout_grid_title_function_add, parent, false)
            } else {
                view = layoutInflater.inflate(R.layout.layout_grid_item_function_add, parent, false)
            }
            val layoutParams = StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = layoutParams
            TypefaceHelper.typeface(view)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val functionBean = functionBeanList[position]
            with(holder) {
                if (isSplitItem(functionBean)) {
                    itemView.visibility = View.GONE
                } else {
                    val layoutParams = itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                    if (functionBeanList[position].levels == 1) {
                        layoutParams.isFullSpan = true
                    } else {
                        layoutParams.isFullSpan = false
                    }
                    itemView.layoutParams = layoutParams
                    textView.tag = functionBean
                    itemView.visibility = View.VISIBLE
                    textView.text = functionBean.funcTitle
                    if (functionBean.levels != 1) {
                        textView.isChecked = functionBean.checked
                    }
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return functionBeanList[position].levels
        }

        override fun getItemCount(): Int {
            return functionBeanList.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textView: CheckBox

            init {
                textView = itemView.tv_title
                textView.setOnCheckedChangeListener { buttonView, isChecked ->
                    val func = buttonView.tag as FunctionBean
                    val preCheck = func.checked
                    func.checked = isChecked
                    if (func.levels == 1 && func.children != null) {
                        func.checked = !preCheck
                        val itemCount = func.children.size
                        for (i in 0..itemCount - 1) {
                            func.children[i].checked = func.checked
                        }
                        if (itemCount > 0) {
                            val from = functionBeanList.indexOf(func) + 1
                            notifyItemRangeChanged(from, itemCount)
                        }
                    }
                }
            }
        }
    }

    fun isSplitItem(item: FunctionBean?): Boolean {
        return item == null || "----".equals(item.funcTitle)
    }
}
