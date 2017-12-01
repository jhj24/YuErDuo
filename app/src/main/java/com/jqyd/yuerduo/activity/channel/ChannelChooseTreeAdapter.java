package com.jqyd.yuerduo.activity.channel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jqyd.yuerduo.R;
import com.jqyd.yuerduo.bean.ChannelTreeNodeBean;
import com.jqyd.yuerduo.util.TreeHandleUtil;
import com.jqyd.yuerduo.util.UIUtils;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangfan on 2016/3/17.
 * 渠道选择列表适配器
 */
public class ChannelChooseTreeAdapter extends RecyclerView.Adapter<ChannelChooseTreeAdapter.ChannelChooseItemHolder> {

    private Context context;
    public List<ChannelTreeNodeBean> dataList = new LinkedList<>();

    private final int dp10;

    public ChannelChooseTreeAdapter(Context context) {
        this.context = context;
        dp10 = UIUtils.dip2px(context, 10);
    }

    @Override
    public ChannelChooseItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if (1 == viewType) {
            view = layoutInflater.inflate(R.layout.layout_tree_item_staff, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.layout_tree_item_dept, parent, false);
        }
        return new ChannelChooseItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ChannelChooseItemHolder holder, int position) {
        ChannelTreeNodeBean bean = dataList.get(position);
        holder.itemView.setTag(bean);
        holder.tvName.setText(bean.getName());
        holder.list_item.setPadding(dp10 + bean.levels * dp10 * 2, dp10, dp10, dp10);
        if (bean.isChecked) {
            holder.checkbox.setImageResource(R.drawable.icon_choice);
        } else {
            holder.checkbox.setImageResource(R.drawable.icon_choice_no);
        }
    }

    public void updateListView(List<ChannelTreeNodeBean> filterDataList) {
        this.dataList = filterDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).isChannel() ? 1 : 0;
    }

    public class ChannelChooseItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.checkbox)
        ImageView checkbox;
        @Bind(R.id.list_item)
        View list_item;

        @OnClick(R.id.list_item)
        public void onItemClick() {
            ChannelTreeNodeBean bean = (ChannelTreeNodeBean) itemView.getTag();
            if (bean.isChannel()) {
                onCheckedChanged(checkbox);
            } else {
                int location = dataList.indexOf(bean);
                if (bean.getChildren().size() > 0) {
                    if (bean.isShowChildren) {
                        List<ChannelTreeNodeBean> children = bean.getAllShowedChildren(true);
                        dataList.removeAll(children);
                        notifyDataSetChanged();
                    } else {
                        TreeHandleUtil.Companion.sortData(bean.getChildren());
                        bean.setChildrenLevel();
                        dataList.addAll(location + 1, bean.getChildren());
                        notifyDataSetChanged();
                    }
                    bean.setShowChildren(!bean.isShowChildren);
                } else {
                    Toast.makeText(context,bean.getName()+"暂无客户",Toast.LENGTH_SHORT).show();
                }
            }
        }

        @OnClick(R.id.checkbox)
        public void onCheckedChanged(View view) {
            ChannelTreeNodeBean bean = (ChannelTreeNodeBean) itemView.getTag();
            int location = dataList.indexOf(bean);
            bean.isChecked = !bean.isChecked;
            int count = bean.setChildrenChecked(bean.isChecked);
            notifyItemRangeChanged(location, 1 + count);
        }


        public ChannelChooseItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }
}
