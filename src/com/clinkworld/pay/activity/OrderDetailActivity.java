package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.adapter.OrderDetailProductListAdapter;
import com.clinkworld.pay.entity.OrderDetailInfo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 订单详情
 */
public class OrderDetailActivity extends BaseActivity {

    Dialog mLoadingDialog;
    private LeftBackRightTextTitleBar titleBar;
    private OrderDetailProductListAdapter mAdapter;
    private String orderId;
    private OrderDetailInfo orderDetailInfo;
    private List<StorageDetailInfo> storageDetailInfoList = new ArrayList<StorageDetailInfo>();
    private final static int MSG_DETAIL_SEARCH_SUCCESS = 200;
    private final static int MSG_DETAIL_SEARCH_FAILURE = 201;
    public final static String ORDER_STATUS = "order_status";
    public final static String ORDER_ID = "order_id";
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    /**
     * 订单状态
     * 1：未付款
     * other：付款成功
     */
    private int status;
    /**
     * 订单编号
     */
    @ViewInject(R.id.detail_order_number)
    TextView mtvOrderNumber;
    /**
     * 订单金额
     */
    @ViewInject(R.id.detail_order_money)
    TextView mtvOrderMoney;
    /**
     * 会员手机号
     */
    @ViewInject(R.id.detail_telephone)
    TextView mtvDetailTelephone;
    /**
     * 支付方式
     */
    @ViewInject(R.id.detail_pay_type)
    TextView mtvPayType;
    /**
     * 优惠券
     */
    @ViewInject(R.id.detail_coupons)
    TextView mtvCoupons;
    /**
     * 应收金额
     */
    @ViewInject(R.id.detail_receivable_money)
    TextView mtvReceivableMoney;
    /**
     * 实收金额
     */
    @ViewInject(R.id.detail_paid_money)
    TextView mtvPaidMoney;
    /**
     * 支付方式布局
     */
    @ViewInject(R.id.rl_pay_type)
    RelativeLayout mrlPayType;
    /**
     * 应收金额布局
     */
    @ViewInject(R.id.rl_receiverable)
    RelativeLayout mrlReceiverable;
    /**
     * 实收金额布局
     */
    @ViewInject(R.id.rl_paid)
    RelativeLayout mrlPaid;
    /**
     * 支付方式虚线
     */
    @ViewInject(R.id.view_seprate_5)
    View seprateViewPayType;
    /**
     * 订单商品列表
     */
    @ViewInject(R.id.product_list_view)
    PullRefreshListView mProdutListView;
    /**
     * 商品数量
     */
    @ViewInject(R.id.tv_search_product_number)
    TextView mtvProductNumber;

    /**
     * 商品结果
     */
    @ViewInject(R.id.ll_product_detail_result)
    LinearLayout mllDetailResult;
    /**
     * 商品列表
     */
    @ViewInject(R.id.ll_product_list)
    LinearLayout mllProduct;
    /**
     * 付款按钮
     */
    @ViewInject(R.id.btn_cash)
    Button mbtnCash;

    SafeHandler safeHandler = new SafeHandler(OrderDetailActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DETAIL_SEARCH_SUCCESS:
                    /** 订单详情获取成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    showData();
                    break;
                case MSG_DETAIL_SEARCH_FAILURE:
                    /** 订单详情获取失败 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(OrderDetailActivity.this, errorMessage);
                    } else {
                        ToastUtils.showToast(OrderDetailActivity.this, getString(R.string.reg_httpclient_fail));
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
        setContentView(R.layout.order_detail);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle(R.string.order_detail_title);
    }

    private void initView() {
        status = getIntent().getIntExtra(ORDER_STATUS, 1);
        orderId = getIntent().getStringExtra(ORDER_ID);
        switch (status) {
            case 1:
                /** 未付款 */
                mrlPayType.setVisibility(View.GONE);
                seprateViewPayType.setVisibility(View.GONE);
                mrlPaid.setVisibility(View.GONE);
                mrlReceiverable.setVisibility(View.VISIBLE);
                mbtnCash.setVisibility(View.VISIBLE);
                break;
            default:
                /** 交易成功 */
                mrlPayType.setVisibility(View.VISIBLE);
                seprateViewPayType.setVisibility(View.VISIBLE);
                mrlPaid.setVisibility(View.VISIBLE);
                mrlReceiverable.setVisibility(View.GONE);
                mbtnCash.setVisibility(View.GONE);
                break;
        }
        mLoadingDialog = DialogUtils.getLoadingDialog(OrderDetailActivity.this, "正在查询订单详情信息...");
        mAdapter = new OrderDetailProductListAdapter(OrderDetailActivity.this, storageDetailInfoList);
        mProdutListView.setAdapter(mAdapter);
        mProdutListView.setCanRefresh(false);
        mProdutListView.setCanLoadMore(false);
        if (mLoadingDialog != null) {
            mLoadingDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(searchOrderDetailRunnable);
    }

    private void showData() {
        if (orderDetailInfo != null) {
            mtvOrderNumber.setText(orderDetailInfo.getOrderId());
            mtvOrderMoney.setText("￥" + decimalFormat.format(orderDetailInfo.getOrderMoney()));
            mtvCoupons.setText(orderDetailInfo.getCouponsKey());
            mtvDetailTelephone.setText(orderDetailInfo.getMemberTelphone());
            String payType = "";
            switch (orderDetailInfo.getPayChannelId()) {
                case 0:
                    payType = "现金";
                    break;
                case 1:
                    payType = "微信";
                    break;
                case 2:
                    payType = "支付宝";
                    break;
            }
            mtvPayType.setText(payType);
            if (!TextUtils.isEmpty(orderDetailInfo.getCouponsDiscountDescription())) {
                mtvCoupons.setText(orderDetailInfo.getCouponsDiscountDescription());
            }
            mtvReceivableMoney.setText("￥" + decimalFormat.format(orderDetailInfo.getShouldReceiveMoney()));
            mtvPaidMoney.setText("￥" + decimalFormat.format(orderDetailInfo.getRealReceiveMoney()));
            List<StorageDetailInfo> storageDetailInfos = orderDetailInfo.getStorageDetailInfoList();
            if (storageDetailInfos != null) {
                if (storageDetailInfos.size() > 0) {
                    mllDetailResult.setVisibility(View.VISIBLE);
                    mllProduct.setVisibility(View.VISIBLE);
                } else {
                    mllDetailResult.setVisibility(View.VISIBLE);
                    mllProduct.setVisibility(View.GONE);
                }
                mtvProductNumber.setText(String.valueOf(storageDetailInfos.size()));
                mAdapter.getStorageDetailInfos().clear();
                mAdapter.getStorageDetailInfos().addAll(storageDetailInfos);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    Runnable searchOrderDetailRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("order_id", orderId);
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
                        orderDetailInfo = new OrderDetailInfo();
                        orderDetailInfo.setOrderId(dataJSONObject.optString("order_id"));
                        if (!dataJSONObject.isNull("order_money")) {
                            orderDetailInfo.setOrderMoney(Float.parseFloat(dataJSONObject.optString("order_money")));
                        }
                        orderDetailInfo.setPayChannelId(dataJSONObject.optInt("pay_channel_id"));
                        orderDetailInfo.setMemberTelphone(dataJSONObject.optString("member_telephone"));
                        orderDetailInfo.setCouponsTitle(dataJSONObject.optString("coupon_title"));
                        orderDetailInfo.setCouponsKey(dataJSONObject.optString("coupon_key"));
                        orderDetailInfo.setCouponsDiscountDescription(dataJSONObject.optString("coupon_discount_string"));
                        if (!dataJSONObject.isNull("coupons_money")) {
                            orderDetailInfo.setCouponsMoney(Float.parseFloat(dataJSONObject.optString("coupons_money")));
                        }
                        if (!dataJSONObject.isNull("should_receive_money")) {
                            orderDetailInfo.setShouldReceiveMoney(Float.parseFloat(dataJSONObject.optString("should_receive_money")));
                        }
                        if (!dataJSONObject.isNull("real_receive_money")) {
                            orderDetailInfo.setRealReceiveMoney(Float.parseFloat(dataJSONObject.optString("real_receive_money")));
                        }
                        orderDetailInfo.setPayStatus(dataJSONObject.optInt("PayStatus"));
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

    @OnClick(R.id.btn_cash)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cash:
                /** 收银 */
                Intent intent = new Intent(OrderDetailActivity.this, PayOrderActivity.class);
                intent.putExtra(PayOrderActivity.ORDER_DETAIL_INFO, orderDetailInfo);
                startActivity(intent);
                finish();
                break;
        }
    }

}
