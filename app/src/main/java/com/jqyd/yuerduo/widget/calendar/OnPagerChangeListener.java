package com.jqyd.yuerduo.widget.calendar;

/**
 * Created by gjc on 2017/9/12.
 */

public interface OnPagerChangeListener {
    void onPagerScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPagerSelected(int position);

    void onPagerScrollStateChanged(int state);
}
