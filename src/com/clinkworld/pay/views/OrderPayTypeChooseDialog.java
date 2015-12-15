package com.clinkworld.pay.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.util.UiUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by srh
 * <p/>
 * 支付方式选择框
 */
public class OrderPayTypeChooseDialog extends Dialog {
    public static OrderPayTypeChooseDialog instance = null;
    public final static int DIALOG_ORDER_TYPE_CASH = 100;
    public final static int DIALOG_ORDER_TYPE_ZHIFUBAO = 101;
    public final static int DIALOG_ORDER_TYPE_WEIXIN = 102;
    private Button btnCash, btnZhifubao, btnWeixin, cancel_btn;
    private Handler handler;
    private Context context;
    private LinearLayout mllbtn;

    public OrderPayTypeChooseDialog(Context context, int theme, Handler handler) {
        super(context, theme);
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_type_choose_dialog);
        instance = this;

        mllbtn = (LinearLayout) findViewById(R.id.ll_btn);

        String payTypeStr = ClinkWorldApplication.mApplication.getPaytypeSetting();
        try {
            JSONArray dataArray = new JSONArray(payTypeStr);
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject itemJSONObject = dataArray.getJSONObject(i);
                String name = itemJSONObject.optString("Name");
                final int channel = itemJSONObject.optInt("Chanel");

                Button btn = new Button(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtils.dp2px(context, 45));
                btn.setText(name);
                btn.setGravity(Gravity.CENTER);
                btn.setBackgroundResource(R.drawable.dlg_btn2_bg);
                btn.setTextSize(16);
                btn.setTextColor(Color.parseColor("#0099ff"));
                mllbtn.addView(btn, params);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (channel) {
                            case 0:
                                Message msgCash = new Message();
                                msgCash.what = DIALOG_ORDER_TYPE_CASH;
                                handler.sendMessage(msgCash);
                                OrderPayTypeChooseDialog.this.dismiss();
                                break;
                            case 1:
                                Message msgWeixin = new Message();
                                msgWeixin.what = DIALOG_ORDER_TYPE_WEIXIN;
                                handler.sendMessage(msgWeixin);
                                OrderPayTypeChooseDialog.this.dismiss();
                                break;
                            case 2:
                                Message msgZhifubao = new Message();
                                msgZhifubao.what = DIALOG_ORDER_TYPE_ZHIFUBAO;
                                handler.sendMessage(msgZhifubao);
                                OrderPayTypeChooseDialog.this.dismiss();
                                break;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderPayTypeChooseDialog.this.dismiss();
            }
        });

//        btnCash = (Button) findViewById(R.id.btn_cash);
//        btnCash.setOnClickListener(mOnClicks);
//        btnZhifubao = (Button) findViewById(R.id.btn_zhifubao);
//        btnZhifubao.setOnClickListener(mOnClicks);
//        btnWeixin = (Button) findViewById(R.id.btn_weixin);
//        btnWeixin.setOnClickListener(mOnClicks);
//        cancel_btn = (Button) findViewById(R.id.cancel_btn);
//        cancel_btn.setOnClickListener(mOnClicks);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
    }


//    View.OnClickListener mOnClicks = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.btn_cash:
//                    /** 现金 */
//                    Message msgCash = new Message();
//                    msgCash.what = DIALOG_ORDER_TYPE_CASH;
//                    handler.sendMessage(msgCash);
//                    OrderPayTypeChooseDialog.this.dismiss();
//                    break;
//                case R.id.btn_zhifubao:
//                    /** 支付宝 */
//                    Message msgZhifubao = new Message();
//                    msgZhifubao.what = DIALOG_ORDER_TYPE_ZHIFUBAO;
//                    handler.sendMessage(msgZhifubao);
//                    OrderPayTypeChooseDialog.this.dismiss();
//                    break;
//                case R.id.btn_weixin:
//                    /** 微信 */
//                    Message msgWeixin = new Message();
//                    msgWeixin.what = DIALOG_ORDER_TYPE_WEIXIN;
//                    handler.sendMessage(msgWeixin);
//                    OrderPayTypeChooseDialog.this.dismiss();
//                    break;
//                default:
//                    OrderPayTypeChooseDialog.this.dismiss();
//                    break;
//            }
//        }
//    };
}
