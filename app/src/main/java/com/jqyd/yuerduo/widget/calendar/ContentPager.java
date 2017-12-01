package com.jqyd.yuerduo.widget.calendar;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangfan on 17-9-8.
 */

public class ContentPager extends ViewPager {
    public ContentPager(Context context) {
        super(context);
    }

    public ContentPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    FragmentManager fragmentManager;
    MyScrollView myScrollView;
    public List<ContentFragment> contentFragmentList = new ArrayList<>();
    public boolean isCalendarPagerNeedChange = true;//contentPager滑动时calendarPager是否执行onPageSelected
    private boolean isCanScroll = true;

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        init();
        setCurrentItem(Integer.MAX_VALUE / 2, false);
    }

    private void init() {

        for (int i = 0; i < 4; i++) {
            ContentFragment contentFragment = new ContentFragment();
            contentFragmentList.add(contentFragment);
        }

        setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return contentFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                position = position % contentFragmentList.size();
                return super.instantiateItem(container, position);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isCanScroll){
            return super.onInterceptTouchEvent(ev);
        }else{
            //false  不能左右滑动
            return false;
        }
    }
}
