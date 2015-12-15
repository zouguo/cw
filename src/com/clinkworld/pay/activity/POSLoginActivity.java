package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.DialogUtils;
import com.clinkworld.pay.util.HttpClientC;
import com.clinkworld.pay.util.SafeHandler;
import com.clinkworld.pay.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shirenhua on 2015/10/16.
 * <p/>
 * POS机登录页面
 */
public class POSLoginActivity extends BaseActivity {

    public final static String TAG = "POSLoginActivity.this";
    private LeftBackRightTextTitleBar titleBar;
    public final static int MSG_POS_LOGIN_SUCCESS = 1;
    public final static int MSG_POS_LOGIN_FAILURE = 2;
    Dialog mPOSLoginDialog;

    /**
     * POS机号码输入
     */
    @ViewInject(R.id.et_input_posnumber)
    EditText metInputPOSNumber;

    SafeHandler safeHandler = new SafeHandler(POSLoginActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_POS_LOGIN_SUCCESS:
                    if (mPOSLoginDialog != null) {
                        mPOSLoginDialog.dismiss();
                    }
                    Intent intent = new Intent(instance, ScanGoodsActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case MSG_POS_LOGIN_FAILURE:
                    if (mPOSLoginDialog != null) {
                        mPOSLoginDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(POSLoginActivity.this, "网络错误，请检查网络后重试");
                    } else {
                        ToastUtils.showToast(POSLoginActivity.this, errorMessage);
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
        setContentView(R.layout.pos_login);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle(R.string.title_login);
    }

    private void initView() {
        mPOSLoginDialog = DialogUtils.getLoadingDialog(POSLoginActivity.this, "正在登录POS机...");
    }

    /**
     * pos机登录
     */
    private void posLogin() {

        if (TextUtils.isEmpty(metInputPOSNumber.getText().toString())) {
            ToastUtils.showToast(instance, R.string.prompt_input_posnumber);
            return;
        }
        if (mPOSLoginDialog != null) {
            mPOSLoginDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(POSLoginRunnable);
    }

    Runnable POSLoginRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("pos_no", metInputPOSNumber.getText().toString());

            String url = ServerUrl.BASE_URL + ServerUrl.LOGIN_POS_PATH;
            String result = HttpClientC.post(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_POS_LOGIN_FAILURE);
            } else {
                try {
                    JSONObject resultJsonObject = new JSONObject(result);
                    int status = resultJsonObject.getInt("status");
                    if (status == 200) {
                        safeHandler.sendEmptyMessage(MSG_POS_LOGIN_SUCCESS);
                    } else {
                        Message message = new Message();
                        message.what = MSG_POS_LOGIN_FAILURE;
                        if (resultJsonObject.has("info")) {
                            message.obj = resultJsonObject.getString("info");
                        }
                        safeHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_POS_LOGIN_FAILURE);
                }
            }
        }
    };

    @OnClick(R.id.btn_pos_login)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pos_login:
                /** POS机登录 */
                posLogin();
                break;
        }
    }

}
