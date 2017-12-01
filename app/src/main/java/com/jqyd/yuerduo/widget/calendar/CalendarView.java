package com.jqyd.yuerduo.widget.calendar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.Calendar;

/**
 * Created by zhangfan on 17-9-8.
 */

public class CalendarView extends RecyclerView {
    protected int containerWidth;
    private Calendar currentMonth;

    CalendarMonthAdapter.ItemClickListener itemClickListener;
    CalendarMonthAdapter adapter;

    public void setDate(long date) {
//        if (currentMonth == null || currentMonth.getTimeInMillis() != date) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(date);
        currentMonth = instance;
        adapter = new CalendarMonthAdapter(getContext(), containerWidth / 7, currentMonth);
        adapter.itemClickListener = new CalendarMonthAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Calendar date) {
                currentMonth = date;
                itemClickListener.onItemClick(date);
            }
        };
        this.setAdapter(adapter);
//        }
    }

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setLayoutManager(new GridLayoutManager(getContext(), 7));
    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int measuredWidth = getMeasuredWidth();
        if (measuredWidth != this.containerWidth) {
            this.containerWidth = measuredWidth;
            CalendarMonthAdapter adapter = new CalendarMonthAdapter(getContext(), measuredWidth / 7, currentMonth);
            adapter.itemClickListener = new CalendarMonthAdapter.ItemClickListener() {
                @Override
                public void onItemClick(Calendar date) {
                    itemClickListener.onItemClick(date);
                }
            };
            this.setAdapter(adapter);
        }
    }
}
