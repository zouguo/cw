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
public class CouponsNumberChooseDialog extends Dialog {
    public static CouponsNumberChooseDialog instance = null;
    public final static int DIALOG_COUPONS_NUMBER_NOT_LIMITED = 102;
    public final static int DIALOG_COUPONS_NUMBER_DEFINE = 103;
    private Button btnNotLimited, btnDefine, cancel_btn;
    private Handler handler;
    private Context context;
    /**
     * 类型：
     * 包括优惠券发放量选择和优惠券每人限领量选择
     */
    private int type;

    public CouponsNumberChooseDialog(Context context, int theme, Handler handler) {
        super(context, theme);
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupons_number_choose_dialog);
        instance = this;

        btnNotLimited = (Button) findViewById(R.id.btn_not_limited);
        btnNotLimited.setOnClickListener(mOnClicks);
        btnDefine = (Button) findViewById(R.id.btn_define);
        btnDefine.setOnClickListener(mOnClicks);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(mOnClicks);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
    }

    public void setType(int type) {
        this.type = type;
    }

    View.OnClickListener mOnClicks = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_not_limited:
                    /** 不限 */
                    Message msgNotLimited = new Message();
                    msgNotLimited.what = DIALOG_COUPONS_NUMBER_NOT_LIMITED;
                    msgNotLimited.obj = String.valueOf(type);
                    handler.sendMessage(msgNotLimited);
                    CouponsNumberChooseDialog.this.dismiss();
                    break;
                case R.id.btn_define:
                    /** 自定义 */
//                    Message msgDefine = new Message();
//                    msgDefine.what = DIALOG_COUPONS_NUMBER_DEFINE;
//                    msgDefine.obj = String.valueOf(type);
//                    handler.sendMessage(msgDefine);
                    CouponsNumberChooseDialog.this.dismiss();
                    InputNumberDialog dialog = new InputNumberDialog(context, R.style.DownToUpSlideDialog, handler);
                    dialog.setType(type);
                    dialog.show();
                    break;
                default:
                    CouponsNumberChooseDialog.this.dismiss();
                    break;
            }
        }
    };
}
