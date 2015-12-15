package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.entity.OrderFlowInfo;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.*;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 订单流水
 */
public class OrderFlowActivity extends BaseActivity {

    private LeftBackRightTextTitleBar titleBar;
    private Dialog dateChooseDialog;
    private Dialog orderFlowLoadingDialog;
    private int mcurrentDateType;
    OrderFlowInfo orderFlowInfo;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private final static int MSG_ORDER_FLOW_SUCCESS = 100;
    private final static int MSG_ORDER_FLOW_FAILURE = 101;
    /**
     * 全部订单数
     */
    @ViewInject(R.id.tv_all_order_number)
    TextView mtvAllOrderNumber;
    /**
     * 已完成订单数
     */
    @ViewInject(R.id.tv_finished_all_order_number)
    TextView mtvFinishedNumber;
    /**
     * 未成交订单数
     */
    @ViewInject(R.id.tv_not_finished_all_order_number)
    TextView mtvNotFinishedNumber;
    /**
     * 指定的日期
     */
    @ViewInject(R.id.tv_all_receipts_date)
    TextView mtvAllReceiptsDate;
    /**
     * 指定日期内的总收入
     */
    @ViewInject(R.id.tv_all_receipts_money)
    TextView mtvAllReceiptsMoney;
    /**
     * 微信收入
     */
    @ViewInject(R.id.tv_weixin)
    TextView mtvWeixin;
    /**
     * 支付宝收入
     */
    @ViewInject(R.id.tv_zhifubao)
    TextView mtvZhifubao;
    /**
     * 现金收入
     */
    @ViewInject(R.id.tv_cash_receipts)
    TextView mtvCashReceipts;
    /**
     * 订单日期选择
     */
    @ViewInject(R.id.tv_choose)
    TextView mtvDateChoose;


    SafeHandler safeHandler = new SafeHandler(OrderFlowActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ORDER_FLOW_SUCCESS:
                    if (orderFlowLoadingDialog != null) {
                        orderFlowLoadingDialog.dismiss();
                    }
                    orderFlowInfo = (OrderFlowInfo) msg.obj;
                    showOrderFlowInfoData(orderFlowInfo);
                    break;
                case MSG_ORDER_FLOW_FAILURE:
                    if (orderFlowLoadingDialog != null) {
                        orderFlowLoadingDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(OrderFlowActivity.this, errorMessage);
                    } else {
                        ToastUtils.showToast(OrderFlowActivity.this, getString(R.string.reg_httpclient_fail));
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
        setContentView(R.layout.order_flow);
        titleBar.onPostActivityLayout();
        titleBar.setTitle(R.string.order_flow_title);
        titleBar.setRightText(R.string.order_search);
        titleBar.setOnRightClickListener(searchOrderListener);
        titleBar.showRightButton();
    }

    private void initView() {
        orderFlowLoadingDialog = DialogUtils.getLoadingDialog(OrderFlowActivity.this, "订单流水查询...");
        orderFlowLoadingDialog.show();
        ClinkWorldApplication.httpHelper.execute(orderFlowRunnable);
    }

    View.OnClickListener searchOrderListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent searchIntent = new Intent(OrderFlowActivity.this, OrderSearchActivity.class);
            startActivity(searchIntent);
        }
    };

    private void showOrderFlowInfoData(OrderFlowInfo orderFlowInfo) {
        mtvAllOrderNumber.setText(String.valueOf(orderFlowInfo.getOrderAllNumber()) + "笔");
        mtvFinishedNumber.setText(String.valueOf(orderFlowInfo.getOrderFinishNumber()) + "笔");
        mtvNotFinishedNumber.setText(String.valueOf(orderFlowInfo.getOrderUnfinishNumber()) + "笔");
        mtvAllReceiptsMoney.setText("￥" + decimalFormat.format(orderFlowInfo.getMoney()));
        mtvWeixin.setText("￥" + decimalFormat.format(orderFlowInfo.getWeixinMoney()));
        mtvZhifubao.setText("￥" + decimalFormat.format(orderFlowInfo.getZhifubaoMoney()));
        mtvCashReceipts.setText("￥" + decimalFormat.format(orderFlowInfo.getCashMoney()));
    }

    /**
     * 选择指定的日期查询订单
     */
    public void chooseDate() {
        if (dateChooseDialog == null) {
            dateChooseDialog = new Dialog(this, R.style.BackgroundTranslateDialog);

            WindowManager.LayoutParams layoutParams = dateChooseDialog.getWindow().getAttributes();
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.y = UiUtils.dp2px(this, 80);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            dateChooseDialog.onWindowAttributesChanged(layoutParams);
            dateChooseDialog.setCancelable(true);
            dateChooseDialog.setCanceledOnTouchOutside(true);

            View chooseView = LayoutInflater.from(this).inflate(R.layout.date_choose_window, null);
            LinearLayout mllDateChoose = (LinearLayout) chooseView.findViewById(R.id.ll_date_choose);

            final String[] dateChoose = getResources().getStringArray(R.array.order_date_choose);
            for (int i = 0; i < dateChoose.length; i++) {
                final int timeType = i;
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UiUtils.dp2px(this, 45));
                TextView tv = new TextView(this);
                final String dateStr = dateChoose[i];
                tv.setText(dateStr);
                tv.setTextColor(Color.parseColor("#ffffff"));
                tv.setTextSize(18);
                tv.setSingleLine(true);
                tv.setGravity(Gravity.CENTER);
                mllDateChoose.addView(tv, p);

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dateChooseDialog != null) {
                            dateChooseDialog.dismiss();
                        }
                        mtvDateChoose.setText(dateStr);
                        mcurrentDateType = timeType;
                        mtvAllReceiptsDate.setText(getString(R.string.order_flow_all_receipts, dateStr));
                        if (orderFlowLoadingDialog != null) {
                            orderFlowLoadingDialog.show();
                        }
                        ClinkWorldApplication.httpHelper.execute(orderFlowRunnable);
                    }
                });
            }
            dateChooseDialog.setContentView(chooseView);
        }
        dateChooseDialog.show();
    }

    Runnable orderFlowRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            switch (mcurrentDateType) {
                case 0:
                    params.put("timetype", "1");
                    break;
                case 1:
                    params.put("timetype", "2");
                    break;
                case 2:
                    params.put("timetype", "3");
                    break;
                case 3:
                    params.put("timetype", "");
                    break;
            }
            String url = ServerUrl.BASE_URL + ServerUrl.ORDER_FLOW_PATH;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_ORDER_FLOW_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_ORDER_FLOW_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        OrderFlowInfo orderFlowInfo = new OrderFlowInfo();
                        orderFlowInfo.setOrderType(dataJSONObject.optInt("order_type"));
                        orderFlowInfo.setOrderAllNumber(dataJSONObject.optInt("all_order_num"));
                        orderFlowInfo.setOrderFinishNumber(dataJSONObject.optInt("finish_order_num"));
                        orderFlowInfo.setOrderUnfinishNumber(dataJSONObject.optInt("unfinish_order_num"));
                        orderFlowInfo.setMoney(Float.parseFloat(dataJSONObject.optString("money")));
                        orderFlowInfo.setCashMoney(Float.parseFloat(dataJSONObject.optString("cash_money")));
                        orderFlowInfo.setWeixinMoney(Float.parseFloat(dataJSONObject.optString("weixin_money")));
                        orderFlowInfo.setZhifubaoMoney(Float.parseFloat(dataJSONObject.optString("zhifubao_money")));
                        message.obj = orderFlowInfo;
                    } else {
                        message.what = MSG_ORDER_FLOW_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_ORDER_FLOW_FAILURE);
                }
            }
        }
    };


    @OnClick({R.id.tv_choose, R.id.ll_all_order, R.id.ll_finished_order, R.id.ll_not_finished_order, R.id.ll_cash_income, R.id.ll_weixin_income, R.id.ll_zhifubao_income})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_choose:
                /** 日期选择 */
                chooseDate();
                break;
            case R.id.ll_cash_income:
                /** 现金收入详情 */
                Intent intentCash = new Intent(OrderFlowActivity.this, OrderIncomeActivity.class);
                intentCash.putExtra(OrderIncomeActivity.INCOME_TYPE, 3);
                intentCash.putExtra(OrderIncomeActivity.ORDER_TIME, mcurrentDateType);
                if (orderFlowInfo != null) {
                    intentCash.putExtra(OrderIncomeActivity.INCOME_MONEY, orderFlowInfo.getCashMoney());
                }
                startActivity(intentCash);
                break;
            case R.id.ll_weixin_income:
                /** 微信收入详情 */
                Intent intenWeixin = new Intent(OrderFlowActivity.this, OrderIncomeActivity.class);
                intenWeixin.putExtra(OrderIncomeActivity.INCOME_TYPE, 4);
                intenWeixin.putExtra(OrderIncomeActivity.ORDER_TIME, mcurrentDateType);
                if (orderFlowInfo != null) {
                    intenWeixin.putExtra(OrderIncomeActivity.INCOME_MONEY, orderFlowInfo.getWeixinMoney());
                }
                startActivity(intenWeixin);
                break;
            case R.id.ll_zhifubao_income:
                /** 支付宝收入详情 */
                Intent intentZhifubao = new Intent(OrderFlowActivity.this, OrderIncomeActivity.class);
                intentZhifubao.putExtra(OrderIncomeActivity.INCOME_TYPE, 5);
                intentZhifubao.putExtra(OrderIncomeActivity.ORDER_TIME, mcurrentDateType);
                if (orderFlowInfo != null) {
                    intentZhifubao.putExtra(OrderIncomeActivity.INCOME_MONEY, orderFlowInfo.getZhifubaoMoney());
                }
                startActivity(intentZhifubao);
                break;
            case R.id.ll_all_order:
                /** 全部订单 */
                Intent intentAll = new Intent(OrderFlowActivity.this, OrderListActivity.class);
                intentAll.putExtra(OrderListActivity.ORDER_STATUS, 0);
                intentAll.putExtra(OrderListActivity.ORDER_TIME, mcurrentDateType);
                startActivity(intentAll);
                break;
            case R.id.ll_finished_order:
                /** 已完成订单 */
                Intent intentComplete = new Intent(OrderFlowActivity.this, OrderListActivity.class);
                intentComplete.putExtra(OrderListActivity.ORDER_STATUS, 1);
                intentComplete.putExtra(OrderListActivity.ORDER_TIME, mcurrentDateType);
                startActivity(intentComplete);
                break;
            case R.id.ll_not_finished_order:
                /** 未完成订单 */
                Intent intentNotFinished = new Intent(OrderFlowActivity.this, OrderListActivity.class);
                intentNotFinished.putExtra(OrderListActivity.ORDER_STATUS, 2);
                intentNotFinished.putExtra(OrderListActivity.ORDER_TIME, mcurrentDateType);
                startActivity(intentNotFinished);
                break;

        }
    }
}
