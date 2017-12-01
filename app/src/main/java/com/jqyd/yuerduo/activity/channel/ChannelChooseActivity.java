package com.jqyd.yuerduo.activity.channel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jqyd.yuerduo.R;
import com.jqyd.yuerduo.activity.BaseActivity;
import com.jqyd.yuerduo.activity.staff.ChannelChooseListAdapter;
import com.jqyd.yuerduo.bean.ChannelTreeNodeBean;
import com.jqyd.yuerduo.constant.URLConstant;
import com.jqyd.yuerduo.fragment.contacts.CharacterUtil;
import com.jqyd.yuerduo.net.GsonDialogHttpResponseHandler;
import com.jqyd.yuerduo.net.ParamBuilder;
import com.jqyd.yuerduo.net.RequestClient;
import com.jqyd.yuerduo.net.ResultHolder;
import com.jqyd.yuerduo.util.TreeHandleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 客户选择界面
 */
public class ChannelChooseActivity extends BaseActivity {

    public static final int REQUEST_CODE = 1005;

    @Bind(R.id.topBar_title)
    TextView topBarTitle;
    @Bind(R.id.topBar_right_button)
    TextView topBarRightButton;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.rv_search)
    RecyclerView rvSearch;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.searchBarMask)
    RelativeLayout layoutSearch;

    @Bind(R.id.bt_reload)
    Button btReload;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.ll_reload)
    LinearLayout llReload;
    private ChannelChooseTreeAdapter adapter;
    private List<ChannelTreeNodeBean> selectDataList = new ArrayList<>();
    private CharacterUtil character;
    private List<ChannelTreeNodeBean> beanList = new ArrayList<>();
    private List<ChannelTreeNodeBean> baseDataList = new ArrayList<>();
    private ChannelChooseListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_choose);
        character = CharacterUtil.getInstance();
        List<ChannelTreeNodeBean> selectData = (List<ChannelTreeNodeBean>) getIntent().getSerializableExtra("selectData");
        if (selectData != null && selectData.size() > 0) {
            selectDataList.addAll(selectData);
        }
        ButterKnife.bind(this);
        topBarTitle.setText("选择客户");
        topBarRightButton.setVisibility(View.VISIBLE);
        topBarRightButton.setText("确认");

        layoutSearch.setVisibility(View.VISIBLE);
        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeHandleUtil.Companion.onSearch(layoutSearch, recyclerView, rvSearch, etSearch);
            }
        });
        textChangerListener();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChannelChooseTreeAdapter(this);
        listAdapter = new ChannelChooseListAdapter(this);
        recyclerView.setAdapter(adapter);
        rvSearch.setAdapter(listAdapter);

        initData();
    }

    private void textChangerListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(etSearch.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void filterData(String text) {
        List<ChannelTreeNodeBean> filterDataList = new ArrayList<>();
        if (text.length() == 0) {
            recyclerView.setVisibility(View.VISIBLE);
            rvSearch.setVisibility(View.GONE);
            filterDataList = baseDataList;
            adapter.updateListView(filterDataList);
        } else {
            recyclerView.setVisibility(View.GONE);
            rvSearch.setVisibility(View.VISIBLE);
            filterDataList.clear();
            for (ChannelTreeNodeBean bean : beanList) {
                if (bean.isChannel() && TreeHandleUtil.Companion.isFilter(character,text,bean.getName())) {
                    filterDataList.add(bean);
                }
            }
            Collections.sort(filterDataList, new Comparator<ChannelTreeNodeBean>() {
                @Override
                public int compare(ChannelTreeNodeBean o1, ChannelTreeNodeBean o2) {
                    return o1.spelling.compareTo(o2.spelling);
                }
            });
            listAdapter.updateListView(filterDataList);
        }
    }

    @OnClick(R.id.bt_reload)
    public void onReRequest() {
//        btReload.setVisibility(View.GONE);
        llReload.setVisibility(View.GONE);
        initData();
    }

    @OnClick(R.id.topBar_right_button)
    public void onOk() {
        ArrayList<ChannelTreeNodeBean> result = new ArrayList<>();
        getCheckedItem(result, adapter.dataList);
        Intent intent = new Intent();
        intent.putExtra("channelList", result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void getCheckedItem(List<ChannelTreeNodeBean> result, List<ChannelTreeNodeBean> dataList) {
        for (ChannelTreeNodeBean bean : dataList) {
            if (bean.getChildren().size() > 0) {
                getCheckedItem(result, bean.getChildren());
            }
            if (bean.isChannel() && bean.isChecked && result.indexOf(bean) == -1) {
                result.add(bean);
            }
        }
    }

    private void initData() {
        RequestClient.request(this, URLConstant.GET_CHANNEL_TREE, ParamBuilder.GET_CHANNEL_TREE(), new GsonDialogHttpResponseHandler<ChannelTreeNodeBean>(this, "请稍后") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final ResultHolder<ChannelTreeNodeBean> response) {
                super.onSuccess(statusCode, headers, rawJsonResponse, response);
                Observable
                        .create(new Observable.OnSubscribe<List<ChannelTreeNodeBean>>() {
                            @Override
                            public void call(Subscriber<? super List<ChannelTreeNodeBean>> subscriber) {
                                List<ChannelTreeNodeBean> dataList = response.getDataList();
                                List<ChannelTreeNodeBean> list = new ArrayList<>();
                                for (ChannelTreeNodeBean bean : dataList) {
                                    if (bean.getChildren().size() > 0) {
                                        list.add(bean);
                                        list.addAll(bean.getAllShowedChildren());
                                    } else {
                                        list.add(bean);
                                    }
                                }
                                for (ChannelTreeNodeBean bean : list) {
                                    bean.spelling = character.getStringSpelling(bean.getName(), true);
                                    if (bean.getChildren() != null && bean.getChildren().size() > 0) {
                                        bean.childrenSize = 1;
                                    } else {
                                        bean.childrenSize = 0;
                                    }

                                }
                                TreeHandleUtil.Companion.sortData(dataList);
                                beanList = list;
                                baseDataList = dataList;
                                recursionCheckState(dataList);
                                subscriber.onNext(dataList);
                            }
                        })
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<ChannelTreeNodeBean>>() {
                            @Override
                            public void call(List<ChannelTreeNodeBean> channelTreeNodeBeenList) {
                                adapter.dataList = channelTreeNodeBeenList;
                                adapter.notifyDataSetChanged();
                                setTipText(true);
                            }
                        });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResultHolder<ChannelTreeNodeBean> errorResponse) {
                super.onFailure(statusCode, headers, throwable, rawJsonData, errorResponse);
//                btReload.setVisibility(View.VISIBLE);
                llReload.setVisibility(View.VISIBLE);
                setTipText(false);
            }
        });
    }

    private boolean recursionCheckState(List<ChannelTreeNodeBean> dataList) {
        boolean allChildrendChecked = true;
        for (ChannelTreeNodeBean node : dataList) {
            if (node.isChannel()) {
                allChildrendChecked = setBeanCheckedState(node) && allChildrendChecked;
            } else {
                boolean checkedState = recursionCheckState(node.getChildren());
                node.isChecked = checkedState;
                allChildrendChecked = checkedState && allChildrendChecked;
            }
        }
        if (dataList.size() == 0) {
            allChildrendChecked = false;
        }
        return allChildrendChecked;
    }

    private boolean setBeanCheckedState(ChannelTreeNodeBean bean) {
        for (ChannelTreeNodeBean node : selectDataList) {
            if (node.getId().equals(bean.getId()) && node.isChannel() == bean.isChannel() && node.getName().equals(bean.getName())) {
                bean.isChecked = true;
                return true;
            }
        }
        return false;
    }

    private void setTipText(boolean success) {
        if (success) {
            tvTip.setText("没有数据");
        } else {
            tvTip.setText("查询失败");
        }
    }
}
