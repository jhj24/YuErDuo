package com.jqyd.yuerduo.extention.bill.pagerbill

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.extention.anko.topBar
import com.jqyd.yuerduo.extention.anko.xBill
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.viewPager

/**
 * Created by zhangfan on 2016/12/6 0006.
 */
class BillPagerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
//        return SimpleActivityUI("").createView(AnkoContextImpl(activity, activity, true))
//        SimpleActivityUI("").createView()
        val parentAct = activity as? PagerBillActivity
        parentAct?.let {
            val indexOf = parentAct.fragmentList.indexOf(this)
            if (indexOf < 0) {
                return@let
            }
            val defineX = parentAct.billList?.get(indexOf)
            return UI {
                verticalLayout {
                    val define = defineX ?: return@verticalLayout
                    xBill(define, "simpleBill")
//                    button("xx")
                }
            }.view

        }
        return UI {
            verticalLayout {
            }
        }.view
    }

    fun getSelfIndex(): Int {
        val parentAct = activity as? PagerBillActivity
        parentAct?.let {
            return parentAct.fragmentList.indexOf(this)
        }
        return -1
    }

    fun getPagerSize(): Int {
        val parentAct = activity as? PagerBillActivity
        parentAct?.let {
            return parentAct.fragmentList.size
        }
        return 0
    }


    inner class SimpleActivityUI(var title: String) : AnkoComponent<PagerBillActivity> {
        override fun createView(ui: AnkoContext<PagerBillActivity>) = with(ui) {
            verticalLayout {
                topBar(title) {
                }

//                val define = billDefine ?: return@verticalLayout
//                xBill(define, "simpleBill")
//                commit(buttonId = "commit")
                //commit("驳回")

            }
        }
    }

}