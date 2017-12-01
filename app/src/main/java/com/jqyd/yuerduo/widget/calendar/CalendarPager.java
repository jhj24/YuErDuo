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
import java.util.Calendar;
import java.util.List;

/**
 * Created by zhangfan on 17-9-8.
 */

public class CalendarPager extends ViewPager {
    public CalendarPager(Context context) {
        super(context);
    }

    public CalendarPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    FragmentManager fragmentManager;
    public MyScrollView myScrollView;
    public ContentPager contentPager;
    public OnPagerChangeListener listener = null;

    public long date = System.currentTimeMillis();
    public boolean isContentPagerNeedChange = true;//calendarPager滑动时contentPager是否执行onPagerSelected
    private boolean isCanScroll = true;

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        init();
        setCurrentItem(Integer.MAX_VALUE / 2, false);
        date = System.currentTimeMillis();
        resetFragment();
    }

    int type = 0;//0month, 1week

    public void setType(int type) {
        this.type = type;
        resetFragment();
    }

    public int getType() {
        return type;
    }

    public List<CalendarFragment> fragmentList = new ArrayList<CalendarFragment>();

    private void init() {
        CalendarFragment a = new CalendarFragment();
        a.calendarPager = this;
        fragmentList.add(a);
        CalendarFragment b = new CalendarFragment();
        b.calendarPager = this;
        fragmentList.add(b);
        CalendarFragment c = new CalendarFragment();
        c.calendarPager = this;
        fragmentList.add(c);
        CalendarFragment d = new CalendarFragment();
        d.calendarPager = this;
        fragmentList.add(d);
        CalendarFragment e = new CalendarFragment();
        e.calendarPager = this;
        fragmentList.add(e);
        setMonthAdapter();
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (listener != null) {
                    listener.onPagerScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
//                ViewGroup.LayoutParams layoutParams = getLayoutParams();
//                layoutParams.height = (int) (pagerWidth/7f*5);
//                setLayoutParams(layoutParams);
//                myScrollView.scrollToBottom();
//                date = fragmentList.get(getCurrentItem() % fragmentList.size()).getDate();
//                resetFragment();
                if (listener != null) {
                    listener.onPagerSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_IDLE) {
                    if (CalendarPager.this.type == 1) {
                        myScrollView.scrollToBottom();
                    } else {
                        myScrollView.scrollToTop();
                    }
//                    contentPager.setCurrentItem(contentPager.getCurrentItem() + 1, true);
                    date = fragmentList.get(getCurrentItem() % fragmentList.size()).getDate();
                    resetFragment();
                }
                if (listener != null) {
                    listener.onPagerScrollStateChanged(state);
                }
            }

        });
    }

    public void resetCurrentFragment() {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(date);
        int position1 = getCurrentItem();
        CalendarFragment calendarFragment = fragmentList.get(position1 % fragmentList.size());
        calendarFragment.setDate(instance.getTimeInMillis());
        calendarFragment.setType(type);
    }

    public void resetFragment() {
        if (type == 0) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(date);
            instance.add(Calendar.MONTH, 1);
            int position1 = getCurrentItem() + 1;
            CalendarFragment calendarFragment = fragmentList.get(position1 % fragmentList.size());
            calendarFragment.setDate(instance.getTimeInMillis());
            calendarFragment.setType(type);

            instance.setTimeInMillis(date);
            instance.add(Calendar.MONTH, -1);
            int position2 = getCurrentItem() - 1;
            CalendarFragment calendarFragment1 = fragmentList.get(position2 % fragmentList.size());
            calendarFragment1.setDate(instance.getTimeInMillis());
            calendarFragment1.setType(type);
        } else {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(date);
            instance.add(Calendar.WEEK_OF_YEAR, 1);
            int position1 = getCurrentItem() + 1;
            CalendarFragment calendarFragment = fragmentList.get(position1 % fragmentList.size());
            calendarFragment.setDate(instance.getTimeInMillis());
            calendarFragment.setType(type);

            Calendar instance2 = Calendar.getInstance();
            instance2.setTimeInMillis(date);
            instance2.add(Calendar.WEEK_OF_YEAR, -1);
            int position2 = getCurrentItem() - 1;
            CalendarFragment calendarFragment1 = fragmentList.get(position2 % fragmentList.size());
            calendarFragment1.setDate(instance2.getTimeInMillis());
            calendarFragment1.setType(type);
        }
    }

//    private void setWeekAdapter() {
//        setAdapter(new FragmentPagerAdapter(fragmentManager) {
//            @Override
//            public Fragment getItem(int position) {
//                return new CalendarFragment();
//            }
//
//            @Override
//            public int getCount() {
//                return 10;
//            }
//        });
//    }

    private void setMonthAdapter() {
        setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
//                CalendarFragment calendarFragment = new CalendarFragment();
//                Bundle args = new Bundle();
//                args.putLong("date", System.currentTimeMillis());
//                calendarFragment.setArguments(args);
//                currentFragment = calendarFragment;
//                return calendarFragment;
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //处理position。让数组下标落在[0,fragmentList.size)中，防止越界
                position = position % fragmentList.size();
                return super.instantiateItem(container, position);
            }
        });
    }

    int scrollY;
//    CalendarFragment currentFragment;

    public void onScroll(int scrollY) {
        this.scrollY = scrollY;
//        if (currentFragment != null) {
//            currentFragment.onScroll(scrollY);
//        }
        fragmentList.get(getCurrentItem() % fragmentList.size()).onScroll(scrollY);
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
