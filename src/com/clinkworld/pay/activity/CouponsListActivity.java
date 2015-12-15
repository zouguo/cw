package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.adapter.CouponsListAdapter;
import com.clinkworld.pay.entity.CouponsInfo;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.DialogUtils;
import com.clinkworld.pay.util.HttpClientC;
import com.clinkworld.pay.util.SafeHandler;
import com.clinkworld.pay.util.ToastUtils;
import com.clinkworld.pay.views.PullRefreshListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;


/**
 * Created by srh on 2015/11/2.
 * <p/>
 * 优惠券页面
 */
public class CouponsListActivity extends BaseActivity {

    private LeftBackRightTextTitleBar titleBar;
    private CouponsListAdapter mAdapter;
    private int mcurrentPage = 1;
    private final static int PAGE_SIZE = 30;
    private final static int MSG_SEARCH_COUPONS_SUCCESS = 200;
    private final static int MSG_SEARCH_COUPONS_FAILURE = 201;
    private final static int MSG_SEARCH_COUPONS_ERROR = 202;
    public final static int MSG_UPDATE_COUPONS_STATUS_SUCCESS = 203;
    public final static int MSG_UPDATE_COUPONS_STATUS_FAILURE = 204;
    public final static String UPDATE_COUPONS_PERMISSION = "com.clinkworld.pay.permission.UPDATE_COUPONS_BROADCAST";
    public final static String BROADCAST_UPDATE_COUPONS_ACTION = "com.clinkworld.pay.activity.CouponsListActivity.action.refreshCoupons";
    private Dialog mLoadingDialog;
    private static Dialog mDisableCouponDialog;
    private List<CouponsInfo> couponsInfoList = new ArrayList<CouponsInfo>();

    @ViewInject(R.id.coupons_list)
    PullRefreshListView mlv;

    SafeHandler mHandler = new SafeHandler(CouponsListActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH_COUPONS_SUCCESS:
                    /** 优惠券获取成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    if (mcurrentPage == 1) {
                        mlv.onRefreshComplete(new Date());
                        mAdapter.getCouponsInfos().clear();
                    } else {
                        mlv.onLoadMoreComplete();
                    }
                    if (msg.getData().getBoolean("canLoadMore")) {
                        mlv.setCanLoadMore(true);
                    } else {
                        mlv.setCanLoadMore(false);
                    }
                    List<CouponsInfo> couponsInfos = (List<CouponsInfo>) msg.obj;
                    mAdapter.getCouponsInfos().addAll(couponsInfos);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_SEARCH_COUPONS_FAILURE:
                    /** 优惠券获取失败 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    break;
                case MSG_SEARCH_COUPONS_ERROR:
                    /** status不为200的情况 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    mlv.onLoadMoreComplete();
                    mlv.setCanLoadMore(false);
                    ToastUtils.showToast(CouponsListActivity.this, (String) msg.obj);
                    break;
                case MSG_UPDATE_COUPONS_STATUS_SUCCESS:
                    if (mDisableCouponDialog != null) {
                        mDisableCouponDialog.dismiss();
                    }
                    int position = msg.getData().getInt("position");
                    int couponId = msg.getData().getInt("couponid");
                    for (int i = 0; i < mAdapter.getCouponsInfos().size(); i++) {
                        if ((position == i) && (couponId == mAdapter.getCouponsInfos().get(i).getId())) {
                            mAdapter.getCouponsInfos().get(i).setCouponStatus(0);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    ToastUtils.showToast(CouponsListActivity.this, "优惠券停用成功");
                    break;
                case MSG_UPDATE_COUPONS_STATUS_FAILURE:
                    if (mDisableCouponDialog != null) {
                        mDisableCouponDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(CouponsListActivity.this, errorMessage);
                    } else {
                        ToastUtils.showToast(CouponsListActivity.this, "网络错误，请查询您的网络设置");
                    }
                    break;
            }
        }
    };

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mcurrentPage = 1;
            ClinkWorldApplication.httpHelper.execute(searchCouponsListRunnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_UPDATE_COUPONS_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter, UPDATE_COUPONS_PERMISSION, new Handler());
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.coupons_list);
        titleBar.onPostActivityLayout();
        titleBar.setTitle(R.string.coupons_title);
        titleBar.setRightText(R.string.coupons_right_title);
        titleBar.showRightButton();
        titleBar.setOnRightClickListener(createCouponsLisener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void initView() {
        mLoadingDialog = DialogUtils.getLoadingDialog(CouponsListActivity.this, "正在加载中...");
        mDisableCouponDialog = DialogUtils.getLoadingDialog(CouponsListActivity.this, "正在停用...");
        mAdapter = new CouponsListAdapter(CouponsListActivity.this, couponsInfoList, mHandler);
        mlv.setAdapter(mAdapter);
        mlv.setCanLoadMore(false);
        mlv.setCanRefresh(true);
        mlv.setPullRefreshListener(new PullRefreshListView.PullRefreshListener() {
            @Override
            public void onRefresh() {
                mcurrentPage = 1;
                ClinkWorldApplication.httpHelper.execute(searchCouponsListRunnable);
            }

            @Override
            public void onLoadMore() {
                mcurrentPage += 1;
                ClinkWorldApplication.httpHelper.execute(searchCouponsListRunnable);
            }
        });
        mLoadingDialog.show();
        ClinkWorldApplication.httpHelper.execute(searchCouponsListRunnable);
    }

    View.OnClickListener createCouponsLisener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            /** 创建优惠券 */
            Intent intent = new Intent(CouponsListActivity.this, CouponsCreateActivity.class);
            startActivity(intent);
        }
    };

    public static void showDisableDialog() {
        if (mDisableCouponDialog != null) {
            mDisableCouponDialog.show();
        }
    }

    Runnable searchCouponsListRunnable = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("platform_id", ClinkWorldApplication.userDataInfo.getPlatfromId());
            params.put("page", String.valueOf(mcurrentPage));
            params.put("page_size", String.valueOf(PAGE_SIZE));

            String url = ServerUrl.BASE_URL + ServerUrl.COUPON_QUERY_LIST_PATH;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                ToastUtils.showToast(CouponsListActivity.this, "失败");
                mHandler.sendEmptyMessage(MSG_SEARCH_COUPONS_FAILURE);
            } else {
                try {
                    JSONObject resultJsonObject = new JSONObject(result);
                    int status = resultJsonObject.getInt("status");
                    if (status == 200) {
                        /** 获取成功 */
                        JSONObject dataJSONObject = resultJsonObject.getJSONObject("data");
                        JSONArray dataArray = dataJSONObject.getJSONArray("list");
                        List<CouponsInfo> couponsInfos = new ArrayList<CouponsInfo>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject itemJsonObject = dataArray.getJSONObject(i);
                            CouponsInfo couponsInfo = new CouponsInfo();
                            couponsInfo.setId(itemJsonObject.optInt("Id"));
                            couponsInfo.setMerchantId(itemJsonObject.optString("MerchantId"));
                            couponsInfo.setTitle(itemJsonObject.optString("Title"));
                            couponsInfo.setType(itemJsonObject.optInt("Type"));
                            couponsInfo.setCouponValue(Float.parseFloat(itemJsonObject.optString("CouponValue")));
                            couponsInfo.setStartTime(itemJsonObject.optString("StartTime"));
                            couponsInfo.setEndTime(itemJsonObject.optString("EndTime"));
                            couponsInfo.setSendQuantity(itemJsonObject.optInt("SendQuantity"));
                            couponsInfo.setQuantity(itemJsonObject.optInt("Quantity"));
                            couponsInfo.setCouponStatus(itemJsonObject.optInt("CouponStatus"));
                            couponsInfo.setUseQuantity(itemJsonObject.optInt("UseQuantity"));
                            couponsInfo.setUseCodition(itemJsonObject.optInt("CouponStatus"));
                            couponsInfo.setMax(itemJsonObject.optInt("Max"));
                            couponsInfo.setUseCodition(itemJsonObject.optInt("UseCondition"));
                            couponsInfo.setPushUrl(itemJsonObject.optString("push_url"));
                            couponsInfos.add(couponsInfo);
                        }
                        Message message = new Message();
                        message.what = MSG_SEARCH_COUPONS_SUCCESS;
                        Bundle bundle = new Bundle();
                        int pageCount = dataJSONObject.getInt("page_count");
                        if (pageCount > mcurrentPage) {
                            bundle.putBoolean("canLoadMore", true);
                        } else {
                            bundle.putBoolean("canLoadMore", false);
                        }
                        message.obj = couponsInfos;
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = MSG_SEARCH_COUPONS_ERROR;
                        message.obj = resultJsonObject.getString("info");
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }
    };

}
