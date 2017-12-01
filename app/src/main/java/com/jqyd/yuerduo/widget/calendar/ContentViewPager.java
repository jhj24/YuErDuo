package com.jqyd.yuerduo.widget.calendar;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;


import com.jqyd.yuerduo.widget.calendartest.ContentViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liushiqi on 2017/9/14,0014.
 */

public class ContentViewPager extends ViewPager {

    public boolean isCalendarPagerNeedChange = true;//contentPager滑动时calendarPager是否执行onPageSelected

    public ContentViewPager(Context context) {
        super(context);
    }

    public ContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isCanScroll = true;

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    public List<ContentViewFragment> contentViewFragmentList = new ArrayList<>();
    FragmentManager fragmentManager;
    MyScrollView myScrollView;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        init();
        setCurrentItem(Integer.MAX_VALUE / 2, false);
    }

    private void init() {
        for (int i = 0; i < 4; i++) {
            ContentViewFragment contentViewFragment = new ContentViewFragment();
            contentViewFragmentList.add(contentViewFragment);
        }

        setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return contentViewFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                position = position % contentViewFragmentList.size();
                return super.instantiateItem(container, position);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            return super.onInterceptTouchEvent(ev);
        } else {
            //false  不能左右滑动
            return false;
        }
    }
}