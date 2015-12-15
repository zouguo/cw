package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.adapter.StorageDetailAdapter;
import com.clinkworld.pay.entity.StorageDetailInfo;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.DialogUtils;
import com.clinkworld.pay.util.HttpClientC;
import com.clinkworld.pay.util.SafeHandler;
import com.clinkworld.pay.util.ToastUtils;
import com.clinkworld.pay.views.PullRefreshListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by srh on 2015/10/30.
 * <p/>
 * 查询入库单
 */
public class StorageDetailActivity extends BaseActivity {

    public final static String STORAGE_NUMBER = "storage_number";
    private final static int MSG_SEARCH_STORAGE_SUCCESS = 200;
    private final static int MSG_SEARCH_STORAGE_FAILURE = 201;
    private final static int PAGE_SIZE = 10;
    private int mCurrentPage = 1;
    private LeftBackRightTextTitleBar titleBar;
    private String mStorageNumber;
    private Dialog mLoadingDialog;
    private StorageDetailAdapter mAdapter;
    private List<StorageDetailInfo> storageDetailInfoList = new ArrayList<StorageDetailInfo>();

    @ViewInject(R.id.storage_detail_list)
    PullRefreshListView mListView;

    @ViewInject(R.id.tv_storage_detail_date)
    TextView mtvDate;

    @ViewInject(R.id.tv_storage_detail_no)
    TextView mtvDetailNo;

    SafeHandler mHandler = new SafeHandler(StorageDetailActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH_STORAGE_SUCCESS:
                    /** 搜索成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    mListView.setCanLoadMore(msg.getData().getBoolean("canLoadMore"));
                    if (mCurrentPage == 1) {
                        mtvDate.setText(msg.getData().getString("storage_time"));
                        mListView.onRefreshComplete(new Date());
                        mAdapter.getMstorageDetailInfoList().clear();
                    } else {
                        mListView.onLoadMoreComplete();
                    }
                    List<StorageDetailInfo> storageDetailInfos = (List<StorageDetailInfo>) msg.obj;
                    mAdapter.getMstorageDetailInfoList().addAll(storageDetailInfos);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_SEARCH_STORAGE_FAILURE:
                    /** 搜索失败 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(StorageDetailActivity.this, errorMessage);
                    } else {
                        ToastUtils.showToast(StorageDetailActivity.this, getString(R.string.reg_httpclient_fail));
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadingDialog = DialogUtils.getLoadingDialog(StorageDetailActivity.this, "正在加载中...");
        initView();
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.storage_detail);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle(R.string.sotrage_list_title);
    }

    private void initView() {
        mStorageNumber = getIntent().getStringExtra(STORAGE_NUMBER);
        mtvDetailNo.setText(getString(R.string.storage_number_prompt, mStorageNumber));
        mAdapter = new StorageDetailAdapter(StorageDetailActivity.this, storageDetailInfoList);
        mListView.setAdapter(mAdapter);
        mListView.setCanLoadMore(false);
        mListView.setCanRefresh(true);
        mListView.setPullRefreshListener(new PullRefreshListView.PullRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 1;
                ClinkWorldApplication.httpHelper.execute(searchStorageNumberRunnable);
            }

            @Override
            public void onLoadMore() {
                mCurrentPage += 1;
                ClinkWorldApplication.httpHelper.execute(searchStorageNumberRunnable);
            }
        });
        mLoadingDialog.show();
        ClinkWorldApplication.httpHelper.execute(searchStorageNumberRunnable);
    }

    Runnable searchStorageNumberRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("page_size", String.valueOf(PAGE_SIZE));
            params.put("page", String.valueOf(mCurrentPage));

            String url = ServerUrl.BASE_URL + ServerUrl.INCOME_BATCH_QUERY_PATH + mStorageNumber + "?";
            String result = HttpClientC.getHttpUrlWithParams(url, params);

            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                mHandler.sendEmptyMessage(MSG_SEARCH_STORAGE_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.getInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_SEARCH_STORAGE_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        Bundle bundle = new Bundle();

                        if (mCurrentPage == 1) {
                            JSONObject selfJSONObject = dataJSONObject.getJSONObject("self");
                            bundle.putString("storage_time", selfJSONObject.optString("AddTime"));
                        }

                        JSONObject recordJSONObject = dataJSONObject.getJSONObject("record");
                        int pageCount = recordJSONObject.optInt("page_count");
                        int currentPage = recordJSONObject.optInt("current_page");
                        bundle.putBoolean("canLoadMore", (pageCount > currentPage));
                        message.setData(bundle);

                        JSONArray listArray = recordJSONObject.getJSONArray("list");
                        List<StorageDetailInfo> storageDetailInfos = new ArrayList<StorageDetailInfo>();
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject itemJSONObject = listArray.getJSONObject(i);
                            StorageDetailInfo storageDetailInfo = new StorageDetailInfo();
                            storageDetailInfo.setId(itemJSONObject.optString("Id"));
                            storageDetailInfo.setRecordId(itemJSONObject.optString("RecordId"));
                            storageDetailInfo.setProductBarcode(itemJSONObject.optString("Barcode"));
                            storageDetailInfo.setProductProductName(itemJSONObject.optString("Name"));
                            storageDetailInfo.setProductNumber(itemJSONObject.optInt("Number"));
                            storageDetailInfo.setProductPriceIn(Float.parseFloat(itemJSONObject.optString("CostPrice")));
                            storageDetailInfo.setProductPriceOut(Float.parseFloat(itemJSONObject.optString("SellingPrice")));
                            storageDetailInfo.setAddTime(itemJSONObject.optString("AddTime"));
                            storageDetailInfos.add(storageDetailInfo);
                        }
                        message.obj = storageDetailInfos;
                    } else {
                        message.obj = resultJSONObject.optString("info");
                        message.what = MSG_SEARCH_STORAGE_FAILURE;
                    }
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_SEARCH_STORAGE_FAILURE);
                }
            }
        }
    };
}
