package com.jqyd.yuerduo.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by zhangfan on 17-9-8.
 */

/**
 * 没用
 */
public class BottomContainer extends RelativeLayout {
    public BottomContainer(Context context) {
        super(context);
    }

    public BottomContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    boolean interceptTouch=true;

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (ev.getAction()!=MotionEvent.ACTION_MOVE){
//            return super.onInterceptTouchEvent(ev);
//        }
//        return interceptTouch;
////        return super.onInterceptTouchEvent(ev);
//    }
}
