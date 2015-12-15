package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.adapter.InComeProductAdapter;
import com.clinkworld.pay.entity.IncomeProductBatchInfo;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.*;
import com.clinkworld.pay.views.PullRefreshListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by srh on 2015/10/29.
 * <p/>
 * 入库单
 */
public class StorageListActivity extends BaseActivity {

    private LeftBackRightTextTitleBar titleBar;
    private InComeProductAdapter mAdapter;
    private List<IncomeProductBatchInfo> incomeProductBatchInfoList = new ArrayList<IncomeProductBatchInfo>();
    private int mCurrentPage = 1;
    private final static int PAGE_SIZE = 10;
    private final static int MSG_SEARCH_STORAGE_LIST_SUCCESS = 200;
    private final static int MSG_SEARCH_STORAGE_LIST_FAILURE = 201;
    public final static String UPDATE_STORAGE_PERMISSION = "com.clinkworld.pay.permission.UPDATE_STORAGE_BROADCAST";
    public final static String BROADCAST_UPDATE_STORAGE_ACTION = "com.clinkworld.pay.activity.StorageListActivity.action.refreshStorage";
    private Dialog mLoadingDialog;
    /**
     * 入库单列表
     */
    @ViewInject(R.id.storage_list_view)
    PullRefreshListView mlv;
    /**
     * 入库单单号详情搜索
     */
    @ViewInject(R.id.et_input_storage_number)
    EditText metInputStorageNumber;

    SafeHandler mHandler = new SafeHandler(StorageListActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH_STORAGE_LIST_SUCCESS:
                    /** 加载成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    mlv.setCanLoadMore(msg.getData().getBoolean("canLoadMore"));
                    if (mCurrentPage == 1) {
                        mlv.onRefreshComplete(new Date());
                        mAdapter.getmIncomeProductBatchInfos().clear();
                    } else {
                        mlv.onLoadMoreComplete();
                    }
                    List<IncomeProductBatchInfo> incomeProductBatchInfos = (List<IncomeProductBatchInfo>) msg.obj;
                    mAdapter.getmIncomeProductBatchInfos().addAll(incomeProductBatchInfos);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_SEARCH_STORAGE_LIST_FAILURE:
                    /** 加载失败 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(StorageListActivity.this, errorMessage);
                    } else {
                        ToastUtils.showToast(StorageListActivity.this, getString(R.string.reg_httpclient_fail));
                    }
                    break;
            }
        }
    };

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCurrentPage = 1;
            ClinkWorldApplication.httpHelper.execute(searchStorageListRunnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_UPDATE_STORAGE_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter, UPDATE_STORAGE_PERMISSION, new Handler());
        initView();
        initData();
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.storage_list);
        titleBar.onPostActivityLayout();
        titleBar.setTitle(R.string.sotrage_list_title);
        titleBar.setRightText(R.string.storage_commodity_storage);
        titleBar.setOnRightClickListener(addProductClickLister);
        titleBar.showRightButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void initView() {
        mLoadingDialog = DialogUtils.getLoadingDialog(StorageListActivity.this, "正在加载中...");
        metInputStorageNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchStorageDetail();
                }
                return false;
            }
        });
    }

    private void initData() {
        mAdapter = new InComeProductAdapter(StorageListActivity.this, incomeProductBatchInfoList);
        mlv.setAdapter(mAdapter);
        mlv.setCanLoadMore(false);
        mlv.setCanRefresh(true);
        mlv.setPullRefreshListener(new PullRefreshListView.PullRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 1;
                ClinkWorldApplication.httpHelper.execute(searchStorageListRunnable);
            }

            @Override
            public void onLoadMore() {
                mCurrentPage += 1;
                ClinkWorldApplication.httpHelper.execute(searchStorageListRunnable);
            }
        });
        mLoadingDialog.show();
        ClinkWorldApplication.httpHelper.execute(searchStorageListRunnable);
    }

    private void searchStorageDetail() {
        if (!TextUtils.isEmpty(metInputStorageNumber.getText().toString().trim())) {
            // 先隐藏键盘
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(StorageListActivity.this.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
            Intent intent = new Intent(StorageListActivity.this, StorageDetailActivity.class);
            intent.putExtra(StorageDetailActivity.STORAGE_NUMBER, metInputStorageNumber.getText().toString().trim());
            startActivity(intent);
        }
    }

    View.OnClickListener addProductClickLister = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(StorageListActivity.this, AddProductActivity.class);
            startActivity(intent);
        }
    };

    Runnable searchStorageListRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("page", String.valueOf(mCurrentPage));
            params.put("page_size", String.valueOf(PAGE_SIZE));

            String url = ServerUrl.BASE_URL + ServerUrl.INCOME_BATCH_LIST_PATH;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                mHandler.sendEmptyMessage(MSG_SEARCH_STORAGE_LIST_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.getInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        message.what = MSG_SEARCH_STORAGE_LIST_SUCCESS;
                        boolean canLoadMoren = false;
                        int pageCount = dataJSONObject.getInt("page_count");
                        int currentPage = dataJSONObject.getInt("current_page");
                        if (pageCount > currentPage) {
                            canLoadMoren = true;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("canLoadMore", canLoadMoren);

                        JSONArray listArray = dataJSONObject.getJSONArray("list");
                        List<IncomeProductBatchInfo> incomeProductBatchInfos = new ArrayList<IncomeProductBatchInfo>();
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject itemJSONObject = listArray.getJSONObject(i);
                            IncomeProductBatchInfo incomeProductBatchInfo = new IncomeProductBatchInfo();
                            incomeProductBatchInfo.setId(itemJSONObject.getString("Id"));
                            incomeProductBatchInfo.setMerchantId(itemJSONObject.optString("MerchantId"));
                            incomeProductBatchInfo.setStoreId(itemJSONObject.optString("StoreId"));
                            incomeProductBatchInfo.setRecordId(itemJSONObject.optString("RecordId"));
                            float costValue = Float.parseFloat(itemJSONObject.optString("CostValue"));
                            float totalValue = Float.parseFloat(itemJSONObject.optString("TotalValue"));
                            incomeProductBatchInfo.setMoneryIn(costValue);
                            incomeProductBatchInfo.setMoneryOut(totalValue);
                            incomeProductBatchInfo.setGrossProfit((totalValue - costValue) / totalValue);
                            incomeProductBatchInfo.setCreateUserId(itemJSONObject.optString("CreateUserId"));
                            incomeProductBatchInfo.setDate(itemJSONObject.optString("AddTime"));
                            incomeProductBatchInfos.add(incomeProductBatchInfo);
                        }
                        message.obj = incomeProductBatchInfos;
                    } else {
                        if (resultJSONObject.has("info")) {
                            String errorMessage = resultJSONObject.getString("info");
                            message.obj = errorMessage;
                        }
                    }
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_SEARCH_STORAGE_LIST_FAILURE);
                }
            }

        }
    };
}
