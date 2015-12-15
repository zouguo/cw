package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.entity.UserDataInfo;
import com.clinkworld.pay.util.*;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by srh on 2015/10/21.
 * <p/>
 * 首页
 */
public class MainActivity extends BaseActivity {

    public final static String TAG = "MainActivity.this";
    public final static int MSG_URSER_LOGOUT_SUCCESS = 1;
    public final static int MSG_USER_LOGOUT_FAILURE = 2;
    public final static int MSG_USER_LOGIN_SUCCESS = 3;
    public final static int MSG_USER_LOGIN_FAILURE = 4;
    Dialog mExitLoadingDialog;

    /**
     * 商户号
     */
    @ViewInject(R.id.tv_mearchant_id)
    TextView mtvMerchantId;

    /**
     * 商户名称
     */
    @ViewInject(R.id.tv_merchant_name)
    TextView mtvMerchantName;
    /**
     * 平台号
     */
    @ViewInject(R.id.tv_platform_id)
    TextView mtvPlatformId;

    /**
     * 工号
     */
    @ViewInject(R.id.tv_work_id)
    TextView mtvWorkId;

    /**
     * 用户名
     */
    @ViewInject(R.id.tv_home_user_name)
    TextView mtvUserName;

    /**
     * 布局一
     */
    @ViewInject(R.id.ll_1)
    LinearLayout linearLayout1;

    /**
     * 布局二
     */
    @ViewInject(R.id.ll_2)
    LinearLayout linearLayout2;
    /**
     * 公司图标
     */
    @ViewInject(R.id.iv_company_icon)
    ImageView mivCompanyIcon;
    /**
     * 公司名称
     */
    @ViewInject(R.id.tv_company_name)
    TextView mtvCompanyName;


    SafeHandler safeHandler = new SafeHandler(MainActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_URSER_LOGOUT_SUCCESS:
                    if (mExitLoadingDialog != null) {
                        mExitLoadingDialog.dismiss();
                    }
                    ClinkWorldApplication.userDataInfo = null;
                    PreferencesManager.getInstance(MainActivity.this).setLoginPlatform("");
                    PreferencesManager.getInstance(MainActivity.this).setLoginMerchant("");
                    PreferencesManager.getInstance(MainActivity.this).setLoginUsername("");
                    PreferencesManager.getInstance(MainActivity.this).setLoginPassword("");
                    Intent loginIntent = new Intent(MainActivity.this, UserLoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                    break;
                case MSG_USER_LOGOUT_FAILURE:
                    if (mExitLoadingDialog != null) {
                        mExitLoadingDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(MainActivity.this, "网络错误，请检查网络后重试");
                    } else {
                        ToastUtils.showToast(MainActivity.this, errorMessage);
                    }
                    break;
                case MSG_USER_LOGIN_SUCCESS:
                    initData();
                    break;
                case MSG_USER_LOGIN_FAILURE:
                    login();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** TODO: 首先判断用户是否已经登录，session_id和username是否失效 */
        if (ClinkWorldApplication.userDataInfo == null) {
            if (TextUtils.isEmpty(PreferencesManager.getInstance(MainActivity.this).getLoginPlatform(""))) {
                login();
                return;
            } else {
                ClinkWorldApplication.httpHelper.execute(userLoginRunnable);
            }
        }
        initView();
    }

    private void initView() {
        mExitLoadingDialog = DialogUtils.getLoadingDialog(MainActivity.this, "正在退出...");
    }

    private void initData() {
        ImageLoader.getInstance().init(
                ImageUtils.getSimpleImageLoaderConfig(getApplicationContext()));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenWidth / 2);
        linearLayout1.setLayoutParams(layoutParams);
        linearLayout2.setLayoutParams(layoutParams);

        if (ClinkWorldApplication.userDataInfo != null) {
            mtvMerchantId.setText(ClinkWorldApplication.userDataInfo.getMerchantId());
            mtvMerchantName.setText(ClinkWorldApplication.userDataInfo.getMerchantName());
            mtvWorkId.setText(ClinkWorldApplication.userDataInfo.getWorkId());
            mtvUserName.setText(ClinkWorldApplication.userDataInfo.getTrueName());
            mtvPlatformId.setText(getString(R.string.login_user_platform_prompt, ClinkWorldApplication.userDataInfo.getPlatfromId()));
            mtvCompanyName.setText(ClinkWorldApplication.userDataInfo.getCompanyName());
            DisplayImageOptions options =
                    new DisplayImageOptions.Builder()
                            .showStubImage(R.drawable.icon_default)
                            .showImageForEmptyUri(R.drawable.icon_default)
                            .showImageOnFail(R.drawable.icon_default)
                            .cacheInMemory(true).cacheOnDisc()
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .imageScaleType(ImageScaleType.EXACTLY).build();

            ImageLoader.getInstance().displayImage(ClinkWorldApplication.userDataInfo.getPlatformLogo(), mivCompanyIcon, options);
        }
        ClinkWorldApplication.httpHelper.execute(PayTypeRunnable);
    }

    private void login() {
        Intent loginIntent = new Intent(MainActivity.this, UserLoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void addCurrentLayout() {
        setContentView(R.layout.home);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 退出平台
     */
    private void exitPlatform() {
        if (mExitLoadingDialog != null) {
            mExitLoadingDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(exitPlatformRunnable);
    }

    Runnable PayTypeRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            String url = ServerUrl.BASE_URL + ServerUrl.ORDER_PAY_TYPE_PATH;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {

            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    if (status == 200) {
                        JSONArray dataArray = resultJSONObject.getJSONArray("data");
                        ClinkWorldApplication.mApplication.setPaytypeSetting(dataArray.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable userLoginRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("platform_id", PreferencesManager.getInstance(MainActivity.this).getLoginPlatform(""));
            params.put("merchant_id", PreferencesManager.getInstance(MainActivity.this).getLoginMerchant(""));
            params.put("work_id", PreferencesManager.getInstance(MainActivity.this).getLoginUsername(""));
            params.put("user_pass", PreferencesManager.getInstance(MainActivity.this).getLoginPassword(""));

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
                        JSONObject dataJsonObject = resultJSONObject.getJSONObject("data");
                        UserDataInfo userDataInfo = new UserDataInfo();
                        userDataInfo.setCompanyId(dataJsonObject.optString("company_id"));
                        userDataInfo.setCompanyName(dataJsonObject.optString("platform_name"));
                        userDataInfo.setPlatformLogo(dataJsonObject.optString("platform_logo_mobile"));
                        userDataInfo.setRoleName(dataJsonObject.optString("role_name"));
                        userDataInfo.setPlatfromId(dataJsonObject.optString("platform_id"));
                        userDataInfo.setMerchantId(dataJsonObject.optString("merchant_id"));
                        userDataInfo.setMerchantName(dataJsonObject.optString("merchant_name"));
                        userDataInfo.setWorkId(dataJsonObject.optString("work_id"));
                        userDataInfo.setTrueName(dataJsonObject.optString("true_name"));
                        userDataInfo.setSex(dataJsonObject.optInt("sex"));
                        userDataInfo.setEmail(dataJsonObject.optString("email"));
                        userDataInfo.setPOSId(dataJsonObject.optString("pos_id"));
                        ClinkWorldApplication.userDataInfo = userDataInfo;
                        safeHandler.sendEmptyMessage(MSG_USER_LOGIN_SUCCESS);
                    } else {
                        safeHandler.sendEmptyMessage(MSG_USER_LOGIN_FAILURE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_USER_LOGIN_FAILURE);
                }
            }
        }
    };


    Runnable exitPlatformRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            String url = ServerUrl.BASE_URL + ServerUrl.LOGOUT_USER_PATH;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_USER_LOGOUT_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.getInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_URSER_LOGOUT_SUCCESS;
                    } else {
                        message.what = MSG_USER_LOGOUT_FAILURE;
                        message.obj = resultJSONObject.getString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_USER_LOGOUT_FAILURE);
                }
            }


        }
    };


    @OnClick({R.id.rl_part_cash, R.id.rl_part_mc, R.id.rl_part_order, R.id.rl_part_coupons, R.id.home_btn_exit_platform})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_part_cash:
                /** 收银 */
                Intent intent = new Intent(instance, ScanGoodsActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_part_mc:
                /** 商品入库 */
                Intent intentMc = new Intent(instance, StorageListActivity.class);
                startActivity(intentMc);
                break;
            case R.id.rl_part_order:
                /** 订单 */
                Intent intentOrder = new Intent(instance, OrderFlowActivity.class);
                startActivity(intentOrder);
                break;
            case R.id.rl_part_coupons:
                /** 优惠券 */
                Intent intentCoupons = new Intent(instance, CouponsListActivity.class);
                startActivity(intentCoupons);
                break;
            case R.id.home_btn_exit_platform:
                /** 退出平台 */
                exitPlatform();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
