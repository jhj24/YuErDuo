package com.jqyd.yuerduo.activity.main;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jqyd.yuerduo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangfan on 16-1-8.
 */
public class BottomBarItem {
    private final ViewPager viewPager;
    int index;
    View item;

    public BottomBarItem(int index, View item, ViewPager viewPager) {
        this.index = index;
        this.item = item;
        this.viewPager = viewPager;
        ButterKnife.bind(this, item);
    }

    @Bind(R.id.imageViewDefault)
    ImageView imageViewDefault;
    //    @Bind(R.id.imageViewSelected)
//    ImageView imageViewSelected;
    @Bind(R.id.itemTitleDefault)
    TextView titleDefault;
    @Bind(R.id.itemTitleSelected)
    TextView titleSelected;
    @Bind(R.id.red_dot)
    TextView redDot;

    @OnClick(R.id.bottomItem)
    public void onItemClick() {
        viewPager.setCurrentItem(index, false);
    }
}
