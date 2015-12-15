package com.clinkworld.pay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.clinkworld.pay.util.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 订单查询列表适配器
 */
public class OrderSearchListAdapter extends BaseAdapter {

    private Context mContext;
    private List<OrderInfo> orderInfoList;

    public OrderSearchListAdapter(Context context, List<OrderInfo> orderInfos) {
        this.mContext = context;
        this.orderInfoList = orderInfos;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_search_list_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final OrderInfo orderInfo = orderInfoList.get(position);
        viewHolder.mtvNumber.setText(orderInfo.getOrderNumber());
        viewHolder.mtvPOSNumber.setText(orderInfo.getPosNumber());
        viewHolder.mtvMoney.setText("￥" + orderInfo.getOrderMoney());
        String statusStr = null;
        switch (orderInfo.getStatus()) {
            case 1:
                statusStr = "待付款";
                viewHolder.mtvStatus.setTextColor(Color.parseColor("#ff6600"));
                break;
            case 2:
                statusStr = "待发货";
                viewHolder.mtvStatus.setTextColor(Color.parseColor("#626262"));
                break;
            case 3:
                statusStr = "已发货";
                viewHolder.mtvStatus.setTextColor(Color.parseColor("#626262"));
                break;
            case 4:
                statusStr = "已收货";
                viewHolder.mtvStatus.setTextColor(Color.parseColor("#626262"));
                break;
            case 5:
                statusStr = "待评价";
                viewHolder.mtvStatus.setTextColor(Color.parseColor("#626262"));
                break;
            case 6:
                statusStr = "已评论";
                viewHolder.mtvStatus.setTextColor(Color.parseColor("#626262"));
                break;
            case 7:
                statusStr = "取消";
                viewHolder.mtvStatus.setTextColor(Color.parseColor("#626262"));
                break;
            case 8:
                statusStr = "退货中";
                viewHolder.mtvStatus.setTextColor(Color.parseColor("#626262"));
                break;
            case 9:
                statusStr = "退货完成";
                viewHolder.mtvStatus.setTextColor(Color.parseColor("#626262"));
                break;
        }
        viewHolder.mtvStatus.setText(statusStr);

        viewHolder.mtvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderInfo.getStatus() == 1) {
                    /** 未收款直接跳转收银页面 */
                    Intent intent = new Intent(mContext, PayOrderActivity.class);
                    OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
                    orderDetailInfo.setOrderId(orderInfo.getOrderNumber());
                    orderDetailInfo.setOrderMoney(orderInfo.getOrderMoney());
                    intent.putExtra(PayOrderActivity.ORDER_DETAIL_INFO, orderDetailInfo);
                    mContext.startActivity(intent);
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.ORDER_STATUS, orderInfo.getStatus());
                intent.putExtra(OrderDetailActivity.ORDER_ID, orderInfo.getOrderNumber());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public List<OrderInfo> getOrderInfoList() {
        return orderInfoList;
    }

    class ViewHolder {
        /**
         * 订单编号
         */
        @ViewInject(R.id.order_item_number)
        TextView mtvNumber;
        /**
         * POS机号
         */
        @ViewInject(R.id.order_item_pos_number)
        TextView mtvPOSNumber;
        /**
         * 订单金额
         */
        @ViewInject(R.id.order_item_money)
        TextView mtvMoney;
        /**
         * 订单状态
         */
        @ViewInject(R.id.order_item_status)
        TextView mtvStatus;

        public ViewHolder(View view) {
            ViewUtils.inject(this, view);
        }
    }

}
