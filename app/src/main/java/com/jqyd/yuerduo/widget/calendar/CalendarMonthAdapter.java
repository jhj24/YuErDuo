package com.jqyd.yuerduo.widget.calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jqyd.yuerduo.R;
import com.jqyd.yuerduo.activity.itinerary.ItineraryDataContainer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangfan on 17-9-8.
 */

public class CalendarMonthAdapter extends RecyclerView.Adapter<CalendarMonthAdapter.ItemViewHolder> {


    private final int itemWidth;
    private Calendar currentMonth;
    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private List<String> markList = new ArrayList<>();
    private ItineraryDataContainer itineraryDataContainer;
    private List<String> unMarkList = new ArrayList<>();

    public CalendarMonthAdapter(Context context, int itemWidth, Calendar currentMonth) {
        this.context = context;
        this.itemWidth = itemWidth;
        this.currentMonth = currentMonth;
        itineraryDataContainer = (ItineraryDataContainer) context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_grid_item, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.height = itemWidth;
        layoutParams.width = itemWidth;
        itemView.setLayoutParams(layoutParams);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Calendar dateByPosition = getDateByPosition(position);
        holder.tv_day.setText("" + dateByPosition.get(Calendar.DAY_OF_MONTH));

        boolean isCurrentMonth = dateByPosition.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH);
        Calendar instance = Calendar.getInstance();
        markList = itineraryDataContainer.getMonthList();
        unMarkList = itineraryDataContainer.getUnUploadList();
        if (markList != null) {
            for (int i = 0; i < markList.size(); i++) {
                if (sdf.format(dateByPosition.getTimeInMillis()).equals(markList.get(i))) {
                    if (dateByPosition.getTimeInMillis() == currentMonth.getTimeInMillis()) {
                        holder.calendar_mark.setVisibility(View.GONE);
                    } else {
                        holder.calendar_mark.setVisibility(View.VISIBLE);
                    }
                    break;
                } else {
                    holder.calendar_mark.setVisibility(View.GONE);
                }
            }
        } else {
            holder.calendar_mark.setVisibility(View.GONE);
        }

        if (unMarkList != null) {
            for (int i = 0; i < unMarkList.size(); i++) {
                if (sdf.format(dateByPosition.getTimeInMillis()).equals(unMarkList.get(i))) {
                    if (dateByPosition.getTimeInMillis() == currentMonth.getTimeInMillis()) {
                        holder.un_commit_calendar_mark.setVisibility(View.GONE);
                    } else {
                        holder.un_commit_calendar_mark.setVisibility(View.VISIBLE);
                    }
                    break;
                } else {
                    holder.un_commit_calendar_mark.setVisibility(View.GONE);
                }
            }
        } else {
            holder.un_commit_calendar_mark.setVisibility(View.GONE);
        }

        if (sdf.format(dateByPosition.getTimeInMillis()).equals(sdf.format(instance.getTimeInMillis()))) {//当天
            if (dateByPosition.getTimeInMillis() == currentMonth.getTimeInMillis()) {// 当天被选中
                holder.tv_day.setTextColor(Color.WHITE);
                holder.calendar_item.setBackgroundResource(R.drawable.bg_calendar_state_item);
            } else { // 当天没有被选中
                holder.tv_day.setTextColor(ContextCompat.getColor(context, R.color.primary));
                holder.calendar_item.setBackgroundResource(0);
            }
        } else {
            if (isCurrentMonth) {
                if (dateByPosition.getTimeInMillis() == currentMonth.getTimeInMillis()) {
                    holder.tv_day.setTextColor(ContextCompat.getColor(context, R.color.calendar_text));
                    holder.calendar_item.setBackgroundResource(R.drawable.bg_calendar_item);
                } else {
                    holder.tv_day.setTextColor(ContextCompat.getColor(context, R.color.calendar_text));
                    holder.calendar_item.setBackgroundResource(0);
                }
            } else {
                holder.tv_day.setTextColor(Color.GRAY);
            }
        }

        holder.setDate(dateByPosition);
    }

    private Calendar getDateByPosition(int position) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(currentMonth.getTime());
        Date time = instance.getTime();
        instance.set(Calendar.DAY_OF_MONTH, 1);
        Date time1 = instance.getTime();
        instance.set(Calendar.DAY_OF_WEEK, 1);
        Date time2 = instance.getTime();
        instance.add(Calendar.DAY_OF_YEAR, position);
        return instance;
    }

    @Override
    public int getItemCount() {
        return 42;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_day;
        ImageView calendar_mark;
        ImageView un_commit_calendar_mark;
        RelativeLayout calendar_item;
        private Calendar date;

        public ItemViewHolder(View itemView) {
            super(itemView);
            calendar_item = (RelativeLayout) itemView.findViewById(R.id.calendar_item);
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            calendar_mark = (ImageView) itemView.findViewById(R.id.calendar_mark);
            un_commit_calendar_mark = (ImageView) itemView.findViewById(R.id.unCommit_calendar_mark);
            ViewGroup.LayoutParams layoutParams = calendar_item.getLayoutParams();
            layoutParams.height = itemWidth;
            layoutParams.width = itemWidth;
            calendar_item.setLayoutParams(layoutParams);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CalendarMonthAdapter.this.currentMonth = date;
                    itemClickListener.onItemClick(date);
                    notifyDataSetChanged();
                }
            });
        }

        public void setDate(Calendar date) {
            this.date = date;
        }

        public Calendar getDate() {
            return date;
        }
    }

    public interface ItemClickListener {
        void onItemClick(Calendar date);
    }

    ItemClickListener itemClickListener;
}
