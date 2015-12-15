package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.adapter.OrderSearchListAdapter;
import com.clinkworld.pay.entity.OrderInfo;
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
 * Created by srh on 2015/11/1.
 * <p/>
 * 订单查询
 */
public class OrderSearchActivity extends BaseActivity {

    private LeftBackRightTextTitleBar titleBar;
    private final static int MSG_SEARCH_SUCCESS = 200;
    private final static int MSG_SEARCH_FAILURE = 201;
    Dialog mLoadingDialog;
    private int mCurrentPage = 1;
    private final static int PAGE_SIZE = 10;
    private String lastSearchPhone;
    private List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
    private OrderSearchListAdapter mAdapter;
    /**
     * 查询结果列表条数布局
     */
    @ViewInject(R.id.ll_search_order_result)
    LinearLayout mllSearchResult;
    /**
     * 查询结果列表条数
     */
    @ViewInject(R.id.tv_search_order_number)
    TextView mtvOrderNumber;

    /**
     * 查询结果列表
     */
    @ViewInject(R.id.ll_order_list)
    LinearLayout mllOrderList;
    /**
     * ListView
     */
    @ViewInject(R.id.order_list_view)
    PullRefreshListView mlvOrder;
    /**
     * 订单搜索
     */
    @ViewInject(R.id.et_input_order_number)
    EditText metInputOrderNumber;

    SafeHandler safeHandler = new SafeHandler(OrderSearchActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH_SUCCESS:
                    /** 查询成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }

                    List<OrderInfo> orderInfos = (List<OrderInfo>) msg.obj;
                    mlvOrder.setCanLoadMore(msg.getData().getBoolean("canLoadMore"));
                    if (mCurrentPage == 1) {
                        mtvOrderNumber.setText(String.valueOf(msg.getData().getInt("count")));
                        mlvOrder.onRefreshComplete(new Date());
                        mAdapter.getOrderInfoList().clear();
                    } else {
                        mlvOrder.onLoadMoreComplete();
                    }
                    if (orderInfos.size() > 0) {
                        mllSearchResult.setVisibility(View.VISIBLE);
                        mllOrderList.setVisibility(View.VISIBLE);
                    } else {
                        mllSearchResult.setVisibility(View.VISIBLE);
                        mllOrderList.setVisibility(View.GONE);
                    }
                    mAdapter.getOrderInfoList().addAll(orderInfos);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_SEARCH_FAILURE:
                    /** 查询失败 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    ToastUtils.showToast(OrderSearchActivity.this, "查询订单号失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.order_search);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle(R.string.order_search_title);
    }

    private void initView() {
        metInputOrderNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchOrderList();
                }
                return false;
            }
        });
        mLoadingDialog = DialogUtils.getLoadingDialog(OrderSearchActivity.this, "正在查询订单...");
        mAdapter = new OrderSearchListAdapter(OrderSearchActivity.this, orderInfoList);
        mlvOrder.setAdapter(mAdapter);
        mlvOrder.setCanRefresh(true);
        mlvOrder.setCanLoadMore(false);
        mlvOrder.setPullRefreshListener(new PullRefreshListView.PullRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 1;
                ClinkWorldApplication.httpHelper.execute(new SearchOrderRunnable(lastSearchPhone));
            }

            @Override
            public void onLoadMore() {
                mCurrentPage += 1;
                ClinkWorldApplication.httpHelper.execute(new SearchOrderRunnable(lastSearchPhone));
            }
        });
    }

    /**
     * 查询订单
     */
    private void searchOrderList() {
        if (TextUtils.isEmpty(metInputOrderNumber.getText().toString().trim())) {
            ToastUtils.showToast(OrderSearchActivity.this, "请输入会员手机号");
            return;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(OrderSearchActivity.this.getCurrentFocus()
                                .getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

        if (mLoadingDialog != null) {
            mLoadingDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(new SearchOrderRunnable(metInputOrderNumber.getText().toString()));
    }

    class SearchOrderRunnable implements Runnable {

        private String telephone;

        public SearchOrderRunnable(String telephone) {
            this.telephone = telephone;
        }

        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("telephone", telephone);
            params.put("page", String.valueOf(mCurrentPage));
            String url = ServerUrl.BASE_URL + ServerUrl.ORDER_SEARCH_PATH;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_SEARCH_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        lastSearchPhone = telephone;
                        message.what = MSG_SEARCH_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        int pageCount = dataJSONObject.optInt("page_count");
                        int currentPage = dataJSONObject.optInt("current_page");
                        Bundle bundle = new Bundle();
                        bundle.putInt("count", dataJSONObject.optInt("count"));
                        bundle.putBoolean("canLoadMore", (pageCount > currentPage));
                        message.setData(bundle);
                        JSONArray listArray = dataJSONObject.getJSONArray("list");
                        List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
                        for (int i = 0; i < listArray.length(); i++) {
                            OrderInfo orderInfo = new OrderInfo();
                            JSONObject itemJSONObject = listArray.getJSONObject(i);
                            orderInfo.setOrderNumber(itemJSONObject.optString("OrderID"));
                            orderInfo.setPosNumber(itemJSONObject.optString("PosID"));
                            orderInfo.setOrderMoney(Float.parseFloat(itemJSONObject.optString("Money")));
                            orderInfo.setStatus(itemJSONObject.optInt("OrderStatus"));
                            orderInfoList.add(orderInfo);
                        }
                        message.obj = orderInfoList;
                    } else {
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_SEARCH_FAILURE);
                }
            }
        }
    }
}
