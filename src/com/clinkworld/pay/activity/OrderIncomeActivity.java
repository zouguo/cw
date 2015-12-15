package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.clinkworld.pay.adapter.OrderIncomeAdapter;
import com.clinkworld.pay.entity.OrderDetailInfo;
import com.clinkworld.pay.entity.OrderInfo;
import com.clinkworld.pay.entity.StorageDetailInfo;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.DialogUtils;
import com.clinkworld.pay.util.HttpClientC;
import com.clinkworld.pay.util.SafeHandler;
import com.clinkworld.pay.util.ToastUtils;
import com.clinkworld.pay.views.PullRefreshListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by srh on 2015/11/2.
 * <p/>
 * 今日收入页面
 */
public class OrderIncomeActivity extends BaseActivity {

    public final static String INCOME_TYPE = "income_type";
    public final static String ORDER_TIME = "order_time";
    public final static String INCOME_MONEY = "income_money";
    private final static int MSG_ORDER_LIST_SUCCESS = 200;
    private final static int MSG_ORDER_LIST_FAILURE = 201;
    private final static int MSG_ORDER_LIST_ERROR = 202;
    private final static int MSG_DETAIL_SEARCH_SUCCESS = 203;
    private final static int MSG_DETAIL_SEARCH_FAILURE = 204;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private float incomeMoney;
    private LeftBackRightTextTitleBar titleBar;
    private OrderIncomeAdapter mAdapter;
    private String timetype;
    private int mCurrentPage = 1;
    private Dialog mLoadingDialog;
    private Dialog mSearchOrderDialog;
    private List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
    /**
     * 收入类型
     * 3：现金
     * 4：支付宝
     * 5：微信
     */
    private int incomeType;
    /**
     * 当前显示的订单页面指定日期类型
     * 0：今日收入
     * 1：本周收入
     * 2：当月收入
     * 3：全部收入
     */
    private int timeType;

    @ViewInject(R.id.income_list)
    PullRefreshListView mlv;

    @ViewInject(R.id.tv_income_type)
    TextView mtvIncomeType;

    @ViewInject(R.id.et_input_order_number)
    EditText metInputOrderNumber;

    @ViewInject(R.id.tv_money)
    TextView mtvMoney;

    @ViewInject(R.id.tv_time)
    TextView mtvTime;

    @ViewInject(R.id.tv_order_num)
    TextView mtvOrderNumber;

    SafeHandler safeHandler = new SafeHandler(OrderIncomeActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ORDER_LIST_SUCCESS:
                    /** 查询订单成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    int count = msg.getData().getInt("count", -1);
                    if (count != -1) {
                        mtvOrderNumber.setText(String.valueOf(count));
                    }
                    mlv.setCanLoadMore(msg.getData().getBoolean("canLoadMore"));
                    if (mCurrentPage == 1) {
                        mlv.onRefreshComplete(new Date());
                        mAdapter.getOrderInfoList().clear();
                    } else {
                        mlv.onLoadMoreComplete();
                    }
                    List<OrderInfo> orderInfos = (List<OrderInfo>) msg.obj;
                    mAdapter.getOrderInfoList().addAll(orderInfos);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_ORDER_LIST_ERROR:
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    mlv.onLoadMoreComplete();
                    mlv.setCanLoadMore(false);
                    String errorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(OrderIncomeActivity.this, errorMessage);
                    }
                    break;
                case MSG_ORDER_LIST_FAILURE:
                    /** 查询订单失败 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    mlv.onLoadMoreComplete();
                    mlv.setCanLoadMore(false);
                    ToastUtils.showToast(OrderIncomeActivity.this, getString(R.string.reg_httpclient_fail));
                    break;
                case MSG_DETAIL_SEARCH_SUCCESS:
                    /** 订单详情获取成功 */
                    if (mSearchOrderDialog != null) {
                        mSearchOrderDialog.dismiss();
                    }
                    OrderDetailInfo orderDetailInfo = (OrderDetailInfo) msg.obj;
                    Intent intent = new Intent(OrderIncomeActivity.this, OrderDetailActivity.class);
                    intent.putExtra(OrderDetailActivity.ORDER_ID, orderDetailInfo.getOrderId());
                    intent.putExtra(OrderDetailActivity.ORDER_STATUS, orderDetailInfo.getPayStatus());
                    startActivity(intent);
                    finish();
                    break;
                case MSG_DETAIL_SEARCH_FAILURE:
                    /** 订单详情获取失败 */
                    if (mSearchOrderDialog != null) {
                        mSearchOrderDialog.dismiss();
                    }
                    String detailErrorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(detailErrorMessage)) {
                        ToastUtils.showToast(OrderIncomeActivity.this, detailErrorMessage);
                    } else {
                        ToastUtils.showToast(OrderIncomeActivity.this, getString(R.string.reg_httpclient_fail));
                    }
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
        setContentView(R.layout.order_income);
        titleBar.onPostActivityLayout();
        titleBar.setTitle("");
        titleBar.hideRightButton();
    }

    private void initView() {
        incomeType = getIntent().getIntExtra(INCOME_TYPE, 0);
        timeType = getIntent().getIntExtra(ORDER_TIME, 0);
        incomeMoney = getIntent().getFloatExtra(INCOME_MONEY, 0);
        mtvMoney.setText("￥" + decimalFormat.format(incomeMoney));
        mLoadingDialog = DialogUtils.getLoadingDialog(OrderIncomeActivity.this, "正在收入订单...");
        mSearchOrderDialog = DialogUtils.getLoadingDialog(OrderIncomeActivity.this, "正在搜索订单号...");
        switch (timeType) {
            case 0:
                timetype = "1";
                mtvTime.setText(getString(R.string.order_flow_today));
                titleBar.setTitle(getString(R.string.order_flow_today) + "收入");
                break;
            case 1:
                timetype = "2";
                mtvTime.setText(getString(R.string.order_flow_this_week));
                titleBar.setTitle(getString(R.string.order_flow_this_week) + "收入");
                break;
            case 2:
                timetype = "3";
                mtvTime.setText(getString(R.string.order_flow_this_monty));
                titleBar.setTitle(getString(R.string.order_flow_this_monty) + "收入");
                break;
            case 3:
                timetype = "";
                mtvTime.setText(getString(R.string.order_flow_all));
                titleBar.setTitle(getString(R.string.order_flow_all) + "收入");
                break;
        }
        mAdapter = new OrderIncomeAdapter(OrderIncomeActivity.this, orderInfoList);
        mlv.setAdapter(mAdapter);
        mlv.setCanRefresh(true);
        mlv.setCanLoadMore(false);
        mlv.setPullRefreshListener(new PullRefreshListView.PullRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 1;
                ClinkWorldApplication.httpHelper.execute(searchOrderRunnable);
            }

            @Override
            public void onLoadMore() {
                mCurrentPage += 1;
                ClinkWorldApplication.httpHelper.execute(searchOrderRunnable);
            }
        });
        switch (incomeType) {
            case 3:
                mtvIncomeType.setText(getString(R.string.order_flow_cash_receipts));
                break;
            case 4:
                mtvIncomeType.setText(getString(R.string.order_flow_weixin));
                break;
            case 5:
                mtvIncomeType.setText(getString(R.string.order_flow_zhifubao));
                break;
        }

        metInputOrderNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchOrderDetail();
                }
                return false;
            }
        });

        if (mLoadingDialog != null) {
            mLoadingDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(searchOrderRunnable);
    }

    /**
     * 查询订单
     */
    private void searchOrderDetail() {
        if (TextUtils.isEmpty(metInputOrderNumber.getText().toString().trim())) {
            ToastUtils.showToast(OrderIncomeActivity.this, "请输入订单号");
            return;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(OrderIncomeActivity.this.getCurrentFocus()
                                .getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

        if (mSearchOrderDialog != null) {
            mSearchOrderDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(searchOrderDetailRunnable);
    }

    Runnable searchOrderDetailRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("order_id", metInputOrderNumber.getText().toString());
            String url = ServerUrl.BASE_URL + ServerUrl.ORDER_DETAIL_PATH;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_DETAIL_SEARCH_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_DETAIL_SEARCH_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
                        orderDetailInfo.setOrderId(dataJSONObject.optString("order_id"));
                        if (!dataJSONObject.isNull("order_money")) {
                            orderDetailInfo.setOrderMoney(Float.parseFloat(dataJSONObject.optString("order_money")));
                        }
                        orderDetailInfo.setPayChannelId(dataJSONObject.optInt("pay_channel_id"));
                        orderDetailInfo.setMemberTelphone(dataJSONObject.optString("member_telephone"));
                        orderDetailInfo.setCouponsTitle(dataJSONObject.optString("coupon_title"));
                        orderDetailInfo.setCouponsKey(dataJSONObject.optString("coupon_key"));
                        orderDetailInfo.setCouponsDiscountDescription(dataJSONObject.optString("coupon_discount_string"));
                        if (!dataJSONObject.isNull("should_receive_money")) {
                            orderDetailInfo.setShouldReceiveMoney(Float.parseFloat(dataJSONObject.optString("should_receive_money")));
                        }
                        if (!dataJSONObject.isNull("real_receive_money")) {
                            orderDetailInfo.setRealReceiveMoney(Float.parseFloat(dataJSONObject.optString("real_receive_money")));
                        }
                        orderDetailInfo.setPayStatus(dataJSONObject.optInt("OrderStatus"));
                        List<StorageDetailInfo> storageDetailInfoList = new ArrayList<StorageDetailInfo>();
                        JSONArray productListArray = dataJSONObject.getJSONArray("product_list");
                        for (int i = 0; i < productListArray.length(); i++) {
                            StorageDetailInfo storageDetailInfo = new StorageDetailInfo();
                            JSONObject itemJSONObject = productListArray.getJSONObject(i);
                            storageDetailInfo.setProductProductName(itemJSONObject.optString("Name"));
                            storageDetailInfo.setProductBarcode(itemJSONObject.optString("Barcode"));
                            storageDetailInfo.setProductNumber(itemJSONObject.optInt("Number"));
                            if (!itemJSONObject.isNull("SellingPrice")) {
                                storageDetailInfo.setProductPriceOut(Float.parseFloat(itemJSONObject.optString("SellingPrice")));
                            }
                            storageDetailInfoList.add(storageDetailInfo);
                        }
                        orderDetailInfo.setStorageDetailInfoList(storageDetailInfoList);
                        message.obj = orderDetailInfo;
                    } else {
                        message.what = MSG_DETAIL_SEARCH_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_DETAIL_SEARCH_FAILURE);
                }
            }

        }
    };

    Runnable searchOrderRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("timetype", timetype);
            params.put("ordertype", String.valueOf(incomeType));
            params.put("page", String.valueOf(mCurrentPage));
            String url = ServerUrl.BASE_URL + ServerUrl.ORDER_TYPE_SEARCH_PATH;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_ORDER_LIST_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_ORDER_LIST_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        Bundle bundle = new Bundle();
                        int pageCount = dataJSONObject.optInt("page_count");
                        int currentPage = dataJSONObject.optInt("current_page");
                        int count = dataJSONObject.optInt("count");
                        bundle.putInt("count", count);
                        bundle.putBoolean("canLoadMore", (pageCount > currentPage));
                        message.setData(bundle);

                        JSONArray listArray = dataJSONObject.getJSONArray("list");
                        List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject itemJSONObject = listArray.getJSONObject(i);
                            OrderInfo orderInfo = new OrderInfo();
                            orderInfo.setOrderNumber(itemJSONObject.optString("OrderId"));
                            orderInfo.setPosNumber(itemJSONObject.optString("PosId"));
                            orderInfo.setOrderCompleteTime(itemJSONObject.optString("ReceivablesTime"));
                            orderInfo.setOrderCreateTime(itemJSONObject.optString("SaleDateTime"));
                            orderInfo.setGatheringType(itemJSONObject.optInt("PayChannel"));
                            orderInfo.setStatus(itemJSONObject.optInt("OrderStatus"));
                            if (!itemJSONObject.isNull("TotalMoney")) {
                                orderInfo.setOrderMoney(Float.parseFloat(itemJSONObject.optString("TotalMoney")));
                            }
                            orderInfo.setOrderGatheringTime(itemJSONObject.optString("ReceivablesTime"));
                            orderInfoList.add(orderInfo);
                        }
                        message.obj = orderInfoList;
                    } else {
                        message.what = MSG_ORDER_LIST_ERROR;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_ORDER_LIST_FAILURE);
                }
            }
        }
    };

}
