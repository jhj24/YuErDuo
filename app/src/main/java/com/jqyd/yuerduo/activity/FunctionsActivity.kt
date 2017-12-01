package com.jqyd.yuerduo.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.FunctionsActivity.FunctionListAdapter.FunctionViewHolder
import com.jqyd.yuerduo.bean.FunctionBean
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.activity_functions.*
import kotlinx.android.synthetic.main.layout_list_item_function_small.view.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import java.util.*

/**
 * 功能选择
 * Created by jianhaojie on 2016/8/11.
 */
class FunctionsActivity : BaseActivity() {

    var adapter: FunctionListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_functions)

        val parent = intent.getSerializableExtra("function") as FunctionBean

        topBar_title.text = parent.funcTitle

        recyclerView.layoutManager = LinearLayoutManager(this)
        if (adapter == null) {
            adapter = FunctionListAdapter()
            recyclerView.adapter = adapter
        }
        adapter?.dataList = parent.children
        adapter?.notifyDataSetChanged()


    }


    inner class FunctionListAdapter() : RecyclerView.Adapter<FunctionViewHolder>() {

        var dataList: List<FunctionBean> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FunctionViewHolder? {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_function_small, parent, false)
            TypefaceHelper.typeface(view)
            return FunctionViewHolder(view)
        }

        override fun onBindViewHolder(holder: FunctionViewHolder, position: Int) {
            val functionBean = dataList[position]
            holder.functionBean = functionBean
            with(holder.itemView) {
                if (!isSplitItem(functionBean)) {
                    functionBean.bindImageView(functionImage)
                    tv_title?.text = functionBean.funcTitle
                    var pre: FunctionBean? = null
                    var next: FunctionBean? = null
                    if (position > 0)
                        pre = dataList[position - 1]
                    if (position < dataList.size - 1)
                        next = dataList[position + 1]
                    if (isSplitItem(pre)) {
                        line_top.visibility = View.VISIBLE
                    } else {
                        line_top.visibility = View.GONE
                    }
                    if (!isSplitItem(next)) {
                        line_middle.visibility = View.VISIBLE
                        line_buttom.visibility = View.GONE
                    } else {
                        line_middle.visibility = View.GONE
                        line_buttom.visibility = View.VISIBLE
                    }
                    red_dot.visibility = View.GONE
                } else {
                    val layoutParams = holder.itemView.layoutParams
                    layoutParams.height = 20
                    holder.itemView.layoutParams = layoutParams
                    holder.itemView.visibility = android.view.View.INVISIBLE
                }
            }
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        private fun isSplitItem(item: FunctionBean?): Boolean {
            return item == null || "----" == item.funcTitle
        }

        inner class FunctionViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
            var functionBean: FunctionBean? = null

            init {
                itemview.list_item.onClick {
                    functionBean?.startActivity(this@FunctionsActivity)
                }

            }
        }
    }

}