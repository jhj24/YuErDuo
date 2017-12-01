package com.jqyd.yuerduo.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.MainFunctionAddActivity
import com.jqyd.yuerduo.bean.FunctionBean
import com.jqyd.yuerduo.bean.FunctionNumBean
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.fragment.MainFragment
import com.jqyd.yuerduo.widget.RecyclerViewDraggableAdapter
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.layout_grid_item_function.view.*
import org.jetbrains.anko.onClick
import java.util.*

/**
 * 首页adapter
 * Created by liushiqi on 2016/8/23 0023.
 */
open class MainPageGridAdapter(fragment: Fragment) : RecyclerViewDraggableAdapter<MainPageGridAdapter.MyViewHolder>() {

    var dataList = ArrayList<FunctionBean>()
        get() = field
        set(value) {
            field = value
        }
    var functionNumBean: FunctionNumBean? = null
        get() = field
        set(value) {
            field = value
//            notifyDataSetChanged()
            if (!isEditState) {
                for (viewHolder in holderList) {
                    val functionBean = viewHolder.functionBean
                    if (functionBean?.funcName != null) {
                        viewHolder.bindNumber(functionBean?.funcName ?: "", true)
                    }
                }
            }
        }
    var fragment: Fragment? = null

    init {
        this.fragment = fragment
    }

    override fun setEditState(editState: Boolean) {
        super.setEditState(editState)
        for (viewHolder in holderList) {
            val itemView = viewHolder.itemView
            if (editState) {
                executeAnimation(itemView.bt_delete, 1)
            } else {
                executeAnimation(itemView.bt_delete, 2)

            }
            if (editState) {
                executeAnimation(itemView.tv_number_total, 4)
            } else {
                val functionBean = viewHolder.functionBean
                if (functionBean?.funcName != null) {
                    viewHolder.bindNumber(functionBean?.funcName ?: "", true)
                }
            }
        }
    }

    val holderList = arrayListOf<MyViewHolder>()

    override fun getDataList(): List<*>? {
        return dataList
    }

    override fun getContext(): Context? {
        return fragment?.context
    }

    override fun getItemCount(): Int {
        return dataList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == dataList.size) -1 else 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            val functionBean = dataList[position]
            holder.bindData(functionBean)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder? {
        val inflate = LayoutInflater.from(parent?.context).inflate(R.layout.layout_grid_item_function, parent, false)
        val myViewHolder = MyViewHolder(inflate, viewType)
        TypefaceHelper.typeface(inflate)
        holderList.add(myViewHolder)
        return myViewHolder
    }

    inner class MyViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        var viewType: Int = 0
        var title: TextView? = null
        var functionBean: FunctionBean? = null

        init {
            this.viewType = viewType
            if (0 == viewType) {
                itemView.item_function.visibility = View.VISIBLE
                itemView.item_add.visibility = View.GONE
            } else {
                itemView.item_function.visibility = View.GONE
                itemView.item_add.visibility = View.VISIBLE
            }

            itemView.grid_item.onClick {
                if (0 == viewType) {
                    if (isEditState) return@onClick
                    val context = itemView.context
                    functionBean?.startActivity(context)
                    return@onClick
                }
                val intent = Intent(itemView.context, MainFunctionAddActivity::class.java)
                intent.putExtra("functionList", dataList)
                fragment?.startActivityForResult(intent, 100)
            }
            itemView.bt_delete.onClick {
                val indexOf = dataList.indexOf(functionBean)
                if (indexOf != -1) {
                    onItemDismiss(indexOf)
                    if (!itemView.tv_number_total.text.toString().isNullOrEmpty()) {
                        var num = functionNumBean?.sum(dataList)
                        (itemView.context as MainFragment.IUpdateBottomBarNumListener)?.update(num!!, true)
                    }
                }
            }
        }

        fun bindData(functionBean: FunctionBean) {
            this.functionBean = functionBean
            itemView.tv_title.text = functionBean.funcTitle
            functionBean.bindImageView(itemView.functionImage)
            itemView.bt_delete.visibility = if (isEditState) View.VISIBLE else View.GONE
            if (!isEditState && functionNumBean != null) {
                bindNumber(functionBean.funcName, false)
            } else {
                itemView.tv_number_total.visibility = View.GONE
            }
        }

        /**
         * 根据功能模块名字，显示对应的未读数量
         * @param functionName 功能模块名字
         */
        fun bindNumber(functionName: String, isHasAnnotation: Boolean) {
            when (functionName) {
                FunctionName.VisitApprove -> {
                    itemView.tv_number_total.visibility = if (functionNumBean?.visitApproveNum ?: 0 > 0) {
                        if (functionNumBean?.visitApproveNum!! > 99) {
                            itemView.tv_number_total.text = "99+"
                        } else {
                            itemView.tv_number_total.text = functionNumBean?.visitApproveNum.toString()
                        }
                        if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 3)
                        View.VISIBLE
                    } else {
                        itemView.tv_number_total.text = ""
                        if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 4)
                        View.GONE
                    }
                }
                FunctionName.VisitRecord -> {
                    itemView.tv_number_total.visibility = if (functionNumBean?.myVisitRecordNum ?: 0 > 0) {
                        if (functionNumBean?.myVisitRecordNum!! > 99) {
                            itemView.tv_number_total.text = "99+"
                        } else {
                            itemView.tv_number_total.text = functionNumBean?.myVisitRecordNum.toString()
                        }
                        if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 3)
                        View.VISIBLE
                    } else {
                        itemView.tv_number_total.text = ""
                        if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 4)
                        View.GONE
                    }
                }
                FunctionName.LeaveList -> {
                    when (functionBean?.type) {
                        "0" -> itemView.tv_number_total.visibility = if (functionNumBean?.myLeaveNum ?: 0 > 0) {
                            if (functionNumBean?.myLeaveNum!! > 99) {
                                itemView.tv_number_total.text = "99+"
                            } else {
                                itemView.tv_number_total.text = functionNumBean?.myLeaveNum.toString()
                            }
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 3)
                            View.VISIBLE
                        } else {
                            itemView.tv_number_total.text = ""
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 4)
                            View.GONE
                        }
                        "1" -> itemView.tv_number_total.visibility = if (functionNumBean?.leaveApproveNum ?: 0 > 0) {
                            if (functionNumBean?.leaveApproveNum!! > 99) {
                                itemView.tv_number_total.text = "99+"
                            } else {
                                itemView.tv_number_total.text = functionNumBean?.leaveApproveNum.toString()
                            }
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 3)
                            View.VISIBLE
                        } else {
                            itemView.tv_number_total.text = ""
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 4)
                            View.GONE
                        }
                    }
                }
                FunctionName.TravelList -> {
                    when (functionBean?.type) {
                        "0" -> itemView.tv_number_total.visibility = if (functionNumBean?.myTravelNum ?: 0 > 0) {
                            if (functionNumBean?.myTravelNum!! > 99) {
                                itemView.tv_number_total.text = "99+"
                            } else {
                                itemView.tv_number_total.text = functionNumBean?.myTravelNum.toString()
                            }
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 3)
                            View.VISIBLE
                        } else {
                            itemView.tv_number_total.text = ""
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 4)
                            View.GONE
                        }
                        "1" -> itemView.tv_number_total.visibility = if (functionNumBean?.travelApproveNum ?: 0 > 0) {
                            if (functionNumBean?.travelApproveNum!! > 99) {
                                itemView.tv_number_total.text = "99+"
                            } else {
                                itemView.tv_number_total.text = functionNumBean?.travelApproveNum.toString()
                            }
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 3)
                            View.VISIBLE
                        } else {
                            itemView.tv_number_total.text = ""
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 4)
                            View.GONE
                        }
                    }
                }
                FunctionName.AskList -> {
                    when (functionBean?.type) {
                        "0" -> itemView.tv_number_total.visibility = if (functionNumBean?.myAskNum ?: 0 > 0) {
                            if (functionNumBean?.myAskNum!! > 99) {
                                itemView.tv_number_total.text = "99+"
                            } else {
                                itemView.tv_number_total.text = functionNumBean?.myAskNum.toString()
                            }
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 3)
                            View.VISIBLE
                        } else {
                            itemView.tv_number_total.text = ""
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 4)
                            View.GONE
                        }
                        "1" -> itemView.tv_number_total.visibility = if (functionNumBean?.askApproveNum ?: 0 > 0) {
                            if (functionNumBean?.askApproveNum!! > 99) {
                                itemView.tv_number_total.text = "99+"
                            } else {
                                itemView.tv_number_total.text = functionNumBean?.askApproveNum.toString()
                            }
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 3)
                            View.VISIBLE
                        } else {
                            itemView.tv_number_total.text = ""
                            if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 4)
                            View.GONE
                        }
                    }
                }
                FunctionName.MessageList -> {
                    itemView.tv_number_total.visibility = if (functionNumBean?.myMessageMum ?: 0 > 0) {
                        if (functionNumBean?.myMessageMum!! > 99) {
                            itemView.tv_number_total.text = "99+"
                        } else {
                            itemView.tv_number_total.text = functionNumBean?.myMessageMum.toString()
                        }
                        if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 3)
                        View.VISIBLE
                    } else {
                        itemView.tv_number_total.text = ""
                        if (isHasAnnotation) executeAnimation(itemView.tv_number_total, 4)
                        View.GONE
                    }
                }
                else -> {
                    itemView.tv_number_total.text = ""
                    itemView.tv_number_total.visibility = View.GONE
                }
            }
        }
    }

    /**
     * 控件执行动画方法
     * @param view 执行动画的控件
     * @param 动画的类型1,右上显示，2，右上隐藏3，左下显示4，左下隐藏
     */
    fun executeAnimation(view: View, type: Int) {
        when (type) {
            1 -> {
                view.visibility = View.VISIBLE
                startAnimation(view, 1)
            }
            2 -> {
                startAnimation(view, 2)
                view.visibility = View.GONE
            }
            3 -> {
                if (view.visibility == View.GONE) {
                    startAnimation(view, 3)
                    view.visibility = View.VISIBLE
                }
            }
            4 -> {
                if (view.visibility == View.VISIBLE) {
                    startAnimation(view, type)
                    view.visibility = View.GONE
                }
            }
        }
    }

    /**
     * 开始动画
     * @param view 执行动画的控件
     * @param 动画的类型1,右上显示，2，右上隐藏3，左下显示4，左下隐藏
     */
    fun startAnimation(view: View, type: Int) {
        var anim: Animation
        when (type) {
            1 -> {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_in_top_right)
            }
            2 -> {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_out_top_right)
            }
            3 -> {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_in_left_bottom)
            }
            4 -> {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_out_left_bottom)
            }
            else -> {
                return
            }
        }
        view.startAnimation(anim)
    }
}
