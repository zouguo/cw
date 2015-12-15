package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.adapter.OrderListAdapter;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 订单页面包括(全部订单、成交订单、未成交订单)
 */
public class OrderListActivity extends BaseActivity {

    private LeftBackRightTextTitleBar titleBar;
    private final static int MSG_ORDER_LIST_SUCCESS = 200;
    private final static int MSG_ORDER_LIST_FAILURE = 201;
    private final static int MSG_DETAIL_SEARCH_SUCCESS = 202;
    private final static int MSG_DETAIL_SEARCH_FAILURE = 203;
    public final static String ORDER_STATUS = "order_status";
    public final static String ORDER_TIME = "order_time";
    private List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
    private OrderListAdapter mAdapter;
    private Dialog mLoadingDialog;
    private Dialog mSearchOrderDialog;
    private int mCurrentPage = 1;
    private String timetype;
    private String ordertype;
    @ViewInject(R.id.order_list)
    PullRefreshListView mOrderListView;

    @ViewInject(R.id.tv_1)
    TextView mtvItem1;

    @ViewInject(R.id.tv_2)
    TextView mtvItem2;

    @ViewInject(R.id.tv_3)
    TextView mtvItem3;

    @ViewInject(R.id.et_input_order_number)
    EditText metInputOrderNumber;

    /**
     * 当前显示的订单页面状态类型
     * 0：全部订单
     * 1：成交订单
     * 2：未成交订单
     */
    private int statusType;
    /**
     * 当前显示的订单页面指定日期类型
     * 0：今日订单
     * 1：本周订单
     * 2：当月订单
     * 3：全部订单
     */
    private int timeType;


    SafeHandler safeHandler = new SafeHandler(OrderListActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ORDER_LIST_SUCCESS:
                    /** 查询订单成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    mOrderListView.setCanLoadMore(msg.getData().getBoolean("canLoadMore"));
                    if (mCurrentPage == 1) {
                        mOrderListView.onRefreshComplete(new Date());
                        mAdapter.getOrderInfoList().clear();
                    } else {
                        mOrderListView.onLoadMoreComplete();
                    }
                    List<OrderInfo> orderInfos = (List<OrderInfo>) msg.obj;
                    mAdapter.getOrderInfoList().addAll(orderInfos);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_ORDER_LIST_FAILURE:
                    /** 查询订单失败 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(OrderListActivity.this, errorMessage);
                    } else {
                        ToastUtils.showToast(OrderListActivity.this, getString(R.string.reg_httpclient_fail));
                    }
                    break;
                case MSG_DETAIL_SEARCH_SUCCESS:
                    /** 订单详情获取成功 */
                    if (mSearchOrderDialog != null) {
                        mSearchOrderDialog.dismiss();
                    }
                    OrderDetailInfo orderDetailInfo = (OrderDetailInfo) msg.obj;
                    Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
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
                        ToastUtils.showToast(OrderListActivity.this, detailErrorMessage);
                    } else {
                        ToastUtils.showToast(OrderListActivity.this, getString(R.string.reg_httpclient_fail));
                    }
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.order_list);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        statusType = getIntent().getIntExtra(ORDER_STATUS, 0);
        timeType = getIntent().getIntExtra(ORDER_TIME, 0);
    }

    private void initView() {
        mLoadingDialog = DialogUtils.getLoadingDialog(OrderListActivity.this, "正在加载订单...");
        mSearchOrderDialog = DialogUtils.getLoadingDialog(OrderListActivity.this, "正在搜索订单号...");
        mAdapter = new OrderListAdapter(OrderListActivity.this, statusType, orderInfoList);
        mOrderListView.setAdapter(mAdapter);
        mOrderListView.setCanRefresh(true);
        mOrderListView.setCanLoadMore(false);
        mOrderListView.setPullRefreshListener(new PullRefreshListView.PullRefreshListener() {
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

        StringBuffer buffer = new StringBuffer();
        switch (timeType) {
            case 0:
                buffer.append(getString(R.string.order_flow_today));
                timetype = "1";
                break;
            case 1:
                timetype = "2";
                buffer.append(getString(R.string.order_flow_this_week));
                break;
            case 2:
                timetype = "3";
                buffer.append(getString(R.string.order_flow_this_monty));
                break;
            default:
                timetype = "";
                break;
        }
        switch (statusType) {
            case 0:
                ordertype = "0";
                buffer.append(getString(R.string.order_flow_all_ordre));
                mtvItem1.setText(R.string.order_list_create_time);
                mtvItem2.setText(R.string.order_list_complete_time);
                mtvItem3.setText(R.string.order_search_item_status);
                break;
            case 1:
                ordertype = "1";
                buffer.append(getString(R.string.order_flow_finished_order));
                mtvItem1.setText(R.string.order_list_gathering_time);
                mtvItem2.setText(R.string.order_search_item_money);
                mtvItem3.setText(R.string.order_list_gathering_type);
                break;
            case 2:
                ordertype = "2";
                buffer.append(getString(R.string.order_flow_not_finished_order));
                mtvItem1.setText(R.string.order_list_create_time);
                mtvItem2.setText(R.string.order_search_item_money);
                mtvItem3.setText(R.string.order_search_item_status);
                break;
        }
        titleBar.setTitle(buffer.toString());

        metInputOrderNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchOrderDetail();
                }
                return false;
            }
        });
    }

    /**
     * 查询订单
     */
    private void searchOrderDetail() {
        if (TextUtils.isEmpty(metInputOrderNumber.getText().toString().trim())) {
            ToastUtils.showToast(OrderListActivity.this, "请输入订单号");
            return;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(OrderListActivity.this.getCurrentFocus()
                                .getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

        if (mSearchOrderDialog != null) {
            mSearchOrderDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(searchOrderDetailRunnable);
    }

    private void initData() {
        if (mLoadingDialog != null) {
            mLoadingDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(searchOrderRunnable);
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
            params.put("ordertype", ordertype);
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
                        bundle.putBoolean("canLoadMore", (pageCount > currentPage));
                        message.setData(bundle);

                        JSONArray listArray = dataJSONObject.getJSONArray("list");
                        List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject itemJSONObject = listArray.getJSONObject(i);
                            OrderInfo orderInfo = new OrderInfo();
                            orderInfo.setOrderNumber(itemJSONObject.optString("OrderId"));
                            orderInfo.setPosNumber(itemJSONObject.optString("PosId"));
                            String receivableTime = itemJSONObject.optString("ReceivablesTime");
                            orderInfo.setOrderCompleteTime(itemJSONObject.optString("ReceivablesTime"));
                            orderInfo.setOrderCompleteTime(receivableTime);
                            orderInfo.setOrderCreateTime(itemJSONObject.optString("SaleDateTime"));
                            orderInfo.setGatheringType(itemJSONObject.optInt("PayChannel"));
                            orderInfo.setStatus(itemJSONObject.optInt("OrderStatus"));
                            orderInfo.setOrderMoney(Float.parseFloat(itemJSONObject.optString("TotalMoney")));
                            orderInfo.setOrderGatheringTime(itemJSONObject.optString("ReceivablesTime"));
                            orderInfoList.add(orderInfo);
                        }
                        message.obj = orderInfoList;
                    } else {
                        message.what = MSG_ORDER_LIST_FAILURE;
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
