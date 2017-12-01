package com.jqyd.yuerduo.activity.staff;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jqyd.yuerduo.R;
import com.jqyd.yuerduo.bean.StaffTreeNodeBean;
import com.jqyd.yuerduo.util.TreeHandleUtil;
import com.jqyd.yuerduo.util.UIUtils;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangfan on 2016/3/17.
 */
public class StaffChooseTreeAdapter extends RecyclerView.Adapter<StaffChooseTreeAdapter.StaffChooseItemHolder> {

    private Context context;
    public List<StaffTreeNodeBean> dataList = new LinkedList<>();
//    public List<StaffTreeNodeBean> selectedList = new LinkedList<>();

    private final int dp10;

    public StaffChooseTreeAdapter(Context context) {
        dp10 = UIUtils.dip2px(context, 10);
        this.context = context;
    }

    @Override
    public StaffChooseItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if (1 == viewType) {
            view = layoutInflater.inflate(R.layout.layout_tree_item_staff, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.layout_tree_item_dept, parent, false);
        }
        return new StaffChooseItemHolder(view);
    }

    @Override
    public void onBindViewHolder(StaffChooseItemHolder holder, int position) {
        StaffTreeNodeBean staffTreeNodeBean = dataList.get(position);
        holder.itemView.setTag(staffTreeNodeBean);
        holder.tvName.setText(staffTreeNodeBean.getName());
        holder.list_item.setPadding(dp10 + staffTreeNodeBean.levels * dp10 * 2, dp10, dp10, dp10);
        if (staffTreeNodeBean.isChecked) {
            holder.checkbox.setImageResource(R.drawable.icon_choice);
        } else {
            holder.checkbox.setImageResource(R.drawable.icon_choice_no);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).isStaff() ? 1 : 0;
    }

    public void updateListView(List<StaffTreeNodeBean> filterDataList) {
        this.dataList = filterDataList;
        notifyDataSetChanged();
    }

    public class StaffChooseItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.checkbox)
        ImageView checkbox;
        @Bind(R.id.list_item)
        View list_item;

        @OnClick(R.id.list_item)
        public void onItemClick() {
            StaffTreeNodeBean bean = (StaffTreeNodeBean) itemView.getTag();
            if (bean.isStaff()) {
                onCheckedChanged(checkbox);
            } else {
                int location = dataList.indexOf(bean);
                if (bean.getChildren().size() > 0) {
//                    List<StaffTreeNodeBean> oldList = new ArrayList<>();
//                    oldList.addAll(dataList);
                    if (bean.isShowChildren) {
                        List<StaffTreeNodeBean> children = bean.getAllShowedChildren(true);
                        dataList.removeAll(children);
//                        notifyItemRangeRemoved(location + 1, children.size());
//                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyCallBack(oldList, dataList));
//                        diffResult.dispatchUpdatesTo(StaffChooseTreeAdapter.this);
                        notifyDataSetChanged();
                    } else {
                        TreeHandleUtil.Companion.sortData(bean.getChildren());
                        bean.setChildrenLevel();
                        dataList.addAll(location + 1, bean.getChildren());
//                        notifyItemRangeInserted(location + 1, bean.getChildren().size());
                        notifyDataSetChanged();
                    }
                    bean.setShowChildren(!bean.isShowChildren);

//                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyCallBack(oldList, dataList));
//                    diffResult.dispatchUpdatesTo(StaffChooseTreeAdapter.this);
                } else {
                    Toast.makeText(context, bean.getName() + "暂无客户", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @OnClick(R.id.checkbox)
        public void onCheckedChanged(View view) {
            StaffTreeNodeBean bean = (StaffTreeNodeBean) itemView.getTag();
            int location = dataList.indexOf(bean);
            bean.isChecked = !bean.isChecked;
            int count = bean.setChildrenChecked(bean.isChecked);
            notifyItemRangeChanged(location, 1 + count);
//            selectedList.add(bean);
        }


        public StaffChooseItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }

//    public class MyCallBack extends DiffUtil.Callback {
//        List<StaffTreeNodeBean> newList;
//        List<StaffTreeNodeBean> oldList;
//
//        public MyCallBack(List<StaffTreeNodeBean> newList, List<StaffTreeNodeBean> oldList) {
//            this.newList = newList;
//            this.oldList = oldList;
//        }
//
//        @Override
//        public int getOldListSize() {
//            return oldList.size();
//        }
//
//        @Override
//        public int getNewListSize() {
//            return newList.size();
//        }
//
//        @Override
//        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
//        }
//
//        @Override
//        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
//        }
//    }
}
