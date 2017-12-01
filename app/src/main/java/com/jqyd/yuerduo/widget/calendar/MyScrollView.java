package com.jqyd.yuerduo.widget.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by zhangfan on 17-9-8.
 */

public class MyScrollView extends ScrollView {
    private int containerHeight;

    public MyScrollView(Context context) {
        super(context);
        init();
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        post(new Runnable() {
            @Override
            public void run() {
                scrollTo(0, 0);
            }
        });
    }

    private boolean isScrolledToTop = true;
    private boolean isScrolledToBottom = false;

    int scrollDir = 1;
    long scrollTime = 0;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        scrollDir = t - oldt > 0 ? 1 : -1;
        scrollTime = System.currentTimeMillis();
//        Log.i("xxx", "" + l + "," + t + "," + oldl + "," + oldt);
        if (getScrollY() == 0) {    // 小心踩坑1: 这里不能是getScrollY() <= 0
            isScrolledToTop = true;
            isScrolledToBottom = false;
            scrollDir = 0;
        } else if (getScrollY() + getHeight() - getPaddingTop() - getPaddingBottom() == getChildAt(0).getHeight()) {
            isScrolledToBottom = true;
            isScrolledToTop = false;
            scrollDir = 0;
        } else {
            isScrolledToTop = false;
            isScrolledToBottom = false;
        }
        notifyScrollChangedListeners();
    }

    private void notifyScrollChangedListeners() {
        if (isScrolledToTop) {
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToTop();
            }
        } else if (isScrolledToBottom) {
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToBottom();
            }
        }
        mSmartScrollChangedListener.onScroll(getScrollY());
    }


    private ISmartScrollChangedListener mSmartScrollChangedListener;

    /**
     * 定义监听接口
     */
    public interface ISmartScrollChangedListener {
        void onScrolledToBottom();

        void onScrolledToTop();

        void onScroll(int scrollY);
    }

    public void setScanScrollChangedListener(ISmartScrollChangedListener smartScrollChangedListener) {
        mSmartScrollChangedListener = smartScrollChangedListener;
    }

    public boolean isScrolledToTop() {
        return isScrolledToTop;
    }

    public boolean isScrolledToBottom() {
        return isScrolledToBottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.containerHeight = getMeasuredHeight();
        if (this.onMeasureCallback != null) {
            this.onMeasureCallback.onMeasure(this.containerHeight);
        }
    }

    public OnMeasureCallback onMeasureCallback;

    public interface OnMeasureCallback {
        void onMeasure(int height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i("xx", "scroll touch");
        return super.onTouchEvent(ev);
    }


    float preY = 0;
    float preX = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean b = super.onInterceptTouchEvent(ev);
        Log.i("xx", "scroll intercep" + b);

//        if (calendarPager.onInterceptTouchEvent(ev)) {
//            return false;
//        }
        float dy = ev.getY() - preY;
        float dx = ev.getX() - preX;
        if (isScrolledToTop && ev.getAction() == MotionEvent.ACTION_MOVE && ev.getY() < preY && Math.abs(dy) > Math.abs(dx)) {
            preY = ev.getY();
            preX = ev.getX();
            return true;
        }
        preY = ev.getY();
        preX = ev.getX();

        return b;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = super.dispatchTouchEvent(ev);
        Log.i("xx", "scroll dispatchTouchEvent " + b);
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - scrollTime > 300) {
                if (getScrollY() > (getChildAt(0).getHeight() - getHeight()) / 2f) {
                    smoothScrollTo(0, getChildAt(0).getHeight() - getHeight());
                } else {
                    smoothScrollTo(0, 0);
                }
            } else {
                if (scrollDir == 1) {
                    smoothScrollTo(0, getChildAt(0).getHeight() - getHeight());
                } else if (scrollDir == -1) {
                    smoothScrollTo(0, 0);
                }
            }
        }
        return b;
    }

    public void scrollToTop() {
        smoothScrollTo(0, 0);
    }

    public void scrollToBottom() {
        smoothScrollTo(0, getChildAt(0).getHeight() - getHeight());
    }

    public void scrollToBottomImmediately() {
        scrollTo(0, getChildAt(0).getHeight() - getHeight());
    }

}
