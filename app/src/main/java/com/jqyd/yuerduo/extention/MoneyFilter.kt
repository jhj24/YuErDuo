package com.jqyd.yuerduo.extention

import android.text.InputFilter
import android.text.Spanned

import com.orhanobut.logger.Logger

/**
 * 金额输入过滤器
 * Created by zhangfan on 2016/5/6 0006.
 */
class MoneyFilter : InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val x = dest.toString().substring(0, dstart) + source + dest.toString().substring(dend)
        Logger.i("$source,$start,$end===$dest,$dstart,$dend===$x")
        if (match(x)) {
            return null
        } else {
            return ""
        }
    }

    private fun match(str: String): Boolean {
        return str.matches("^(([1-9]\\d{0,9})|0)(\\.\\d{0,2})?$".toRegex())
    }
}
