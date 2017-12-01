package com.jqyd.yuerduo.widget.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jqyd.yuerduo.R;
import com.jqyd.yuerduo.activity.itinerary.ItineraryListAdapter;
import com.jqyd.yuerduo.bean.ItineraryBean;
import com.jqyd.yuerduo.widget.DividerGridItemDecoration;
import com.jqyd.yuerduo.widget.DividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zhangfan on 17-9-8.
 */

public class ContentFragment extends Fragment {

    public ItineraryListAdapter adapter;
    public RelativeLayout state_mask;
    public Button reload;
    public TextView tv_mask;
    public int itineraryState = -2;//-1请求失败，0无数据，>0正常
    public OnReloadClickListener listener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_itinerary_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.itineraryList);
        state_mask = (RelativeLayout) view.findViewById(R.id.state_mask);
        reload = (Button) view.findViewById(R.id.reload);
        tv_mask = (TextView) view.findViewById(R.id.tv_mask);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReloadClick(v);
            }
        });
        adapter = new ItineraryListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setReloadView();
    }

    /**
     * 设置提示界面
     */
    public void setReloadView() {
        if (state_mask != null && tv_mask != null && reload != null) {
            if (itineraryState > 0) {//-1请求失败，0无数据，>0正常
                state_mask.setVisibility(View.GONE);
            } else if (itineraryState == -1) {
                state_mask.setVisibility(View.VISIBLE);
                tv_mask.setText("加载失败");
                reload.setVisibility(View.VISIBLE);
            } else if (itineraryState == 0){
                state_mask.setVisibility(View.VISIBLE);
                reload.setVisibility(View.INVISIBLE);
                tv_mask.setText("没有数据");
            }
        }
    }

    public interface OnReloadClickListener{
        void onReloadClick(View v);
    }

}
