package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.entity.OrderDetailInfo;
import com.clinkworld.pay.qrcode.CaptureActivity;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.*;
import com.clinkworld.pay.views.OrderPayTypeChooseDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by srh on 2015/11/2.
 * <p/>
 * 订单支付页面
 */
public class PayOrderActivity extends BaseActivity {

    private LeftBackRightTextTitleBar titleBar;
    private OrderPayTypeChooseDialog orderPayTypeChooseDialog;
    private Dialog mExitPOSDialog;
    private Dialog cashPayDialog;
    private Dialog captureQRcodeDialog;
    private OrderDetailInfo orderDetailInfo;
    private String weixinQRcode;
    private String zhifubaoQRcode;
    private Timer zhifubaoTimer;
    private Timer weixinTimer;
    private int payChannelType = -1;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public final static int MSG_LOGOUT_POS_SUCCESS = 1;
    public final static int MSG_LOGOUT_POS_FAILURE = 2;
    public final static int MSG_QRCODE_INFO_SUCCESS = 3;
    public final static int MSG_QRCODE_INFO_FAILURE = 4;
    public final static int MSG_PAY_INFO_SUCCESS = 5;
    public final static int MSG_PAY_INFO_FAILURE = 6;
    public final static int MSG_PAY_CASH_SUCCESS = 7;
    public final static int MSG_PAY_CASH_FAILURE = 8;
    public final static int MSG_PAY_CASH_TIMEOUT = 9;
    public final static int MSG_PAY_CAPTURE_SUCCESS = 10;
    public final static int MSG_PAY_CAPTURE_FAILURE = 11;
    public final static int MSG_PAY_CAPTURE_TIMEOUT = 12;
    public final static int QEQUEST_CODE_PAYMENT = 0;
    public final static String ORDER_DETAIL_INFO = "order_detail_info";
    public final static String ORDER_PAY_QRCODE_TYPE = "pay_type"; // 1 微信付款  2 支付宝付款
    public final static String ORDER_CAPTURE_QRCODE = "capture_qrcode";

    /**
     * 登录的POS机
     */
    @ViewInject(R.id.pos_number)
    TextView mtvPOSNumber;

    /**
     * 用户工号
     */
    @ViewInject(R.id.work_id)
    TextView mtvWorkId;

    /**
     * 收银员
     */
    @ViewInject(R.id.user_name)
    TextView mtvUserName;


    /**
     * 支付方式选择
     */
    @ViewInject(R.id.tv_choose_pay_type)
    TextView mtvChoosePayType;
    /**
     * 输入现金
     */
    @ViewInject(R.id.detail_receivable_money)
    EditText metInputCash;

    @ViewInject(R.id.tv_prompt_1)
    TextView mrlReceiverable;

    @ViewInject(R.id.rl_change)
    TextView mrlChange;

    /**
     * 二维码
     */
    @ViewInject(R.id.iv_qrcode)
    ImageView mivQrcode;
    /**
     * 下一步
     */
    @ViewInject(R.id.btn_next)
    Button btnNext;
    /**
     * 顾客扫描二维码入口
     */
    @ViewInject(R.id.tv_capture_qrcode)
    TextView mtvCaptrueQrcode;
    /**
     * 订单号
     */
    @ViewInject(R.id.detail_order_number)
    TextView mtvDetailOrderNumber;
    /**
     * 订单金额
     */
    @ViewInject(R.id.detail_order_money)
    TextView mtvDetailOrderMoney;
    /**
     * 会员手机号
     */
    @ViewInject(R.id.detail_telephone)
    TextView mtvDetailTelephone;
    /**
     * 优惠券
     */
    @ViewInject(R.id.detail_coupons)
    TextView mtvDetailCoupons;
    /**
     * 找零
     */
    @ViewInject(R.id.tv_change_money)
    TextView mtvChangeMoney;
    /**
     * 优惠券已抵金额
     */
    @ViewInject(R.id.detail_pay_type)
    TextView mtvCouponsMoney;

    SafeHandler safeHandler = new SafeHandler(PayOrderActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderPayTypeChooseDialog.DIALOG_ORDER_TYPE_CASH:
                    mivQrcode.setVisibility(View.GONE);
                    btnNext.setVisibility(View.VISIBLE);
                    mtvCaptrueQrcode.setVisibility(View.GONE);
                    mtvChoosePayType.setText("现金");
                    mtvChoosePayType.setTextColor(Color.parseColor("#626262"));
                    mrlReceiverable.setAlpha(1f);
                    mrlChange.setAlpha(1f);
                    metInputCash.setEnabled(true);
                    mtvChangeMoney.setText("");
                    metInputCash.setText("");
                    payChannelType = 0;
                    break;
                case OrderPayTypeChooseDialog.DIALOG_ORDER_TYPE_WEIXIN:
                    mivQrcode.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);
                    mtvChoosePayType.setText("微信");
                    mtvChoosePayType.setTextColor(Color.parseColor("#626262"));
                    mtvCaptrueQrcode.setVisibility(View.VISIBLE);
                    createQRImage(1);
                    mrlReceiverable.setAlpha(0.5f);
                    mrlChange.setAlpha(0.5f);
                    metInputCash.setEnabled(false);
                    mtvChangeMoney.setText("");
                    metInputCash.setText("");
                    payChannelType = 1;
                    break;
                case OrderPayTypeChooseDialog.DIALOG_ORDER_TYPE_ZHIFUBAO:
                    mivQrcode.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);
                    mtvCaptrueQrcode.setVisibility(View.VISIBLE);
                    mtvChoosePayType.setText("支付宝");
                    mtvChoosePayType.setTextColor(Color.parseColor("#626262"));
                    createQRImage(2);
                    mrlReceiverable.setAlpha(0.5f);
                    mrlChange.setAlpha(0.5f);
                    metInputCash.setEnabled(false);
                    mtvChangeMoney.setText("");
                    metInputCash.setText("");
                    payChannelType = 2;
                    break;
                case MSG_LOGOUT_POS_FAILURE:
                    if (mExitPOSDialog != null) {
                        mExitPOSDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(PayOrderActivity.this, "网络错误，请检查网络后重试");
                    } else {
                        ToastUtils.showToast(PayOrderActivity.this, errorMessage);
                    }
                    break;
                case MSG_LOGOUT_POS_SUCCESS:
                    if (mExitPOSDialog != null) {
                        mExitPOSDialog.dismiss();
                    }
                    ToastUtils.showToast(PayOrderActivity.this, "退出成功");
                    Intent intent = new Intent(PayOrderActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case MSG_QRCODE_INFO_SUCCESS:
                    createQRImage(msg.getData().getString("data"));
                    int channel_id = msg.getData().getInt("pay_channel");
                    switch (channel_id) {
                        case 1:
                            /** 微信 */
                            if (weixinTimer != null) {
                                weixinTimer.schedule(timerweixinTask, 2 * 1000, 1000);
                            }
                            break;
                        case 2:
                            /** 支付宝 */
                            if (zhifubaoTimer != null) {
                                zhifubaoTimer.schedule(timerzhifubaoTask, 2 * 1000, 1000);
                            }
                            break;
                    }
                    break;

                case MSG_QRCODE_INFO_FAILURE:
                    String qrcodeErrorMessage = (String) msg.obj;
                    if (TextUtils.isEmpty(qrcodeErrorMessage)) {
                        ToastUtils.showToast(PayOrderActivity.this, "网络错误，请检查网络后重试");
                    } else {
                        ToastUtils.showToast(PayOrderActivity.this, qrcodeErrorMessage);
                    }
                    break;
                case MSG_PAY_INFO_SUCCESS:
                    /** 轮询支付成功 */
                    OrderDetailInfo orderDetailInfo = (OrderDetailInfo) msg.obj;
                    Intent intent1 = new Intent(PayOrderActivity.this, PayOrderResultActivity.class);
                    intent1.putExtra(PayOrderResultActivity.ORDER_DETAIL_INFO, orderDetailInfo);
                    startActivity(intent1);
                    finish();
                    break;
                case MSG_PAY_INFO_FAILURE:
                    /** 轮询支付失败 */
                    break;
                case MSG_PAY_CASH_SUCCESS:
                    /** 现金支付成功 */
                    if (cashPayDialog != null) {
                        cashPayDialog.dismiss();
                    }
                    removeMessages(MSG_PAY_CASH_TIMEOUT);
                    OrderDetailInfo orderDetailInfoCash = (OrderDetailInfo) msg.obj;
                    Intent intent2 = new Intent(PayOrderActivity.this, PayOrderResultActivity.class);
                    intent2.putExtra(PayOrderResultActivity.ORDER_DETAIL_INFO, orderDetailInfoCash);
                    startActivity(intent2);
                    finish();
                    break;
                case MSG_PAY_CASH_FAILURE:
                    /** 现金支付失败 */
                    if (cashPayDialog != null) {
                        cashPayDialog.dismiss();
                    }
                    String cashErrorMessage = (String) msg.obj;
                    if (TextUtils.isEmpty(cashErrorMessage)) {
                        ToastUtils.showToast(PayOrderActivity.this, getString(R.string.reg_httpclient_fail));
                    } else {
                        ToastUtils.showToast(PayOrderActivity.this, cashErrorMessage);
                    }
                    removeMessages(MSG_PAY_CASH_TIMEOUT);
                    break;
                case MSG_PAY_CASH_TIMEOUT:
                    /** 现金支付超时 */
                    if (cashPayDialog != null) {
                        cashPayDialog.dismiss();
                    }
                    ToastUtils.showToast(PayOrderActivity.this, getString(R.string.reg_httpclient_fail));
                    break;
                case MSG_PAY_CAPTURE_SUCCESS:
                    /** 扫描顾客二维码支付成功 */
                    if (captureQRcodeDialog != null) {
                        captureQRcodeDialog.dismiss();
                    }
                    removeMessages(MSG_PAY_CAPTURE_TIMEOUT);
                    OrderDetailInfo orderDetailInfoCapture = (OrderDetailInfo) msg.obj;
                    Intent intentCapture = new Intent(PayOrderActivity.this, PayOrderResultActivity.class);
                    intentCapture.putExtra(PayOrderResultActivity.ORDER_DETAIL_INFO, orderDetailInfoCapture);
                    startActivity(intentCapture);
                    finish();
                    break;
                case MSG_PAY_CAPTURE_FAILURE:
                    /** 扫描顾客二维码支付失败 */
                    if (captureQRcodeDialog != null) {
                        captureQRcodeDialog.dismiss();
                    }
                    removeMessages(MSG_PAY_CAPTURE_TIMEOUT);
                    break;
                case MSG_PAY_CAPTURE_TIMEOUT:
                    /** 扫描顾客二维码支付超时 */
                    if (captureQRcodeDialog != null) {
                        captureQRcodeDialog.dismiss();
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
        setContentView(R.layout.order_pay);
        titleBar.onPostActivityLayout();
        titleBar.setTitle(R.string.title_cash_platform);
        titleBar.setRightText(R.string.quit);
        titleBar.showRightButton();
        titleBar.setOnRightClickListener(exitPOSListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timerweixinTask != null) {
            timerweixinTask.cancel();
            timerweixinTask = null;
        }
        if (timerzhifubaoTask != null) {
            timerzhifubaoTask.cancel();
            timerzhifubaoTask = null;
        }
        if (weixinTimer != null) {
            weixinTimer.cancel();
            weixinTimer = null;
        }
        if (zhifubaoTimer != null) {
            zhifubaoTimer.cancel();
            zhifubaoTimer = null;
        }
    }

    public void createQRImage(int channelType) {
        switch (channelType) {
            case 1:
                /** 微信 */
                if (!TextUtils.isEmpty(weixinQRcode)) {
                    createQRImage(weixinQRcode);
                } else {
                    ClinkWorldApplication.httpHelper.execute(new QRcodeInfoRunnable(channelType));
                }
                break;
            case 2:
                /** 支付宝 */
                if (!TextUtils.isEmpty(zhifubaoQRcode)) {
                    createQRImage(zhifubaoQRcode);
                } else {
                    ClinkWorldApplication.httpHelper.execute(new QRcodeInfoRunnable(channelType));
                }
                break;
        }
    }


    public void createQRImage(String encodingData) {
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            int QR_WIDTH = UiUtils.dp2px(PayOrderActivity.this, 160);
            int QR_HEIGHT = UiUtils.dp2px(PayOrderActivity.this, 160);
            BitMatrix bitMatrix = new QRCodeWriter().encode(encodingData, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);

            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap mQrcodeBitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            mQrcodeBitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            mivQrcode.setImageBitmap(mQrcodeBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener exitPOSListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mExitPOSDialog != null) {
                mExitPOSDialog.show();
            }
            ClinkWorldApplication.httpHelper.execute(exitPOSRunnalbe);
        }
    };


    private void initView() {
        if (ClinkWorldApplication.userDataInfo != null) {
            mtvPOSNumber.setText(getString(R.string.pos_number, ClinkWorldApplication.userDataInfo.getPOSId()));
            mtvWorkId.setText(getString(R.string.user_number, ClinkWorldApplication.userDataInfo.getWorkId()));
            mtvUserName.setText(getString(R.string.user_name, ClinkWorldApplication.userDataInfo.getTrueName()));
        }
        zhifubaoTimer = new Timer(true);
        weixinTimer = new Timer(true);
        orderDetailInfo = (OrderDetailInfo) getIntent().getSerializableExtra(ORDER_DETAIL_INFO);
        if (orderDetailInfo != null) {
            mtvDetailOrderNumber.setText(orderDetailInfo.getOrderId());
            mtvDetailOrderMoney.setText("￥" + decimalFormat.format(orderDetailInfo.getOrderMoney()));
            if (!TextUtils.isEmpty(orderDetailInfo.getMemberTelphone())) {
                mtvDetailTelephone.setText(orderDetailInfo.getMemberTelphone());
            }
            mtvDetailCoupons.setText(orderDetailInfo.getCouponsKey());
            mtvCouponsMoney.setText("￥" + decimalFormat.format(orderDetailInfo.getCouponsMoney()));
            mtvDetailCoupons.setText(orderDetailInfo.getCouponsKey());

            /** 添加优惠券优惠金额大于订单金额的情况处理 */
            if (orderDetailInfo.getCouponsMoney() > orderDetailInfo.getOrderMoney()) {
                mtvChangeMoney.setText("￥" + decimalFormat.format(0));
                metInputCash.setText(decimalFormat.format(0));
                metInputCash.setEnabled(false);
                mtvChoosePayType.setEnabled(false);
                mtvChoosePayType.setText("现金");
                mtvChoosePayType.setTextColor(Color.parseColor("#626262"));
                mrlReceiverable.setAlpha(1f);
                mrlChange.setAlpha(1f);
                payChannelType = 0;
            }

        }

        metInputCash.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    /** 金额输入完成 */
                    try {
                        float inputCashMoney = Float.parseFloat(metInputCash.getText().toString());
                        if (inputCashMoney != 0) {
                            mtvChangeMoney.setText("￥" + decimalFormat.format(inputCashMoney - orderDetailInfo.getOrderMoney() + orderDetailInfo.getCouponsMoney()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        orderPayTypeChooseDialog = new OrderPayTypeChooseDialog(PayOrderActivity.this, R.style.DownToUpSlideDialog, safeHandler);
        mExitPOSDialog = DialogUtils.getLoadingDialog(PayOrderActivity.this, "正在退出收银系统...");
        cashPayDialog = DialogUtils.getLoadingDialog(PayOrderActivity.this, "正在现金支付...");
        captureQRcodeDialog = DialogUtils.getLoadingDialog(PayOrderActivity.this, "正在付款码支付...");
    }

    TimerTask timerzhifubaoTask = new TimerTask() {

        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("channel_id", "2");
            String url = ServerUrl.BASE_URL + "/order/" + orderDetailInfo.getOrderId() + "/paystatus?";
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_PAY_INFO_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_PAY_INFO_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
                        orderDetailInfo.setOrderId(dataJSONObject.optString("OrderId"));
                        orderDetailInfo.setOrderMoney(Float.parseFloat(dataJSONObject.optString("TotalMoney")));
                        orderDetailInfo.setPayChannelId(dataJSONObject.optInt("PayChannel"));
                        orderDetailInfo.setMemberTelphone(dataJSONObject.optString("MemberTelephone"));
                        orderDetailInfo.setCouponsKey(dataJSONObject.optString("CouponsKey"));
                        orderDetailInfo.setPayStatus(dataJSONObject.optInt("PayStatus"));
                        message.obj = orderDetailInfo;
                    } else {
                        message.what = MSG_PAY_INFO_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_PAY_INFO_FAILURE);
                }
            }

        }
    };

    TimerTask timerweixinTask = new TimerTask() {

        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("channel_id", "1");
            String url = ServerUrl.BASE_URL + "/order/" + orderDetailInfo.getOrderId() + "/paystatus?";
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_PAY_INFO_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_PAY_INFO_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
                        orderDetailInfo.setOrderId(dataJSONObject.optString("OrderId"));
                        orderDetailInfo.setOrderMoney(Float.parseFloat(dataJSONObject.optString("TotalMoney")));
                        orderDetailInfo.setPayChannelId(dataJSONObject.optInt("PayChannel"));
                        orderDetailInfo.setMemberTelphone(dataJSONObject.optString("MemberTelephone"));
                        orderDetailInfo.setCouponsKey(dataJSONObject.optString("CouponsKey"));
                        orderDetailInfo.setPayStatus(dataJSONObject.optInt("PayStatus"));
                        message.obj = orderDetailInfo;
                    } else {
                        message.what = MSG_PAY_INFO_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_PAY_INFO_FAILURE);
                }
            }

        }
    };


    class QRcodeInfoRunnable implements Runnable {

        private int payChannel;

        public QRcodeInfoRunnable(int channelId) {
            this.payChannel = channelId;
        }


        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("member_phone", orderDetailInfo.getMemberTelphone());
            params.put("channel_id", String.valueOf(payChannel));
            String url = ServerUrl.BASE_URL + "/order/" + orderDetailInfo.getOrderId() + "/pay?";
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_QRCODE_INFO_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_QRCODE_INFO_SUCCESS;
                        Bundle bundle = new Bundle();
                        bundle.putInt("pay_channel", payChannel);
                        bundle.putString("data", resultJSONObject.optString("data"));
                        message.setData(bundle);
                        switch (payChannel) {
                            case 1:
                                weixinQRcode = resultJSONObject.optString("data");
                                break;
                            case 2:
                                zhifubaoQRcode = resultJSONObject.optString("data");
                                break;
                        }
                    } else {
                        message.what = MSG_QRCODE_INFO_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_QRCODE_INFO_FAILURE);
                }
            }
        }
    }

    Runnable exitPOSRunnalbe = new Runnable() {
        @Override
        public void run() {
            /** 退出系统 */
            Map<String, String> params = new HashMap<String, String>();
            String url = ServerUrl.BASE_URL + ServerUrl.LOGOUT_POS_PATH;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_LOGOUT_POS_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.getInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_LOGOUT_POS_SUCCESS;
                    } else {
                        message.what = MSG_LOGOUT_POS_FAILURE;
                        if (resultJSONObject.has("info")) {
                            message.obj = resultJSONObject.getString("info");
                        }
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    class capturePayRunnable implements Runnable {

        private int channelId;
        private String key;

        public capturePayRunnable(int paramChannelId, String paramKey) {
            this.channelId = paramChannelId;
            this.key = paramKey;
        }

        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("member_phone", orderDetailInfo.getMemberTelphone());
            params.put("channel_id", String.valueOf(channelId));
            params.put("Key", key);
            String url = ServerUrl.BASE_URL + "/order/" + orderDetailInfo.getOrderId() + "/qrcodepay?";
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_PAY_CAPTURE_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_PAY_CAPTURE_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
                        orderDetailInfo.setOrderId(dataJSONObject.optString("OrderId"));
                        orderDetailInfo.setOrderMoney(Float.parseFloat(dataJSONObject.optString("TotalMoney")));
                        orderDetailInfo.setPayChannelId(dataJSONObject.optInt("PayChannel"));
                        orderDetailInfo.setMemberTelphone(dataJSONObject.optString("MemberTelephone"));
                        orderDetailInfo.setCouponsKey(dataJSONObject.optString("CouponsKey"));
                        orderDetailInfo.setPayStatus(dataJSONObject.optInt("PayStatus"));
                        message.obj = orderDetailInfo;
                    } else {
                        message.what = MSG_PAY_CAPTURE_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_PAY_CAPTURE_FAILURE);
                }
            }
        }
    }

    Runnable cashPayRunnalbe = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("pay_type", "cash");
            params.put("money", metInputCash.getText().toString());
            String url = ServerUrl.BASE_URL + "/order/" + orderDetailInfo.getOrderId() + "/cashpay";
            String result = HttpClientC.post(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_PAY_CASH_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.getInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_PAY_CASH_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
                        orderDetailInfo.setOrderId(dataJSONObject.optString("OrderId"));
                        orderDetailInfo.setOrderMoney(Float.parseFloat(dataJSONObject.optString("TotalMoney")));
                        orderDetailInfo.setPayChannelId(dataJSONObject.optInt("PayChannel"));
                        orderDetailInfo.setMemberTelphone(dataJSONObject.optString("MemberTelephone"));
                        orderDetailInfo.setCouponsKey(dataJSONObject.optString("CouponsKey"));
                        orderDetailInfo.setPayStatus(dataJSONObject.optInt("PayStatus"));
                        orderDetailInfo.setShouldReceiveMoney(Float.parseFloat(dataJSONObject.optString("ShouldReceiveMoney")));
                        message.obj = orderDetailInfo;
                    } else {
                        message.what = MSG_PAY_CASH_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_PAY_CASH_FAILURE);
                }
            }
        }
    };


    private void payCash() {
        if (payChannelType == -1) {
            ToastUtils.showToast(PayOrderActivity.this, "请选择收款方式");
            return;
        }
        if (TextUtils.isEmpty(metInputCash.getText().toString())) {
            ToastUtils.showToast(PayOrderActivity.this, "请输入收款金额");
            return;
        }
        cashPayDialog.show();
        safeHandler.sendEmptyMessageDelayed(MSG_PAY_CASH_TIMEOUT, 10 * 1000);
        ClinkWorldApplication.httpHelper.execute(cashPayRunnalbe);
    }

    @OnClick({R.id.btn_next, R.id.tv_choose_pay_type, R.id.tv_no_cash, R.id.tv_capture_qrcode, R.id.ll_cash_money})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                /** 下一步 */
                payCash();
                break;
            case R.id.tv_choose_pay_type:
                /** 支付方式选择 */
                if (orderPayTypeChooseDialog != null) {
                    orderPayTypeChooseDialog.show();
                }
                break;
            case R.id.tv_no_cash:
                /** 暂不收款 */
                finish();
                break;
            case R.id.tv_capture_qrcode:
                /** 顾客扫描二维码 */
                Intent intentQRcode = new Intent(PayOrderActivity.this, CaptureActivity.class);
                intentQRcode.putExtra(CaptureActivity.QRCODE_TYPE, 2);
                intentQRcode.putExtra(CaptureActivity.PAY_TYPE_CHANNEL, payChannelType);
                startActivityForResult(intentQRcode, QEQUEST_CODE_PAYMENT);
                break;
            case R.id.ll_cash_money:
                /** 找零 */
                try {
                    float inputCashMoney = Float.parseFloat(metInputCash.getText().toString());
                    if (inputCashMoney != 0) {
                        mtvChangeMoney.setText("￥" + decimalFormat.format(inputCashMoney - orderDetailInfo.getOrderMoney() + orderDetailInfo.getCouponsMoney()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case QEQUEST_CODE_PAYMENT:
                if (resultCode == RESULT_OK) {
                    /** 扫描到付款码 */
                    int channelId = data.getIntExtra(ORDER_PAY_QRCODE_TYPE, 1);
                    String captureQrcode = data.getStringExtra(ORDER_CAPTURE_QRCODE);
                    if (captureQRcodeDialog != null) {
                        captureQRcodeDialog.show();
                    }
                    ClinkWorldApplication.httpHelper.execute(new capturePayRunnable(channelId, captureQrcode));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
