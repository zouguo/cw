package com.clinkworld.pay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.clinkworld.pay.R;
import com.clinkworld.pay.activity.OrderDetailActivity;
import com.clinkworld.pay.activity.PayOrderActivity;
import com.clinkworld.pay.entity.OrderDetailInfo;
import com.clinkworld.pay.entity.OrderInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 订单列表适配器
 */
public class OrderListAdapter extends BaseAdapter {

    private Context mContext;
    private List<OrderInfo> orderInfoList;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    /**
     * 当前显示的订单页面状态类型
     * 0：全部订单
     * 1：成交订单
     * 2：未成交订单
     */
    private int statusType;

    public OrderListAdapter(Context context, int statusType, List<OrderInfo> orderInfos) {
        this.mContext = context;
        this.orderInfoList = orderInfos;
        this.statusType = statusType;
        if (orderInfoList == null) {
            orderInfoList = new ArrayList<OrderInfo>();
        }
    }

    @Override
    public int getCount() {
        return orderInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_list_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final OrderInfo orderInfo = orderInfoList.get(position);
        viewHolder.mtvItem1.setText(orderInfo.getOrderNumber());
        viewHolder.mtvItem2.setText(orderInfo.getPosNumber());
        switch (statusType) {
            case 0:
                /** 全部订单 */

                //********************************* 生成时间 **************************************
                if (TextUtils.isEmpty(orderInfo.getOrderCreateTime()) || "null".equals(orderInfo.getOrderCreateTime())) {
                    viewHolder.mtvItem3.setText(orderInfo.getOrderCreateTime());
                } else {
                    String createTime = orderInfo.getOrderCreateTime();
                    String[] createTimeArray = createTime.split(" ");
                    viewHolder.mtvItem3.setText(createTimeArray[0].trim() + "\n" + createTimeArray[1].trim());
                }

                //********************************* 完成时间 **************************************
                String completeTime = orderInfo.getOrderCompleteTime();
                if (TextUtils.isEmpty(completeTime) || "null".equals(completeTime)) {
                    viewHolder.mtvItem4.setText(completeTime);
                } else {
                    String[] completeTimeArray = completeTime.split(" ");
                    viewHolder.mtvItem4.setText(completeTimeArray[0].trim() + "\n" + completeTimeArray[1].trim());
                }

                if (orderInfo.getStatus() == 1) {
                    viewHolder.mtvItem5.setTextColor(Color.parseColor("#ff6600"));
                    viewHolder.mtvItem5.setText(mContext.getString(R.string.order_search__result_not_cash));
//                    viewHolder.mtvItem5.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            /** TODO:收银页面跳转 */
//                            Intent intent = new Intent(mContext, PayOrderActivity.class);
//                            OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
//                            orderDetailInfo.setOrderId(orderInfo.getOrderNumber());
//                            orderDetailInfo.setOrderMoney(orderInfo.getOrderMoney());
//                            intent.putExtra(PayOrderActivity.ORDER_DETAIL_INFO, orderDetailInfo);
//                            mContext.startActivity(intent);
//                        }
//                    });
                } else {
                    viewHolder.mtvItem5.setText(mContext.getString(R.string.order_search__result_success));
                    viewHolder.mtvItem5.setTextColor(Color.parseColor("#626262"));
                }
                break;
            case 1:
                /** 成交订单 */

                //********************************* 收款时间 **************************************
                String gatheringTime = orderInfo.getOrderGatheringTime();
                if (TextUtils.isEmpty(gatheringTime) || "null".equals(gatheringTime)) {
                    viewHolder.mtvItem3.setText(gatheringTime);
                } else {
                    String[] gatheringTimeArray = gatheringTime.split(" ");
                    viewHolder.mtvItem3.setText(gatheringTimeArray[0].trim() + "\n" + gatheringTimeArray[1].trim());
                }

                //********************************* 订单金额 **************************************
                viewHolder.mtvItem4.setText("￥" + decimalFormat.format(orderInfo.getOrderMoney()));

                //********************************* 收款方式 **************************************
                switch (orderInfo.getGatheringType()) {
                    case 0:
                        /** 现金支付 */
                        viewHolder.mtvItem5.setText(mContext.getString(R.string.pay_type_cash_receipts));
                        break;
                    case 1:
                        /** 微信支付 */
                        viewHolder.mtvItem5.setText(mContext.getString(R.string.pay_type_orde_weixin));
                        break;
                    case 2:
                        /** 支付宝支付 */
                        viewHolder.mtvItem5.setText(mContext.getString(R.string.pay_type_order_zhifubao));
                        break;
                }
                break;
            case 2:
                /** 未成交订单 */

                //********************************* 生成时间 **************************************
                String createTime1 = orderInfo.getOrderCreateTime();
                if (TextUtils.isEmpty(createTime1) || "null".equals(createTime1)) {
                    viewHolder.mtvItem3.setText(createTime1);
                } else {
                    String[] createTimeArray1 = createTime1.split(" ");
                    viewHolder.mtvItem3.setText(createTimeArray1[0].trim() + "\n" + createTimeArray1[1].trim());
                }

                //********************************* 订单金额 **************************************
                viewHolder.mtvItem4.setText("￥" + decimalFormat.format(orderInfo.getOrderMoney()));
                viewHolder.mtvItem5.setTextColor(Color.parseColor("#ff6600"));
                viewHolder.mtvItem5.setText(mContext.getString(R.string.order_search__result_not_cash));
//                viewHolder.mtvItem5.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        /** TODO:收银页面跳转 */
//                        Intent intent = new Intent(mContext, PayOrderActivity.class);
//                        OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
//                        orderDetailInfo.setOrderId(orderInfo.getOrderNumber());
//                        orderDetailInfo.setOrderMoney(orderInfo.getOrderMoney());
//                        orderDetailInfo.setCouponsMoney(orderInfo.getCouponsMoney());
//                        intent.putExtra(PayOrderActivity.ORDER_DETAIL_INFO, orderDetailInfo);
//                        mContext.startActivity(intent);
//                    }
//                });
                break;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.ORDER_ID, orderInfo.getOrderNumber());
                intent.putExtra(OrderDetailActivity.ORDER_STATUS, orderInfo.getStatus());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public List<OrderInfo> getOrderInfoList() {
        return orderInfoList;
    }

    class ViewHolder {
        @ViewInject(R.id.order_list_item_1)
        TextView mtvItem1;

        @ViewInject(R.id.order_list_item_2)
        TextView mtvItem2;

        @ViewInject(R.id.order_list_item_3)
        TextView mtvItem3;

        @ViewInject(R.id.order_list_item_4)
        TextView mtvItem4;

        @ViewInject(R.id.order_list_item_5)
        TextView mtvItem5;

        public ViewHolder(View view) {
            ViewUtils.inject(this, view);
        }
    }

}
