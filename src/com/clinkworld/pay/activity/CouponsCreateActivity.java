package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.titlebar.LeftTextRightTextTitleBar;
import com.clinkworld.pay.util.*;
import com.clinkworld.pay.views.CouponsNumberChooseDialog;
import com.clinkworld.pay.views.CouponsTypeChooseDialog;
import com.clinkworld.pay.views.InputNumberDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by srh on 2015/11/5.
 * <p/>
 * 创建优惠券
 */
public class CouponsCreateActivity extends BaseActivity {

    private int sendQuantityNumber;
    private int limitGetNumber;
    private String inputDiscount;
    private LeftTextRightTextTitleBar titleBar;
    private CouponsTypeChooseDialog couponsTypeChooseDialog;
    private CouponsNumberChooseDialog couponsNumberChooseDialog;
    private WindowManager OPwindow = null;//操作框
    private Dialog mLoadingDialog;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static int COUPONS_CREATE_SUCCESS = 55;
    public final static int COUPONS_CREATE_FAILURE = 56;

    /**
     * 发放量操作返回
     */
    private final static int COUPONS_NUMBER = 1;
    /**
     * 每人限领操作返回
     */
    private final static int COUPONS_GAIN = 2;
    /**
     * 优惠券的类型
     */
    private int type;
    /**
     * 优惠券名称
     */
    @ViewInject(R.id.et_input_coupons_name)
    EditText metInputCouponsName;
    /**
     * 优惠券类型选择
     */
    @ViewInject(R.id.et_coupons_type)
    EditText metCouponsTypeChoose;
    /**
     * 发放量
     */
    @ViewInject(R.id.et_input_send_quantity)
    EditText metInputSendQuantity;
    /**
     * 生效时间
     */
    private String beginTime;
    /**
     * 失效时间
     */
    private String endTime;
    /**
     * 优惠券生效时间
     */
    @ViewInject(R.id.et_choose_start_time)
    TextView metChooseStartTime;
    /**
     * 优惠券失效时间
     */
    @ViewInject(R.id.et_choose_end_time)
    TextView metChooseEndTime;
    /**
     * 礼券单笔交易满额
     */
    @ViewInject(R.id.et_input_full_transactiony)
    EditText metInputCouponsFullTransactione;
    /**
     * 交易满额￥
     */
    @ViewInject(R.id.tv_input_money_prompt)
    TextView mtvInputMoneyPrompt;
    /**
     * 面值输入￥
     */
    @ViewInject(R.id.tv_input_mianzhi_prompt)
    TextView mtvInputMainzhiPrompt;
    /**
     * 最多折扣￥
     */
    @ViewInject(R.id.tv_input_max_discount_prompt)
    TextView mtvInputMaxDiscountPrompt;

    /**
     * 输入的优惠券面值
     */
    @ViewInject(R.id.et_input_coupons_money)
    EditText metInputCouponsMoney;
    /**
     * 输入最多折扣金额
     */
    @ViewInject(R.id.et_input_coupons_max_discount)
    EditText metInputCouponsMaxDiscount;
    /**
     * 输入优惠券折扣
     */
    @ViewInject(R.id.et_input_coupons_discount)
    EditText metInputCouponsDiscount;

    /**
     * 折扣布局
     */
    @ViewInject(R.id.rl_coupons_discount)
    RelativeLayout mrlCouponsDiscount;

    @ViewInject(R.id.view_spreate_1)
    View spreate1;
    /**
     * 最多折扣金额
     */
    @ViewInject(R.id.rl_coupons_max_discount)
    RelativeLayout mrlCouponsMaxDiscount;

    @ViewInject(R.id.view_seprate_3)
    View spreate3;

    /**
     * 面值布局
     */
    @ViewInject(R.id.rl_coupons_money)
    RelativeLayout mrlCouponsMoney;


    @ViewInject(R.id.view_seprate_2)
    View spreate2;
    /**
     * 每人限领优惠券数量
     */
    @ViewInject(R.id.et_choose_limit_collar)
    EditText metChooseLimitCollar;

    SafeHandler handler = new SafeHandler(CouponsCreateActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 5:  // 选择时间
                    if (msg.arg1 == 0) {
                        beginTime = msg.obj.toString();
                        metChooseStartTime.setTextColor(Color.parseColor("#626262"));
                        if (!TextUtils.isEmpty(metChooseEndTime.getText().toString())) {
                            String startTime = metChooseStartTime.getText().toString();
                            String endTime = metChooseEndTime.getText().toString();

                            Calendar startCalendar = Calendar.getInstance();
                            Calendar endCalendar = Calendar.getInstance();
                            try {
                                startCalendar.setTime(format.parse(startTime));
                                endCalendar.setTime(format.parse(endTime));
                                int result = startCalendar.compareTo(endCalendar);
                                if (result > 0) {
                                    ToastUtils.showToast(CouponsCreateActivity.this, "截止日期不能早于开始日期");
                                    metChooseStartTime.setText(getString(R.string.coupons_create_choose));
                                    metChooseStartTime.setTextColor(Color.parseColor("#C4C4C4"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    } else {
                        endTime = msg.obj.toString();
                        metChooseEndTime.setTextColor(Color.parseColor("#626262"));
                        if (!TextUtils.isEmpty(metChooseStartTime.getText().toString())) {
                            String startTime = metChooseStartTime.getText().toString();
                            String endTime = metChooseEndTime.getText().toString();

                            Calendar startCalendar = Calendar.getInstance();
                            Calendar endCalendar = Calendar.getInstance();
                            try {
                                startCalendar.setTime(format.parse(startTime));
                                endCalendar.setTime(format.parse(endTime));
                                int result = startCalendar.compareTo(endCalendar);
                                if (result > 0) {
                                    ToastUtils.showToast(CouponsCreateActivity.this, "截止日期不能早于开始日期");
                                    metChooseEndTime.setText(getString(R.string.coupons_create_choose));
                                    metChooseEndTime.setTextColor(Color.parseColor("#C4C4C4"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case CouponsTypeChooseDialog.DIALOG_COUPONS_TYPE_MONEY:
                    /** 面值类型选择 */
                    metCouponsTypeChoose.setText(getString(R.string.coupons_create_dialog_money));
                    type = Integer.parseInt((String) msg.obj);
                    mrlCouponsDiscount.setVisibility(View.GONE);
                    spreate1.setVisibility(View.GONE);

                    mrlCouponsMaxDiscount.setVisibility(View.GONE);
                    spreate3.setVisibility(View.GONE);

                    mrlCouponsMoney.setVisibility(View.VISIBLE);
                    spreate2.setVisibility(View.VISIBLE);
                    break;
                case CouponsTypeChooseDialog.DIALOG_COUPONS_TYPE_DISCOUNT:
                    /** 折扣类型选择 */
                    metCouponsTypeChoose.setText(getString(R.string.coupons_create_dialog_discount));
                    type = Integer.parseInt((String) msg.obj);
                    mrlCouponsDiscount.setVisibility(View.VISIBLE);
                    spreate1.setVisibility(View.VISIBLE);

                    mrlCouponsMaxDiscount.setVisibility(View.VISIBLE);
                    spreate3.setVisibility(View.VISIBLE);

                    mrlCouponsMoney.setVisibility(View.GONE);
                    spreate2.setVisibility(View.GONE);

                    break;
                case InputNumberDialog.DIALOG_INPUT_CANCEL:
                    /** 自定义输入，取消按钮 */

                    break;
                case InputNumberDialog.DIALOG_INPUT_COMMIT:
                    /** 自定义输入，确认按钮 */
                    int type = Integer.parseInt((String) msg.obj);
                    switch (type) {
                        case COUPONS_NUMBER:
                            metInputSendQuantity.setText(msg.getData().getString(InputNumberDialog.INPUT_NUMBER));
                            sendQuantityNumber = 10;
                            break;
                        case COUPONS_GAIN:
                            metChooseLimitCollar.setText(msg.getData().getString(InputNumberDialog.INPUT_NUMBER));
                            limitGetNumber = 10;
                            break;
                    }
                    break;
                case CouponsNumberChooseDialog.DIALOG_COUPONS_NUMBER_DEFINE:
                    /** 自定义 */
                    int type1 = Integer.parseInt((String) msg.obj);
                    switch (type1) {
                        case COUPONS_NUMBER:
                            metInputSendQuantity.setText("10");
                            sendQuantityNumber = 10;
                            break;
                        case COUPONS_GAIN:
                            metChooseLimitCollar.setText("10");
                            limitGetNumber = 10;
                            break;
                    }
                    break;
                case CouponsNumberChooseDialog.DIALOG_COUPONS_NUMBER_NOT_LIMITED:
                    /** 不限 */
                    int type2 = Integer.parseInt((String) msg.obj);
                    switch (type2) {
                        case COUPONS_NUMBER:
                            metInputSendQuantity.setText(getString(R.string.coupons_choose_number_not_limited));
                            sendQuantityNumber = 0;
                            break;
                        case COUPONS_GAIN:
                            metChooseLimitCollar.setText(getString(R.string.coupons_choose_number_not_limited));
                            limitGetNumber = 0;
                            break;
                    }
                    break;
                case COUPONS_CREATE_SUCCESS:
                    /** 创建成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    ToastUtils.showToast(CouponsCreateActivity.this, "保存优惠券成功");
                    Intent intent = new Intent(CouponsListActivity.BROADCAST_UPDATE_COUPONS_ACTION);
                    sendBroadcast(intent, CouponsListActivity.UPDATE_COUPONS_PERMISSION);
                    finish();
                    break;
                case COUPONS_CREATE_FAILURE:
                    /** 创建失败 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    ToastUtils.showToast(CouponsCreateActivity.this, "保存优惠券失败");
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
        titleBar = new LeftTextRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.coupons_create);
        titleBar.onPostActivityLayout();
        titleBar.setTitle(R.string.coupons_create_title);
        titleBar.setRightText(R.string.save);
        titleBar.showRightButton();
        titleBar.setLeftText(R.string.cancel);
        titleBar.setOnRightClickListener(saveCouponsListener);
    }

    private void initView() {
        OPwindow = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        couponsTypeChooseDialog = new CouponsTypeChooseDialog(this, R.style.DownToUpSlideDialog, handler);
        couponsNumberChooseDialog = new CouponsNumberChooseDialog(this, R.style.DownToUpSlideDialog, handler);
        metInputCouponsMoney.addTextChangedListener(new InputCouponsMoneyWatcher(0));
        metInputCouponsFullTransactione.addTextChangedListener(new InputCouponsMoneyWatcher(1));
        metInputCouponsMaxDiscount.addTextChangedListener(new InputCouponsMoneyWatcher(2));
        metInputCouponsDiscount.addTextChangedListener(inputCouponsDiscountWatcher);
        mLoadingDialog = DialogUtils.getLoadingDialog(CouponsCreateActivity.this, "正在保存优惠券...");
    }

    class InputCouponsMoneyWatcher implements TextWatcher {

        private int type;

        public InputCouponsMoneyWatcher(int type) {
            this.type = type;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (type) {
                case 0:
                    if (s.length() > 0) {
                        char c = s.charAt(0);
                        if (c == '0') {
                            s.delete(0, 1);
                        }
                    }
                    if (!TextUtils.isEmpty(metInputCouponsMoney.getText().toString())) {
                        mtvInputMainzhiPrompt.setVisibility(View.VISIBLE);
                    } else {
                        mtvInputMainzhiPrompt.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    if (!TextUtils.isEmpty(metInputCouponsFullTransactione.getText().toString())) {
                        mtvInputMoneyPrompt.setVisibility(View.VISIBLE);
                    } else {
                        mtvInputMoneyPrompt.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    if (!TextUtils.isEmpty(metInputCouponsMaxDiscount.getText().toString())) {
                        mtvInputMaxDiscountPrompt.setVisibility(View.VISIBLE);
                    } else {
                        mtvInputMaxDiscountPrompt.setVisibility(View.GONE);
                    }
                    break;

            }
        }
    }

    TextWatcher inputCouponsDiscountWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            inputDiscount = metInputCouponsDiscount.getText().toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                char c = s.charAt(0);
                if (c == '0') {
                    s.delete(0, 1);
                }
            }
        }
    };


    View.OnClickListener saveCouponsListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (type == 0) {
                ToastUtils.showToast(CouponsCreateActivity.this, "请选择优惠券类型");
                return;
            }

            if (TextUtils.isEmpty(metInputCouponsName.getText().toString())) {
                ToastUtils.showToast(CouponsCreateActivity.this, "请输入优惠券名称");
                return;
            }

            /** 保存优惠券 */
            if (TextUtils.isEmpty(metChooseStartTime.getText().toString())) {
                ToastUtils.showToast(CouponsCreateActivity.this, "请选择优惠券生效时间");
                return;
            }
            if (TextUtils.isEmpty(metChooseEndTime.getText().toString())) {
                ToastUtils.showToast(CouponsCreateActivity.this, "请选择优惠券失效时间");
                return;
            }

            if (type == 1) {
                /** 折扣 */
                if (TextUtils.isEmpty(metInputCouponsDiscount.getText().toString())) {
                    ToastUtils.showToast(CouponsCreateActivity.this, "请输入折扣优惠券优惠折扣");
                    return;
                }
                if (!metInputCouponsDiscount.getText().toString().matches("^([1-9]|([1-9][.][0-9]))$")) {
                    ToastUtils.showToast(CouponsCreateActivity.this, "请输入正确的优惠折扣");
                    metInputCouponsDiscount.setText("");
                    return;
                }
                if (TextUtils.isEmpty(metInputCouponsDiscount.getText().toString())) {
                    ToastUtils.showToast(CouponsCreateActivity.this, "请输入最多折扣金额");
                    return;
                }
            } else if (type == 2) {
                /** 面值 */
                if (TextUtils.isEmpty(metInputCouponsMoney.getText().toString())) {
                    ToastUtils.showToast(CouponsCreateActivity.this, "请输入面值优惠券优惠面值金额");
                    return;
                }
            }

            if (TextUtils.isEmpty(metInputCouponsFullTransactione.getText().toString())) {
                ToastUtils.showToast(CouponsCreateActivity.this, "请输入可使用礼券的单笔交易满额");
                return;
            }

            if (TextUtils.isEmpty(metChooseLimitCollar.getText().toString())) {
                ToastUtils.showToast(CouponsCreateActivity.this, "请输入每人限领优惠券数量");
                return;
            }
            if (mLoadingDialog != null) {
                mLoadingDialog.show();
            }
            ClinkWorldApplication.httpHelper.execute(saveCouponsRunnable);
        }
    };

    @OnClick({R.id.et_coupons_type, R.id.et_input_send_quantity, R.id.et_choose_limit_collar, R.id.et_choose_start_time, R.id.et_choose_end_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_coupons_type:
                /** 优惠券类型选择 */
                if (couponsTypeChooseDialog != null) {
                    couponsTypeChooseDialog.show();
                }
                break;
            case R.id.et_input_send_quantity:
                /** 发放量选择 */
                if (couponsNumberChooseDialog != null) {
                    couponsNumberChooseDialog.show();
                    couponsNumberChooseDialog.setType(COUPONS_NUMBER);
                }
                break;
            case R.id.et_choose_limit_collar:
                /** 每人限领选择 */
                if (couponsNumberChooseDialog != null) {
                    couponsNumberChooseDialog.show();
                    couponsNumberChooseDialog.setType(COUPONS_GAIN);
                }
                break;
            case R.id.et_choose_start_time:
                /** 优惠券生效时间选择 */
                Calendar beginCalendar = Calendar.getInstance();
                if (TextUtils.isEmpty(beginTime)) {
                    beginTime = beginCalendar.get(Calendar.YEAR) + "," + (beginCalendar.get(Calendar.MONTH) + 1) + "," +
                            beginCalendar.get(Calendar.DAY_OF_MONTH);
                }
                WhellUtil.getInstance(this).showTimeWhell(this, metChooseStartTime, beginTime, OPwindow, handler, 0);
                break;
            case R.id.et_choose_end_time:
                /** 优惠券失效时间 */
                Calendar endCalendar = Calendar.getInstance();
                if (TextUtils.isEmpty(endTime)) {
                    endTime = endCalendar.get(Calendar.YEAR) + "," + (endCalendar.get(Calendar.MONTH) + 1) + "," +
                            endCalendar.get(Calendar.DAY_OF_MONTH);
                }
                WhellUtil.getInstance(this).showTimeWhell(this, metChooseEndTime, endTime, OPwindow, handler, 1);
                break;
        }
    }

    Runnable saveCouponsRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("type", String.valueOf(type));
            params.put("name", metInputCouponsName.getText().toString());
            if (type == 1) {
                /** 折扣 */
                params.put("coupon_value", metInputCouponsDiscount.getText().toString());
                params.put("max", metInputCouponsMaxDiscount.getText().toString());
            } else {
                params.put("coupon_value", metInputCouponsMoney.getText().toString());
            }
            params.put("condition", metInputCouponsFullTransactione.getText().toString());
            String startTi = "";
            String endTi = "";
            if (!TextUtils.isEmpty(metChooseStartTime.getText().toString())) {
                startTi = metChooseStartTime.getText().toString();
            }
            if (!TextUtils.isEmpty(metChooseEndTime.getText().toString())) {
                endTi = metChooseEndTime.getText().toString();
            }
            params.put("start_time", startTi);
            params.put("end_time", endTi);
            params.put("num", String.valueOf(sendQuantityNumber));
            params.put("user_limit", String.valueOf(limitGetNumber));

            String url = ServerUrl.BASE_URL + ServerUrl.COUPON_CREATE_PATH;
            String result = HttpClientC.post(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                handler.sendEmptyMessage(COUPONS_CREATE_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.getInt("status");
                    if (status == 200) {
                        handler.sendEmptyMessage(COUPONS_CREATE_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(COUPONS_CREATE_FAILURE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(COUPONS_CREATE_FAILURE);
                }
            }

        }
    };

}
