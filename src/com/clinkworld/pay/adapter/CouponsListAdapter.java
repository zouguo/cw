package com.clinkworld.pay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.activity.*;
import com.clinkworld.pay.entity.CouponsInfo;
import com.clinkworld.pay.util.HttpClientC;
import com.clinkworld.pay.util.SafeHandler;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 优惠券适配器
 */
public class CouponsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CouponsInfo> couponsInfos;
    private SafeHandler mHandler;


    public CouponsListAdapter(Context context, List<CouponsInfo> couponsInfoList, SafeHandler mHandler) {
        this.mContext = context;
        this.couponsInfos = couponsInfoList;
        this.mHandler = mHandler;
        if (couponsInfos == null) {
            couponsInfos = new ArrayList<CouponsInfo>();
        }
    }

    @Override
    public int getCount() {
        return couponsInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return couponsInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.coupons_list_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        final CouponsInfo couponsInfo = couponsInfos.get(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        switch (couponsInfo.getType()) {
            case 1:
                /** 折扣 */
                viewHolder.mtvCoupons.setText(String.valueOf((int) couponsInfo.getCouponValue()) + "折");
                viewHolder.mtvMaxCoupons.setVisibility(View.VISIBLE);
                viewHolder.mtvMaxCoupons.setText(mContext.getString(R.string.coupons_max_discount, couponsInfo.getMax()));
                viewHolder.mllCouponsNumber.setVisibility(View.GONE);
                break;
            case 2:
                /** 面值 */
                viewHolder.mtvCoupons.setText("￥" + String.valueOf((int) couponsInfo.getCouponValue()));
                viewHolder.mtvMaxCoupons.setVisibility(View.GONE);
                viewHolder.mllCouponsNumber.setVisibility(View.VISIBLE);
                break;
        }

        switch (couponsInfo.getCouponStatus()) {
            case 1:
                /** 正常状态 */
                viewHolder.mllCouponsItem.setAlpha(255);
                viewHolder.mtvMaxCoupons.setTextColor(Color.parseColor("#626262"));
                viewHolder.mtvUseCondition.setTextColor(Color.parseColor("#626262"));
                viewHolder.mtvValidityPeriod.setTextColor(Color.parseColor("#626262"));
                viewHolder.mtvCouponsQuantity.setTextColor(Color.parseColor("#626262"));
                viewHolder.mtvCouponsSendQuantity.setTextColor(Color.parseColor("#626262"));
                viewHolder.mtvCouponsUseQuantity.setTextColor(Color.parseColor("#626262"));
                viewHolder.mtvCoupons.setTextColor(Color.parseColor("#008BF7"));
                viewHolder.mivCouponsDisabled.setVisibility(View.GONE);
                viewHolder.mllSendCoupons.setVisibility(View.VISIBLE);
                break;
            case 0:
                /** 停用状态 */
                viewHolder.mllCouponsItem.setAlpha(100);
                viewHolder.mtvMaxCoupons.setTextColor(Color.parseColor("#C8C8C8"));
                viewHolder.mtvUseCondition.setTextColor(Color.parseColor("#C8C8C8"));
                viewHolder.mtvValidityPeriod.setTextColor(Color.parseColor("#C8C8C8"));
                viewHolder.mtvCoupons.setTextColor(Color.parseColor("#C8C8C8"));
                viewHolder.mtvCouponsQuantity.setTextColor(Color.parseColor("#C8C8C8"));
                viewHolder.mtvCouponsSendQuantity.setTextColor(Color.parseColor("#C8C8C8"));
                viewHolder.mtvCouponsUseQuantity.setTextColor(Color.parseColor("#C8C8C8"));
                viewHolder.mivCouponsDisabled.setVisibility(View.VISIBLE);
                viewHolder.mllSendCoupons.setVisibility(View.GONE);
                break;
        }

        String startTime[] = couponsInfo.getStartTime().split(" ");
        String time1 = startTime[0];
        String endTime[] = couponsInfo.getEndTime().split(" ");
        String time2 = endTime[0];
        viewHolder.mtvValidityPeriod.setText(mContext.getString(R.string.coupons_validity_period, time1, time2));
        viewHolder.mtvUseCondition.setText(mContext.getString(R.string.coupons_use_condition, couponsInfo.getUseCodition()));
        if (couponsInfo.getQuantity() == 0) {
            /** 不限 */
            viewHolder.mtvCouponsQuantity.setText("发放不限");
        } else {
            viewHolder.mtvCouponsQuantity.setText("发放" + couponsInfo.getQuantity() + "张");
        }

        viewHolder.mtvCouponsSendQuantity.setText("已领取" + String.valueOf(couponsInfo.getSendQuantity()) + "张");

        viewHolder.mtvCouponsUseQuantity.setText("已使用" + String.valueOf(couponsInfo.getUseQuantity()) + "张");

        viewHolder.mtvCouponsUseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CouponsUseCategoryActivity.class);
                intent.putExtra(CouponsUseCategoryActivity.COUPONS_ID, String.valueOf(couponsInfo.getId()));
                mContext.startActivity(intent);
            }
        });

        viewHolder.mtvCouponsSendQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CouponsSendCategoryActivity.class);
                intent.putExtra(CouponsSendCategoryActivity.COUPONS_ID, String.valueOf(couponsInfo.getId()));
                mContext.startActivity(intent);
            }
        });

        viewHolder.mbtnQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, QRcodeCouponsActivity.class);
                intent.putExtra(QRcodeCouponsActivity.QRCODE_COUPONS, couponsInfo.getPushUrl());
                mContext.startActivity(intent);
            }
        });

        viewHolder.mbtnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CouponsLinkActivity.class);
                intent.putExtra(CouponsLinkActivity.COUPONS_LINK, couponsInfo.getPushUrl());
                mContext.startActivity(intent);
            }
        });
        viewHolder.mbtnDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CouponsListActivity.showDisableDialog();
                ClinkWorldApplication.httpHelper.execute(new DisableRunnable(position, couponsInfo.getId()));
            }
        });

        return convertView;
    }

    class DisableRunnable implements Runnable {

        public int position;
        public int couponId;

        public DisableRunnable(int paramPosition, int paramCouponId) {
            this.position = paramPosition;
            this.couponId = paramCouponId;
        }

        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            String paramsData = String.valueOf(couponId);
            String url = ServerUrl.BASE_URL + "/coupon/" + paramsData + "/status";
            String result = HttpClientC.put(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                mHandler.sendEmptyMessage(CouponsListActivity.MSG_UPDATE_COUPONS_STATUS_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.getInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = CouponsListActivity.MSG_UPDATE_COUPONS_STATUS_SUCCESS;
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putInt("couponid", couponId);
                        message.setData(bundle);
                    } else {
                        if (resultJSONObject.has("info")) {
                            message.obj = resultJSONObject.getString("info");
                            message.what = CouponsListActivity.MSG_UPDATE_COUPONS_STATUS_FAILURE;
                        }
                    }
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(CouponsListActivity.MSG_UPDATE_COUPONS_STATUS_FAILURE);
                }
            }

        }
    }


    public List<CouponsInfo> getCouponsInfos() {
        return couponsInfos;
    }

    class ViewHolder {
        /**
         * 子条目布局
         */
        @ViewInject(R.id.ll_coupons_item)
        LinearLayout mllCouponsItem;

        /**
         * 优惠面值
         */
        @ViewInject(R.id.tv_coupons_itme)
        TextView mtvCoupons;
        /**
         * 最多优惠面值
         */
        @ViewInject(R.id.tv_max_coupons)
        TextView mtvMaxCoupons;
        /**
         * 优惠条件
         */
        @ViewInject(R.id.tv_use_condition)
        TextView mtvUseCondition;
        /**
         * 优惠有效期
         */
        @ViewInject(R.id.tv_validity_period)
        TextView mtvValidityPeriod;
        /**
         * 链接发券、二维码发券布局
         */
        @ViewInject(R.id.ll_send_coupons)
        LinearLayout mllSendCoupons;
        /**
         * 链接发券按钮
         */
        @ViewInject(R.id.btn_link)
        Button mbtnLink;
        /**
         * 二维码发券按钮
         */
        @ViewInject(R.id.btn_qrcode)
        Button mbtnQrcode;
        /**
         * 停用按钮
         */
        @ViewInject(R.id.btn_disable)
        Button mbtnDisable;

        /**
         * 已停用标识
         */
        @ViewInject(R.id.iv_coupons_disabled)
        ImageView mivCouponsDisabled;
        /**
         * 优惠券数量
         */
        @ViewInject(R.id.tv_coupons_quantity)
        TextView mtvCouponsQuantity;
        /**
         * 优惠券领取数量
         */
        @ViewInject(R.id.tv_coupons_send_quantity)
        TextView mtvCouponsSendQuantity;
        /**
         * 优惠券使用数量
         */
        @ViewInject(R.id.tv_coupons_use_quantity)
        TextView mtvCouponsUseQuantity;
        /**
         * 优惠券数量布局
         */
        @ViewInject(R.id.ll_coupons_number)
        LinearLayout mllCouponsNumber;

        public ViewHolder(View view) {
            ViewUtils.inject(this, view);
        }
    }

}
