package com.jqyd.yuerduo.fragment.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jqyd.yuerduo.R;
import com.jqyd.yuerduo.constant.URLConstant;
import com.jqyd.yuerduo.util.GlideCircleTransform;
import com.norbsoft.typefacehelper.TypefaceHelper;

import java.util.List;

import static com.jqyd.yuerduo.R.id.iv_acator;


public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<SortModel> list = null;
    private Context mContext;

    public SortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public List<SortModel> getDataList() {
        return list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<SortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_item_contacts, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.ivAcatar = (ImageView) view.findViewById(iv_acator);
            viewHolder.lineSplit = view.findViewById(R.id.line_split);
            view.setTag(viewHolder);
            TypefaceHelper.typeface(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters().substring(0, 1));
            viewHolder.lineSplit.setVisibility(View.GONE);
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
            viewHolder.lineSplit.setVisibility(View.VISIBLE);
        }

        viewHolder.tvTitle.setText(this.list.get(position).getName());
        // ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + mContent.getImageUrl(), viewHolder.ivAcatar, ImgUtil.getOption(R.drawable.no_avatar));
        Glide.with(mContext)
                .load(URLConstant.ServiceHost + mContent.getImageUrl())
                .placeholder(R.drawable.no_avatar_circle)
                .centerCrop()
                .transform(new GlideCircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.ivAcatar);
        return view;

    }

    final static class ViewHolder {
        View lineSplit;
        TextView tvLetter;
        TextView tvTitle;
        ImageView ivAcatar;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}