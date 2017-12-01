package com.jqyd.yuerduo.widget.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangfan on 17-9-8.
 */

public class CalendarFragment extends Fragment {


    private long date = System.currentTimeMillis();
    public CalendarPager calendarPager;
    public CalendarMonthAdapter.ItemClickListener itemClickListener;

    public void setDate(long date) {
        this.date = date;
        if (view != null) {
            view.setDate(date);
        }
    }

    public long getDate() {
        return date;
    }
    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            date = getArguments().getLong("date");
//        }
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        view = new CalendarView(container.getContext());
        view.itemClickListener = new CalendarMonthAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Calendar date) {
                if (calendarPager != null) {
                    calendarPager.date = date.getTimeInMillis();
                    calendarPager.resetFragment();
                    CalendarFragment.this.date = date.getTimeInMillis();
                    CalendarFragment.this.setType(calendarPager.type);
                    itemClickListener.onItemClick(date);
                }
            }
        };
        view.setDate(date);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), "11", Toast.LENGTH_SHORT).show();
//            }
//        });
//        int containerWidth = view.containerWidth / 5;
//        view.setTranslationY(100);

        return view;
    }

    CalendarView view;

    public void onScroll(int scrollY) {
        int i = getRowIndex();
        int itemSize = (int) (view.containerWidth / 7f);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(date);
//        int i = instance.get(Calendar.WEEK_OF_MONTH) - 1;
        if (scrollY > itemSize * i) {
            view.setTranslationY(scrollY - itemSize * i);
        } else {
            view.setTranslationY(0);
        }
        Log.i("xxxxxx", "scroll " + scrollY);
    }

    public void setType(int type) {
        if (view == null) return;
        if (type == 0) {
            view.setTranslationY(0);
        } else {
            int itemSize = (int) (view.containerWidth / 7f);
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(date);
//            int i = instance.get(Calendar.WEEK_OF_MONTH) - 1;
            int i = getRowIndex() + 1;
            view.setTranslationY(itemSize * (6 - i));
        }
    }

    private int getRowIndex() {
//        int currentDay =
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(date);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        Date time1 = instance.getTime();
        instance.set(Calendar.DAY_OF_WEEK, 7);
        Date time2 = instance.getTime();
        for (int i = 0; i < 7; i++) {
            Date time = instance.getTime();
            Date date = new Date(this.date);
            if (instance.getTimeInMillis() >= this.date) {
                return i;
            }
            instance.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return 0;
    }

}
