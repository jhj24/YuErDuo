package com.jqyd.yuerduo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Created by zhangfan on 2016/4/21 0021.
 */
class KeyboardChangeListenerView : View {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        callBack?.invoke()
    }

    public var callBack:(()->Unit)?=null
}
