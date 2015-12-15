package com.clinkworld.pay.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.clinkworld.pay.R;

/**
 * Created by srh
 */
public class InputNumberDialog extends Dialog {
    public static InputNumberDialog instance = null;
    public final static int DIALOG_INPUT_CANCEL = 50;
    public final static int DIALOG_INPUT_COMMIT = 51;
    public final static String INPUT_NUMBER = "input_nubmer";
    private EditText metInputNumber;
    private Button btnCommit, btnCacel;
    private Handler handler;
    private Context context;
    /**
     * 类型：
     * 包括优惠券发放量选择和优惠券每人限领量选择
     */
    private int type;

    public InputNumberDialog(Context context, int theme, Handler handler) {
        super(context, theme);
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_number_dialog);
        instance = this;

        metInputNumber = (EditText) findViewById(R.id.et_input_number);
        btnCommit = (Button) findViewById(R.id.commit);
        btnCommit.setOnClickListener(mOnClicks);
        btnCacel = (Button) findViewById(R.id.cancel);
        btnCacel.setOnClickListener(mOnClicks);

        metInputNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setGravity(Gravity.CENTER);
    }

    public void setType(int type) {
        this.type = type;
    }

    View.OnClickListener mOnClicks = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.commit:
                    Message msgCommit = new Message();
                    msgCommit.what = DIALOG_INPUT_COMMIT;
                    msgCommit.obj = String.valueOf(type);
                    Bundle bundle = new Bundle();
                    bundle.putString(INPUT_NUMBER, metInputNumber.getText().toString());
                    msgCommit.setData(bundle);
                    handler.sendMessage(msgCommit);
                    InputNumberDialog.this.dismiss();
                    break;
                case R.id.cancel:
                    Message msgCancel = new Message();
                    msgCancel.what = DIALOG_INPUT_CANCEL;
                    msgCancel.obj = String.valueOf(type);
                    handler.sendMessage(msgCancel);
                    InputNumberDialog.this.dismiss();
                    break;
                default:
                    InputNumberDialog.this.dismiss();
                    break;
            }
        }
    };
}
