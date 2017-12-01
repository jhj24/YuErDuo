package com.jqyd.yuerduo.util

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import com.jqyd.yuerduo.MyApplication
import com.jqyd.yuerduo.bean.BaseTreeBean
import com.jqyd.yuerduo.fragment.contacts.CharacterUtil
import org.jetbrains.anko.onTouch
import java.util.*

/**
 * 树形数据排序
 * Created by jianhaojie on 2017/3/23.
 */

class TreeHandleUtil {
    init {

    }

    companion object {
        /**
         * 树形数据排序
         */
        fun <T> sortData(data: List<T>): List<T> {
            Collections.sort(data) { o1, o2 ->
                if (o1 is BaseTreeBean && o2 is BaseTreeBean) {
                    o1.spelling.compareTo(o2.spelling)
                } else 0

            }
            Collections.sort(data) { o1, o2 ->
                if (o1 is BaseTreeBean && o2 is BaseTreeBean) {
                    o2.childrenSize.compareTo(o1.childrenSize)
                } else 0
            }
            return data
        }

        /**
         * 点击搜索框，显示键盘，点击RecyclerView键盘消失
         */
        fun onSearch(layoutSearch: RelativeLayout, recyclerView: RecyclerView, rvSearch: RecyclerView, etSearch: EditText) {
            var inputManager: InputMethodManager? = null
            layoutSearch.visibility = View.GONE
            if (inputManager == null) {
                inputManager = MyApplication.instance.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(etSearch, 0)
            }
            recyclerView.onTouch { v, event ->
                (inputManager as InputMethodManager).hideSoftInputFromWindow(etSearch.windowToken, 0)
                false
            }
            rvSearch.onTouch { view, motionEvent ->
                (inputManager as InputMethodManager).hideSoftInputFromWindow(etSearch.windowToken, 0)
                false
            }
        }

        /**
         * 筛选
         */
        fun isFilterSpelling(character: CharacterUtil, filter: String, str: String): Boolean {
            val array = character.getArray(str)
            array.forEach { s ->
                var string = ""
                for (index in array.indexOf(s)..array.size - 1) {
                    string += array[index]
                }
                if (string.toUpperCase().startsWith(filter.toUpperCase())) {
                    return true
                }
            }
            return false
        }

        fun isFilter(character: CharacterUtil, filter: String, str: String): Boolean {
            return isFilterSpelling(character, filter, str) || str.contains(filter)
        }

    }


}
