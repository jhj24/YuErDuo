package com.jqyd.yuerduo.activity.staff;

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
import com.jqyd.yuerduo.bean.StaffTreeNodeBean;
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

public class StaffChooseActivity extends BaseActivity {

    public static final int REQUEST_CODE = 1004;

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
    private StaffChooseTreeAdapter adapter;
    private StaffChooseListAdapter listAdapter;
    private List<StaffTreeNodeBean> selectDataList = new ArrayList<>();
    private CharacterUtil character;
    private List<StaffTreeNodeBean> beanList = new ArrayList<>();
    private List<StaffTreeNodeBean> baseDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_choose);
        character = CharacterUtil.getInstance();
        List<StaffTreeNodeBean> selectData = (List<StaffTreeNodeBean>) getIntent().getSerializableExtra("selectData");
        if (selectData != null && selectData.size() > 0) {
            selectDataList = selectData;
        }
        ButterKnife.bind(this);
        topBarTitle.setText("选择员工");
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
        adapter = new StaffChooseTreeAdapter(this);
        listAdapter = new StaffChooseListAdapter(this);
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
        List<StaffTreeNodeBean> filterDataList = new ArrayList<>();
        if (text.length() == 0) {
            recyclerView.setVisibility(View.VISIBLE);
            rvSearch.setVisibility(View.GONE);
            filterDataList = baseDataList;
            adapter.updateListView(filterDataList);
        } else {
            recyclerView.setVisibility(View.GONE);
            rvSearch.setVisibility(View.VISIBLE);
            filterDataList.clear();
            for (StaffTreeNodeBean bean : beanList) {
                if (bean.isStaff() && TreeHandleUtil.Companion.isFilter(character, text, bean.getName())) {
                    filterDataList.add(bean);
                }
            }
            Collections.sort(filterDataList, new Comparator<StaffTreeNodeBean>() {
                @Override
                public int compare(StaffTreeNodeBean o1, StaffTreeNodeBean o2) {
                    return o1.spelling.compareTo(o2.spelling);
                }
            });
            listAdapter.updateListView(filterDataList);
        }

    }

    @OnClick(R.id.bt_reload)
    public void onReRequest() {
        llReload.setVisibility(View.GONE);
        initData();
    }

    @OnClick(R.id.topBar_right_button)
    public void onOk() {
        ArrayList<StaffTreeNodeBean> result = new ArrayList<>();
        getCheckedItem(result, adapter.dataList);
        Intent intent = new Intent();
        intent.putExtra("staffList", result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void getCheckedItem(List<StaffTreeNodeBean> result, List<StaffTreeNodeBean> dataList) {
        for (StaffTreeNodeBean bean : dataList) {
            if (bean.getChildren().size() > 0) {
                getCheckedItem(result, bean.getChildren());
            }
            if (bean.isStaff() && bean.isChecked && result.indexOf(bean) == -1) {
                result.add(bean);
            }
        }
    }

    private void initData() {
        RequestClient.request(this, URLConstant.DEPT_STAFF, ParamBuilder.DeptStaff(), new GsonDialogHttpResponseHandler<StaffTreeNodeBean>(this, "请稍后") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, final ResultHolder<StaffTreeNodeBean> response) {
                super.onSuccess(statusCode, headers, rawJsonResponse, response);
                Observable
                        .create(new Observable.OnSubscribe<List<StaffTreeNodeBean>>() {
                            @Override
                            public void call(Subscriber<? super List<StaffTreeNodeBean>> subscriber) {
                                List<StaffTreeNodeBean> dataList = response.getDataList();
                                List<StaffTreeNodeBean> list = new ArrayList<>();
                                for (StaffTreeNodeBean bean : dataList) {
                                    if (bean.getChildren().size() > 0) {
                                        list.add(bean);
                                        list.addAll(bean.getAllShowedChildren());
                                    } else {
                                        list.add(bean);
                                    }
                                }
                                for (StaffTreeNodeBean bean : list) {
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
                        .subscribe(new Action1<List<StaffTreeNodeBean>>() {
                            @Override
                            public void call(List<StaffTreeNodeBean> staffTreeNodeBeenList) {
                                adapter.dataList = staffTreeNodeBeenList;
                                adapter.notifyDataSetChanged();
                                setTipText(true);
                            }
                        });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, ResultHolder<StaffTreeNodeBean> errorResponse) {
                super.onFailure(statusCode, headers, throwable, rawJsonData, errorResponse);
                llReload.setVisibility(View.VISIBLE);
                setTipText(false);
            }
        });
    }

    private boolean recursionCheckState(List<StaffTreeNodeBean> dataList) {
        boolean allChildrendChecked = true;
        for (StaffTreeNodeBean node : dataList) {
            if (node.isStaff()) {
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

    private boolean setBeanCheckedState(StaffTreeNodeBean bean) {
        for (StaffTreeNodeBean node : selectDataList) {
            if (node.getId().equals(bean.getId()) && node.isStaff() == bean.isStaff() && node.getName().equals(bean.getName())) {
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
