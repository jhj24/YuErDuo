package com.jqyd.yuerduo.extention.bill.pagerbill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.extention.alert
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.anko.BillLayoutX
import com.jqyd.yuerduo.extention.anko.commit
import com.jqyd.yuerduo.extention.anko.topBar
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.viewPager
import rx.Observable
import java.util.*

/**
 * 多页单据界面
 * Created by zhangfan on 2016/12/6 0006.
 */
class PagerBillActivity : BaseActivity() {

    var billList: List<BillDefineX>? = null
    val fragmentList = arrayListOf<BillPagerFragment>()

    var index = 0
    var editable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        billList = intent.getSerializableExtra("billList") as? List<Map<String, Any>>
        billList = intent.getSerializableExtra("billList") as? List<BillDefineX>
        index = intent.getIntExtra("index", 0)
        if (billList?.isEmpty() ?: true) {
            toast("单据定义异常")
            finish()
            return
        }
        billList?.let {
            for (bill in it) {
                editable = bill.editable
                val fragment = BillPagerFragment()
                fragmentList.add(fragment)
            }
            SimpleActivityUI("").setContentView(this)
        }
    }

    inner class ViewPagerAdapter() : FragmentPagerAdapter(this@PagerBillActivity.supportFragmentManager) {
        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

    }


    inner class SimpleActivityUI(var title: String) : AnkoComponent<PagerBillActivity> {
        override fun createView(ui: AnkoContext<PagerBillActivity>) = with(ui) {
            verticalLayout {
                var pagerChangeCB: ((Int) -> Unit)? = null
                var viewPager: ViewPager? = null
                topBar(title) {
                    val nextTextView = find<TextView>(R.id.topBar_right_button)
                    val titleTextView = find<TextView>(R.id.topBar_title)
                    val pagerSize = fragmentList.size
                    pagerChangeCB = fun(index) {
                        if (pagerSize > 0 && index < pagerSize - 1) {
                            nextTextView.visibility = View.VISIBLE
                        } else {
                            nextTextView.visibility = View.GONE
                        }
                        titleTextView.text = billList?.get(index)?.title.orEmpty()
                    }
                    pagerChangeCB?.invoke(0)
                    nextTextView.text = "下一页"
                    nextTextView.onClick {
                        viewPager?.currentItem = (viewPager?.currentItem ?: 0) + 1
                    }
                }

                viewPager = viewPager {
                    id = 111
                    adapter = ViewPagerAdapter()
                    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(state: Int) {
                        }

                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        }

                        override fun onPageSelected(position: Int) {
                            pagerChangeCB?.invoke(currentItem)
                        }
                    })
                    offscreenPageLimit = Int.MAX_VALUE
                    currentItem = index
                }.lparams {
                    height = 1
                    weight = 1f
                    width = matchParent
                }
                commit(buttonId = "commit", text = "完成") {
                    if(!editable){
                        this@commit.text = "关闭"
                    }
                    onClick {
                        if (editable) {
                            Observable.create<Boolean> {
                                if (viewPager?.currentItem ?: 0 < fragmentList.size - 1) {
                                    alert("提示", "没有填写到最后一页，是否继续操作？", "确定", "取消") { index, alert ->
                                        if (index == 0) {
                                            it.onNext(true)
                                        }
                                    }
                                } else {
                                    it.onNext(true)
                                }
                            }.map {
                                fragmentList.map {
                                    val billLayoutX = it.view?.findViewWithTag("bill_simpleBill") as? BillLayoutX
                                    val buildData = billLayoutX?.buildData()
                                    val checkResult = buildData?.checkNecessaryFiled(this@PagerBillActivity) ?: false
                                    if (!checkResult) {
                                        throw RuntimeException("单据数据校验失败")
                                    }
                                    buildData
                                }
                            }.subscribe({
                                val intent = Intent()
                                intent.putExtra("billList", ArrayList(it))
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }, {
                                it?.printStackTrace()
                            })
                        }else{
                            finish()
                        }
                    }
                }
            }
        }
    }
}
