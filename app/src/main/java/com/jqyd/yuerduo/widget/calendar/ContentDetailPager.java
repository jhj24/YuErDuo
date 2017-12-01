package com.jqyd.yuerduo.widget.calendar;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gjc on 17-9-20.
 */

public class ContentDetailPager extends ViewPager {
    public ContentDetailPager(Context context) {
        super(context);
    }

    public ContentDetailPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    FragmentManager fragmentManager;
    MyScrollView myScrollView;
    public List<ContentDetailFragment> contentDetailFragmentList = new ArrayList<>();
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
            ContentDetailFragment contentDetailFragment = new ContentDetailFragment();
            contentDetailFragmentList.add(contentDetailFragment);
        }

        setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return contentDetailFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                position = position % contentDetailFragmentList.size();
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
