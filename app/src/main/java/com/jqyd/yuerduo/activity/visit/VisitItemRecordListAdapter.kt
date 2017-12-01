package com.jqyd.yuerduo.activity.visit

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.VisitStrategyBean
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.bill.SimpleBillActivity
import com.jqyd.yuerduo.extention.bill.pagerbill.PagerBillActivity
import com.jqyd.yuerduo.extention.bill.pagerbill.PagerBillIndexActivity
import kotlinx.android.synthetic.main.list_item_visit.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.util.*

/**
 * 拜访详情页，拜访项列表adapter
 * Created by zhangfan on 2016/12/5 0005.
 */
class VisitItemRecordListAdapter(val visitList: List<VisitStrategyBean.VisitItem>, val visitRecordDetailActivity: VisitRecordDteailActivity) : RecyclerView.Adapter<VisitItemRecordListAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindData()
    }

    override fun getItemCount(): Int {
        return visitList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_visit, parent, false)
        return ViewHolder(view)
    }


    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        init {

            itemView?.onClick {
                val visitItem = visitList[adapterPosition]
                val itemList = visitItem.billDefine["itemList"] as? List<*>
                if (itemList?.size == 1) {
                    val item = itemList?.get(0) as? Map<String, Any>
                    if (item?.get("type")?.toString()?.toDouble() ?: 0 == 8.0) {
                        doWithType8(item, itemView)
                        return@onClick
                    }
                    if (item?.get("type")?.toString()?.toDouble() ?: 0 == 5.0) {
                        return@onClick
                    }
                }
                visitItem.billDefine.put("editable", false)
                val defineJson = Gson().toJson(visitItem.billDefine)
                val intent = Intent(itemView.context, SimpleBillActivity::class.java)
                intent.putExtra("billDefine", defineJson)
                visitRecordDetailActivity.startActivity(intent)

            }
        }

        private fun doWithType8(item: Map<String, Any>?, itemView: View): ((Int, Int, Intent?) -> Unit)? {
            var defineJson1 = ""
            val map = item?.get("define") as? Map<String, Any>
            if (map != null) {
                val billList = map["billList"]
                if (billList != null) {
                    defineJson1 = Gson().toJson(billList)
                    try {
                        val billListObj: List<BillDefineX> = Gson().fromJson(defineJson1, object : TypeToken<List<BillDefineX>>() {}.type)
                        val intent = Intent()
                        if (map["hasIndexPage"] as Boolean) {
                            intent.setClass(itemView.context, PagerBillIndexActivity::class.java)
                        } else {
                            intent.setClass(itemView.context, PagerBillActivity::class.java)
                        }
                        intent.putExtra("billList", ArrayList(billListObj))
                        visitRecordDetailActivity.startActivity(intent)
                    } catch(e: Exception) {
                        visitRecordDetailActivity.toast("数据格式异常")
                    }
                } else {
                    visitRecordDetailActivity.toast("数据格式异常")
                }
            } else {
                visitRecordDetailActivity.toast("数据格式异常")
            }
            return null
        }

        fun bindData() {
            val visitItem = visitList[adapterPosition]
            itemView.tv_title.text = visitItem.title.orEmpty()
            if (visitItem.finished) {
//                itemView.tv_state.text = "已完成"
                itemView.iv_mark.setImageResource(R.drawable.visit_finish)
            } else {
                if (visitItem.necessary) {
//                    itemView.tv_state.text = "必需项"
                    itemView.iv_mark.setImageResource(R.drawable.visit_mast_unfinish)
                } else {
//                    itemView.tv_state.text = "非必需项"
                    itemView.iv_mark.setImageResource(R.drawable.visit_unfinish)
                }
            }
        }
    }
}
