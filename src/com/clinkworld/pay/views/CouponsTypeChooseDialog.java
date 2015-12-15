package com.clinkworld.pay.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.clinkworld.pay.R;

/**
 * Created by srh
 */
public class CouponsTypeChooseDialog extends Dialog {
    public static CouponsTypeChooseDialog instance = null;
    public final static int DIALOG_COUPONS_TYPE_MONEY = 100;
    public final static int DIALOG_COUPONS_TYPE_DISCOUNT = 101;
    private Button btnMoney, btnDiscount, cancel_btn;
    private Handler handler;
    private Context context;

    public CouponsTypeChooseDialog(Context context, int theme, Handler handler) {
        super(context, theme);
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupons_type_choose_dialog);
        instance = this;

        btnMoney = (Button) findViewById(R.id.btn_money);
        btnMoney.setOnClickListener(mOnClicks);
        btnDiscount = (Button) findViewById(R.id.btn_discount);
        btnDiscount.setOnClickListener(mOnClicks);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(mOnClicks);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
    }

    View.OnClickListener mOnClicks = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_money:
                    /** 面值券选择 */
                    Message msgMoney = new Message();
                    msgMoney.what = DIALOG_COUPONS_TYPE_MONEY;
                    msgMoney.obj = "2";
                    handler.sendMessage(msgMoney);
                    CouponsTypeChooseDialog.this.dismiss();
                    break;
                case R.id.btn_discount:
                    /** 折扣券选择 */
                    Message msgDiscount = new Message();
                    msgDiscount.what = DIALOG_COUPONS_TYPE_DISCOUNT;
                    msgDiscount.obj = "1";
                    handler.sendMessage(msgDiscount);
                    CouponsTypeChooseDialog.this.dismiss();
                    break;
                default:
                    CouponsTypeChooseDialog.this.dismiss();
                    break;
            }
        }
    };
}
