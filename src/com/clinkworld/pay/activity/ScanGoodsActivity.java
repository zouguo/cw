package com.clinkworld.pay.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.entity.OrderDetailInfo;
import com.clinkworld.pay.entity.ProductInfo;
import com.clinkworld.pay.qrcode.CaptureActivity;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.*;
import com.clinkworld.pay.views.SlideView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srh on 2015/10/19.
 * <p/>
 * 商品信息页面
 */
public class ScanGoodsActivity extends BaseActivity {

    public final static String TAG = "ScanGoodsActivity.this";
    public final static String BROADCAST_UPDATE_PRODUCT_ACTION = "com.clinkworld.pay.activity.ScanGoodsActivity.action.refreshProduct";
    public final static String UPDATE_PRODUCT_PERMISSION = "com.clinkworld.pay.permission.UPDATE_PRODUCT_BROADCAST";
    public final static String CAPTURE_QRCODE = "capture_qrcode";
    public final static int REQUEST_CODE_CAPTURE_QRCODE = 0;
    private LeftBackRightTextTitleBar titleBar;
    private Dialog mOrderPostDialog;
    private List<ProductInfo> productInfoList = new ArrayList<ProductInfo>();
    private DecimalFormat df = new DecimalFormat("0.00");
    public final static int MSG_ORDER_POST_SUCCESS = 0;
    public final static int MSG_ORDER_POST_FAILURE = 1;
    public final static int MSG_ORDER_POST_TIMEOUT = 2;
    public final static int MSG_COUPONS_AVABILITY_SUCCESS = 3;
    public final static int MSG_COUPONS_AVABILITY_FAILURE = 4;

    /**
     * 登录的POS机
     */
    @ViewInject(R.id.tv_pos_number)
    TextView mtvPOSNumber;

    /**
     * 用户工号
     */
    @ViewInject(R.id.tv_workid)
    TextView mtvWorkId;

    /**
     * 收银员
     */
    @ViewInject(R.id.tv_user_name)
    TextView mtvUserName;

    /**
     * 会员手机号输入
     */
    @ViewInject(R.id.et_input_vip_phone)
    EditText metInputVipPhone;

    /**
     * 优惠券号码
     */
    @ViewInject(R.id.et_input_coupons)
    EditText metInputCouponsNumber;

    /**
     * 扫描到的商品数量
     */
    @ViewInject(R.id.tv_number_product)
    TextView mtvProductNumber;

    /**
     * 总计花费
     */
    @ViewInject(R.id.et_all_cost)
    EditText metAllCost;

    /**
     * 抵扣后花费
     */
    @ViewInject(R.id.tv_deductible_cost)
    TextView mtvDeductibleCost;

    /**
     * 无添加商品的提示
     */
    @ViewInject(R.id.ll_has_no_product)
    LinearLayout mllHasNoProduct;

    /**
     * 扫描的商品列表布局
     */
    @ViewInject(R.id.ll_capture_product)
    LinearLayout mllCaptureProduct;

    SafeHandler safeHandler = new SafeHandler(ScanGoodsActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ORDER_POST_SUCCESS:
                    /** 订单提交成功 */
                    if (mOrderPostDialog != null) {
                        mOrderPostDialog.dismiss();
                    }
                    removeMessages(MSG_ORDER_POST_TIMEOUT);
                    OrderDetailInfo orderDetailInfo = (OrderDetailInfo) msg.obj;
                    Intent intent = new Intent(ScanGoodsActivity.this, PayOrderActivity.class);
                    intent.putExtra(PayOrderActivity.ORDER_DETAIL_INFO, orderDetailInfo);
                    startActivity(intent);
                    break;
                case MSG_ORDER_POST_FAILURE:
                    /** 订单提交失败 */
                    if (mOrderPostDialog != null) {
                        mOrderPostDialog.dismiss();
                    }
                    removeMessages(MSG_ORDER_POST_TIMEOUT);
                    String errorMessage = (String) msg.obj;
                    if (TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(ScanGoodsActivity.this, getString(R.string.reg_httpclient_fail));
                    } else {
                        ToastUtils.showToast(ScanGoodsActivity.this, errorMessage);
                    }
                    break;
                case MSG_ORDER_POST_TIMEOUT:
                    /** 超时 */
                    if (mOrderPostDialog != null) {
                        mOrderPostDialog.dismiss();
                    }
                    ToastUtils.showToast(ScanGoodsActivity.this, getString(R.string.reg_httpclient_fail));
                    break;
                case MSG_COUPONS_AVABILITY_SUCCESS:
                    String couponsCount = (String) msg.obj;
                    mtvDeductibleCost.setText(couponsCount);
                    ToastUtils.showToast(ScanGoodsActivity.this, "有效优惠券");
                    break;
                case MSG_COUPONS_AVABILITY_FAILURE:
                    String couponsErrorMessage = (String) msg.obj;
                    metInputCouponsNumber.setText("");
                    if (!TextUtils.isEmpty(couponsErrorMessage)) {
                        ToastUtils.showToast(ScanGoodsActivity.this, couponsErrorMessage);
                    }
                    break;
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProductInfo productInfo = (ProductInfo) intent.getSerializableExtra("addProduct");
            boolean hasSameProduct = false;
            for (ProductInfo item : productInfoList) {
                if (item.getProductBarCode().equals(productInfo.getProductBarCode())) {
                    item.setNumber(item.getNumber() + productInfo.getNumber());
                    hasSameProduct = true;
                    break;
                }
            }
            if (!hasSameProduct) {
                productInfoList.add(productInfo);
            }
            refreshProductList(productInfoList);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_UPDATE_PRODUCT_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter, UPDATE_PRODUCT_PERMISSION, new Handler());
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.scan_goods);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle(R.string.title_cash_platform);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void initView() {
        mtvPOSNumber.setText(getString(R.string.pos_number, ClinkWorldApplication.userDataInfo.getPOSId()));
        mtvWorkId.setText(getString(R.string.user_number, ClinkWorldApplication.userDataInfo.getWorkId()));
        mtvUserName.setText(getString(R.string.user_name, ClinkWorldApplication.userDataInfo.getTrueName()));
        mOrderPostDialog = DialogUtils.getLoadingDialog(ScanGoodsActivity.this, "正在提交订单...");

        metInputCouponsNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCouponsAvaiblity();
                }
                return false;
            }
        });
    }

    private void searchCouponsAvaiblity() {
        if (TextUtils.isEmpty(metInputCouponsNumber.getText().toString())) {
            ToastUtils.showToast(ScanGoodsActivity.this, "请输入优惠券号码");
            return;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(ScanGoodsActivity.this.getCurrentFocus()
                                .getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        if (TextUtils.isEmpty(metAllCost.getText().toString())) {
            ToastUtils.showToast(ScanGoodsActivity.this, "请输入订单金额");
            return;
        }
        if (TextUtils.isEmpty(metInputVipPhone.getText().toString())) {
            ToastUtils.showToast(ScanGoodsActivity.this, "请输入会员手机号");
            return;
        }
        ClinkWorldApplication.httpHelper.execute(new CheckCouponsAvailablityRunnable(metInputCouponsNumber.getText().toString(), Float.parseFloat(metAllCost.getText().toString()), metInputVipPhone.getText().toString()));
    }

    private void refreshProductList(final List<ProductInfo> productInfos) {
        if (productInfos != null) {
            mllCaptureProduct.removeAllViews();
            if (productInfos.size() == 0) {
                mtvProductNumber.setText("0");
                metAllCost.setText("");
                mllHasNoProduct.setVisibility(View.VISIBLE);
                mllCaptureProduct.setVisibility(View.GONE);
                return;
            }
            mllHasNoProduct.setVisibility(View.GONE);
            mllCaptureProduct.setVisibility(View.VISIBLE);
            int totalNumber = 0;
            float totalCostAll = 0.0f;
            for (int i = 0; i < productInfos.size(); i++) {
                final int position = i;
                View view = LayoutInflater.from(this).inflate(R.layout.capture_product_item, null);
                TextView mtvProductBarCode = (TextView) view.findViewById(R.id.item_product_barcode);
                TextView mtvProductName = (TextView) view.findViewById(R.id.item_product_name);
                TextView mtvProductPrice = (TextView) view.findViewById(R.id.item_product_price);
                TextView mtvProductNumeber = (TextView) view.findViewById(R.id.item_product_number);
                TextView mtvProductCost = (TextView) view.findViewById(R.id.item_product_cost);
                Button mbtnReduce = (Button) view.findViewById(R.id.cash_item_btn_reduce);
                Button mbtnAdd = (Button) view.findViewById(R.id.cash_item_btn_add);

                final ProductInfo productInfo = productInfos.get(i);

                mtvProductBarCode.setText(productInfo.getProductBarCode());
                mtvProductName.setText(productInfo.getName());
                mtvProductPrice.setText(df.format(productInfo.getPrice()));
                mtvProductNumeber.setText(String.valueOf(productInfo.getNumber()));
                float cost = productInfo.getNumber() * productInfo.getPrice();
                mtvProductCost.setText("￥" + df.format(productInfo.getNumber() * productInfo.getPrice()));

                totalNumber += productInfo.getNumber();
                totalCostAll += cost;

                mbtnReduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < productInfos.size(); i++) {
                            if (i == position) {
                                int number = productInfos.get(i).getNumber();
                                if (number > 1) {
                                    productInfos.get(i).setNumber(number - 1);
                                }
                                break;
                            }
                        }
                        refreshProductList(productInfos);
                    }
                });

                mbtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < productInfos.size(); i++) {
                            if (i == position) {
                                int number = productInfos.get(i).getNumber();
                                productInfos.get(i).setNumber(number + 1);
                                break;
                            }
                        }
                        refreshProductList(productInfos);
                    }
                });

                SlideView slideView = new SlideView(this);
                slideView.setContentView(view);
                slideView.setmBtnDeleteListener(new SlideView.OnBtnDeleteListener() {
                    @Override
                    public void onClick() {
                        /** 删除当前条目 */
                        productInfos.remove(position);
                        refreshProductList(productInfos);
                    }
                });

                slideView.setOnSlideListener(new SlideView.OnSlideListener() {
                    @Override
                    public void onSlide(View view, int status) {
                        if (status == SLIDE_STATUS_START_SCROLL) {
                            view.setBackgroundColor(Color.parseColor("#F6F6F6"));
                        } else if (status == SLIDE_STATUS_OFF) {
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                });

                mllCaptureProduct.addView(slideView);
                if (i != productInfos.size() - 1) {
                    View separateView = new View(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UiUtils.dp2px(this, 1));
                    separateView.setBackgroundResource(R.drawable.dotted_line);
                    mllCaptureProduct.addView(separateView, params);
                }
            }
            mtvProductNumber.setText(String.valueOf(totalNumber));
            metAllCost.setText(df.format(totalCostAll));
            if (!TextUtils.isEmpty(metInputCouponsNumber.getText().toString()) && !TextUtils.isEmpty(metInputVipPhone.getText().toString()) && !TextUtils.isEmpty(metAllCost.getText().toString())) {
                ClinkWorldApplication.httpHelper.execute(new CheckCouponsAvailablityRunnable(metInputCouponsNumber.getText().toString(), Float.parseFloat(metAllCost.getText().toString()), metInputVipPhone.getText().toString()));
            }
        }
    }

    class CheckCouponsAvailablityRunnable implements Runnable {

        private String couponsNumber;
        private float money;
        private String memberPhone;

        public CheckCouponsAvailablityRunnable(String couponsNumber, float money, String memberPhone) {
            this.couponsNumber = couponsNumber;
            this.money = money;
            this.memberPhone = memberPhone;
        }

        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("coupon_no", couponsNumber);
            params.put("money", String.valueOf(money));
            params.put("member_id", memberPhone);
            String url = ServerUrl.BASE_URL + ServerUrl.COUPONS_AVAILBITLY;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_COUPONS_AVABILITY_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_COUPONS_AVABILITY_SUCCESS;
                        message.obj = resultJSONObject.optString("data");
                    } else {
                        message.what = MSG_COUPONS_AVABILITY_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_COUPONS_AVABILITY_FAILURE);
                }
            }
        }
    }

    Runnable orderPostRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            for (int i = 0; i < productInfoList.size(); i++) {
                params.put("products[" + i + "][barcode]", productInfoList.get(i).getProductBarCode());
                params.put("products[" + i + "][num]", String.valueOf(productInfoList.get(i).getNumber()));
            }
            params.put("money", metAllCost.getText().toString());
            params.put("telephone", metInputVipPhone.getText().toString());
            params.put("coupon_code", metInputCouponsNumber.getText().toString());
            String url = ServerUrl.BASE_URL + ServerUrl.ORDER_POST_PATH;
            String result = HttpClientC.post(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_ORDER_POST_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.getInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_ORDER_POST_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
                        orderDetailInfo.setOrderId(dataJSONObject.optString("OrderId"));
                        orderDetailInfo.setOrderMoney(Float.parseFloat(dataJSONObject.optString("TotalMoney")));
                        orderDetailInfo.setPayChannelId(dataJSONObject.optInt("PayChannel"));
                        orderDetailInfo.setMemberTelphone(dataJSONObject.optString("MemberTelephone"));
                        orderDetailInfo.setCouponsKey(dataJSONObject.optString("CouponKey"));
                        orderDetailInfo.setPayStatus(dataJSONObject.optInt("PayStatus"));
                        if (!dataJSONObject.isNull("CouponMoney")) {
                            orderDetailInfo.setCouponsMoney(Float.parseFloat(dataJSONObject.optString("CouponMoney")));
                        }
                        orderDetailInfo.setShouldReceiveMoney(Float.parseFloat(dataJSONObject.optString("ShouldReceiveMoney")));
                        message.obj = orderDetailInfo;
                    } else {
                        message.what = MSG_ORDER_POST_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_ORDER_POST_FAILURE);
                }
            }
        }
    };

    private void cash() {
        if (TextUtils.isEmpty(metAllCost.getText().toString())) {
            ToastUtils.showToast(ScanGoodsActivity.this, "输入收款金额");
            return;
        }
        if (!TextUtils.isEmpty(metInputVipPhone.getText().toString())) {
            MobileUtil mobileUtil = new MobileUtil(metInputVipPhone.getText().toString().trim());
            if (!mobileUtil.isMobile()) {
                ToastUtils.showToast(ScanGoodsActivity.this, "请输入正确的会员手机号");
                return;
            }
        }
        mOrderPostDialog.show();
        ClinkWorldApplication.httpHelper.execute(orderPostRunnable);
    }


    /**
     * 重置数据
     */
    private void resetCaptureProductData() {
        mtvProductNumber.setText("0");
        metAllCost.setText("");
        mllHasNoProduct.setVisibility(View.VISIBLE);
        mllCaptureProduct.setVisibility(View.GONE);
        productInfoList.clear();
        metInputVipPhone.setText("");
        metInputCouponsNumber.setText("");
    }


    @OnClick({R.id.iv_capture, R.id.tv_cash, R.id.tv_reset, R.id.ll_capture_qrcode})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_capture:
                /** 商品扫描 */
                Intent intent = new Intent(instance, CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_cash:
                /** 收款 */
                cash();
                break;
            case R.id.tv_reset:
                /** 重置 */
                resetCaptureProductData();
                break;
            case R.id.ll_capture_qrcode:
                /** 扫描优惠券二维码 */
                Intent qrcodeCaptureIntent = new Intent(ScanGoodsActivity.this, CaptureActivity.class);
                qrcodeCaptureIntent.putExtra(CaptureActivity.QRCODE_TYPE, 4);
                startActivityForResult(qrcodeCaptureIntent, REQUEST_CODE_CAPTURE_QRCODE);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_QRCODE:
                if (resultCode == Activity.RESULT_OK) {
                    String barcode = data.getStringExtra(CAPTURE_QRCODE);
                    metInputCouponsNumber.setText(barcode);
                    searchCouponsAvaiblity();
                }
                break;
        }
    }
}
