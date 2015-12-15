package com.clinkworld.pay.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ListView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.adapter.CouponsUseListAdapter;
import com.clinkworld.pay.entity.CouponsCategoryInfo;
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
 * Created by srh on 2015/11/12.
 * 优惠券类别
 */
public class CouponsUseCategoryActivity extends BaseActivity {

    private String couponId;
    private Dialog mLoadingDialog;
    private LeftBackRightTextTitleBar titleBar;
    private CouponsUseListAdapter mAdapter;
    private int mCurrentPage = 1;
    private List<CouponsCategoryInfo> couponsCategoryInfoList = new ArrayList<CouponsCategoryInfo>();
    public final static String COUPONS_ID = "coupons_id";
    private final static int MSG_CATEGORY_SUCCESS = 1;
    private final static int MSG_CATEGORY_FAILURE = 2;

    @ViewInject(R.id.coupons_use_list)
    PullRefreshListView mlvUseList;

    SafeHandler safeHandler = new SafeHandler(CouponsUseCategoryActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CATEGORY_SUCCESS:
                    /** 成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    mlvUseList.setCanLoadMore(msg.getData().getBoolean("canLoadMore"));
                    if (mCurrentPage == 1) {
                        mlvUseList.onRefreshComplete(new Date());
                        mAdapter.getCouponsCategoryInfos().clear();
                    } else {
                        mlvUseList.onLoadMoreComplete();
                    }
                    List<CouponsCategoryInfo> couponsCategoryInfos = (List<CouponsCategoryInfo>) msg.obj;
                    mAdapter.getCouponsCategoryInfos().addAll(couponsCategoryInfos);
                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_CATEGORY_FAILURE:
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(CouponsUseCategoryActivity.this, errorMessage);
                    } else {
                        ToastUtils.showToast(CouponsUseCategoryActivity.this, getString(R.string.reg_httpclient_fail));
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
        setContentView(R.layout.coupons_category);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle("优惠券");
    }

    private void initView() {
        couponId = getIntent().getStringExtra(COUPONS_ID);
        mAdapter = new CouponsUseListAdapter(CouponsUseCategoryActivity.this, couponsCategoryInfoList);
        mlvUseList.setAdapter(mAdapter);
        mlvUseList.setCanLoadMore(false);
        mlvUseList.setCanRefresh(true);
        mlvUseList.setPullRefreshListener(new PullRefreshListView.PullRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 1;
                ClinkWorldApplication.httpHelper.execute(runnableCategory);
            }

            @Override
            public void onLoadMore() {
                mCurrentPage += 1;
                ClinkWorldApplication.httpHelper.execute(runnableCategory);
            }
        });

        mLoadingDialog = DialogUtils.getLoadingDialog(CouponsUseCategoryActivity.this, "正在加载...");
        mLoadingDialog.show();
        ClinkWorldApplication.httpHelper.execute(runnableCategory);
    }

    Runnable runnableCategory = new Runnable() {
        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            String url = ServerUrl.BASE_URL + "/coupon/" + couponId + "/use_list";
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_CATEGORY_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_CATEGORY_SUCCESS;
                        JSONObject dataJSONObject = resultJSONObject.getJSONObject("data");
                        JSONArray listArray = dataJSONObject.getJSONArray("list");
                        List<CouponsCategoryInfo> couponsCategoryInfos = new ArrayList<CouponsCategoryInfo>();
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject itemJSONObject = listArray.getJSONObject(i);
                            CouponsCategoryInfo couponsCategoryInfo = new CouponsCategoryInfo();
                            couponsCategoryInfo.setMemberName(itemJSONObject.optString("MemberName"));
                            couponsCategoryInfo.setOrderId(itemJSONObject.optString("OrderID"));
                            couponsCategoryInfos.add(couponsCategoryInfo);
                        }
                        message.obj = couponsCategoryInfos;
                        int currentPage = dataJSONObject.optInt("current_page");
                        int pageCount = dataJSONObject.optInt("page_count");
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("canLoadMore", (pageCount > currentPage));
                        message.setData(bundle);
                    } else {
                        message.what = MSG_CATEGORY_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_CATEGORY_FAILURE);
                }
            }
        }
    };
}
