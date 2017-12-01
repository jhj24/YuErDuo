package com.jqyd.yuerduo.fragment;

import android.support.v4.app.Fragment;

import com.baidu.mobstat.StatService;
import com.jqyd.yuerduo.activity.main.TopBar;
import com.orhanobut.logger.Logger;

/**
 * Created by zhangfan on 2015/12/14.
 */
public abstract class BaseFragment extends Fragment {

    public abstract String getTitle();

    public abstract int getIconDefault();

    public abstract int getIconSelected();

    public void doWithTopBar(TopBar topBar) {
        topBar.topBar_title.setText(getTitle());
    }

    @Override
    public void onPause() {
        super.onPause();
//        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        StatService.onResume(this);
    }


    /** Fragment当前状态是否可见 */
    protected boolean isVisible;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
        StatService.onPageStart(getContext(), getTitle());
    }


    /**
     * 不可见
     */
    protected void onInvisible() {
        StatService.onPageEnd(getContext(), getTitle());
    }


    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected void lazyLoad(){
        Logger.i(getTitle());
    }

    /**
     * 页面数据变化
     */
    public void onDataChanged(){

    }
}
