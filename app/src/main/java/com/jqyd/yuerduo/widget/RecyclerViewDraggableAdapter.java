package com.jqyd.yuerduo.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * 可拖拽item的RecyclerView的Adapter
 * Created by zhangfan on 2015/12/21.
 */
public abstract class RecyclerViewDraggableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements ItemTouchHelperAdapter {
    abstract protected List<?> getDataList();

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        List<Object> dataList = (List<Object>) getDataList();
        Object object = dataList.get(fromPosition);
        dataList.remove(fromPosition);
        dataList.add(toPosition, object);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        getDataList().remove(position);
        notifyItemRemoved(position);
    }

    public abstract Context getContext();

    public void setEditState(boolean editState) {
        this.editState = editState;
    }

    public boolean isEditState() {
        return editState;
    }

    private boolean editState = false;
}
