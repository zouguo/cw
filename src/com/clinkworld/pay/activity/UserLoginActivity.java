package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.entity.UserDataInfo;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.titlebar.LeftLogoRightTextTitleBar;
import com.clinkworld.pay.titlebar.LeftTextRightTextTitleBar;
import com.clinkworld.pay.util.*;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.json.JSONObject;

import java.util.*;


/**
 * 用户登录页面
 */
public class UserLoginActivity extends BaseActivity {

    public final static String TAG = "UserLoginActivity.this";
    private LeftBackRightTextTitleBar titleBar;
    public final static int MSG_USER_LOGIN_SUCCESS = 1;
    public final static int MSG_USER_LOGIN_FAILURE = 2;
    private Dialog mLoginDialog;
    private boolean rememberFlag;

    /**
     * 平台号
     */
    @ViewInject(R.id.et_input_platform)
    EditText metInputPlatform;

    /**
     * 商户号
     */
    @ViewInject(R.id.et_input_mechant)
    EditText metInputMechant;

    /**
     * 用户名
     */
    @ViewInject(R.id.et_input_username)
    EditText metInputUserName;

    /**
     * 密码
     */
    @ViewInject(R.id.et_input_password)
    EditText metInputPassword;
    /**
     * 记住帐号
     */
    @ViewInject(R.id.btn_check)
    CheckBox rememberAcountBox;

    SafeHandler safeHandler = new SafeHandler(UserLoginActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_USER_LOGIN_SUCCESS:
                    if (mLoginDialog != null) {
                        mLoginDialog.dismiss();
                    }
                    Intent intent = new Intent(instance, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case MSG_USER_LOGIN_FAILURE:
                    if (mLoginDialog != null) {
                        mLoginDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(UserLoginActivity.this, "网络错误，请检查网络后重试");
                    } else {
                        ToastUtils.showToast(UserLoginActivity.this, errorMessage);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.user_login);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.hideBackButton();
        titleBar.setTitle(R.string.title_login);
    }

    private void initView() {
        mLoginDialog = DialogUtils.getLoadingDialog(UserLoginActivity.this, "正在登录...");
        rememberAcountBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rememberFlag = true;
                } else {
                    rememberFlag = false;
                }
            }
        });
        String userInfoStr = PreferencesManager.getInstance(UserLoginActivity.this).getUserInfo("");
        if (!TextUtils.isEmpty(userInfoStr)) {
            try {
                JSONObject jsonObject = new JSONObject(userInfoStr);
                metInputPlatform.setText(jsonObject.getString("platform"));
                metInputMechant.setText(jsonObject.getString("mechant"));
                metInputUserName.setText(jsonObject.getString("username"));
//                metInputPassword.setText(jsonObject.getString("password"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        metInputPlatform.setSelection(metInputPlatform.length());
        metInputMechant.setSelection(metInputMechant.length());
        metInputUserName.setSelection(metInputUserName.length());
        metInputPassword.setSelection(metInputPassword.length());
    }

    /**
     * 登录
     */
    private void login() {
        /** 平台号为空 */
        if (TextUtils.isEmpty(metInputPlatform.getText().toString())) {
            ToastUtils.showToast(instance, R.string.prompt_platform_can_not_empty);
            return;
        }
        /** 商户号为空 */
        if (TextUtils.isEmpty(metInputMechant.getText().toString())) {
            ToastUtils.showToast(instance, R.string.prompt_merchant_can_not_empty);
            return;
        }
        /** 用户名为空 */
        if (TextUtils.isEmpty(metInputUserName.getText().toString())) {
            ToastUtils.showToast(instance, R.string.prompt_username_can_not_empty);
            return;
        }
        /** 密码为空 */
        if (TextUtils.isEmpty(metInputPassword.getText().toString())) {
            ToastUtils.showToast(instance, R.string.prompt_password_can_not_empty);
            return;
        }
        if (mLoginDialog != null) {
            mLoginDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(userLoginRunnable);
    }


    Runnable userLoginRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("platform_id", metInputPlatform.getText().toString());
            params.put("merchant_id", metInputMechant.getText().toString());
            params.put("work_id", metInputUserName.getText().toString());
            params.put("user_pass", metInputPassword.getText().toString());

            String url = ServerUrl.BASE_URL + ServerUrl.LOGIN_USER_PATH;
            String result = HttpClientC.post(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_USER_LOGIN_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.getInt("status");


                    if (status == 200) {
                        /** 登录成功 */
                        /** 登录成功标识 TODO:跳转POS机登录 */

                        PreferencesManager.getInstance(UserLoginActivity.this).setLoginPlatform(metInputPlatform.getText().toString());
                        PreferencesManager.getInstance(UserLoginActivity.this).setLoginMerchant(metInputMechant.getText().toString());
                        PreferencesManager.getInstance(UserLoginActivity.this).setLoginUsername(metInputUserName.getText().toString());
                        PreferencesManager.getInstance(UserLoginActivity.this).setLoginPassword(metInputPassword.getText().toString());

                        if (rememberFlag) {
                            /** 记住密码 */
                            try {
                                JSONObject userInfoOjbect = new JSONObject();
                                userInfoOjbect.put("platform", metInputPlatform.getText().toString());
                                userInfoOjbect.put("mechant", metInputMechant.getText().toString());
                                userInfoOjbect.put("username", metInputUserName.getText().toString());
//                                userInfoOjbect.put("password", metInputPassword.getText().toString());
                                PreferencesManager.getInstance(UserLoginActivity.this).setUserInfo(userInfoOjbect.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            PreferencesManager.getInstance(UserLoginActivity.this).setUserInfo("");
                        }
                        JSONObject dataJsonObject = resultJSONObject.getJSONObject("data");
                        UserDataInfo userDataInfo = new UserDataInfo();
                        userDataInfo.setCompanyId(dataJsonObject.optString("company_id"));
                        userDataInfo.setCompanyName(dataJsonObject.optString("platform_name"));
                        userDataInfo.setPlatformLogo(dataJsonObject.optString("platform_logo_mobile"));
                        userDataInfo.setRoleName(dataJsonObject.optString("role_name"));
                        userDataInfo.setPOSId(dataJsonObject.optString("pos_id"));
                        if (dataJsonObject.has("platform_id")) {
                            userDataInfo.setPlatfromId(dataJsonObject.getString("platform_id"));
                        }
                        if (dataJsonObject.has("merchant_id")) {
                            userDataInfo.setMerchantId(dataJsonObject.getString("merchant_id"));
                        }

                        if (dataJsonObject.has("merchant_name")) {
                            userDataInfo.setMerchantName(dataJsonObject.getString("merchant_name"));
                        }

                        if (dataJsonObject.has("work_id")) {
                            userDataInfo.setWorkId(dataJsonObject.getString("work_id"));
                        }
                        userDataInfo.setTrueName(dataJsonObject.optString("true_name"));
                        if (dataJsonObject.has("email")) {
                            userDataInfo.setEmail(dataJsonObject.getString("email"));
                        }
                        userDataInfo.setPOSId(dataJsonObject.optString("pos_id"));
                        ClinkWorldApplication.userDataInfo = userDataInfo;
                        safeHandler.sendEmptyMessage(MSG_USER_LOGIN_SUCCESS);
                    } else {
                        Message message = new Message();
                        if (resultJSONObject.has("info")) {
                            message.obj = resultJSONObject.getString("info");
                        }
                        message.what = MSG_USER_LOGIN_FAILURE;
                        safeHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_USER_LOGIN_FAILURE);
                }
            }


        }
    };

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    /**
     * 重置数据
     */
    public void resetInput() {
        metInputPlatform.setText("");
        metInputMechant.setText("");
        metInputUserName.setText("");
        metInputPassword.setText("");
    }


    @OnClick({R.id.btn_login, R.id.ll_rest_password})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.ll_rest_password:
                /** 重置密码 */
                resetInput();
                break;
        }
    }

}
