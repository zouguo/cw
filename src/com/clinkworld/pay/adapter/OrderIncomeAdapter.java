package com.clinkworld.pay.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.clinkworld.pay.R;
import com.clinkworld.pay.activity.OrderDetailActivity;
import com.clinkworld.pay.entity.OrderInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 今日收入适配器
 */
public class OrderIncomeAdapter extends BaseAdapter {

    private Context mContext;
    private List<OrderInfo> orderInfoList;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public OrderIncomeAdapter(Context context, List<OrderInfo> orderInfos) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_income_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final OrderInfo orderInfo = orderInfoList.get(position);
        viewHolder.mtvItem1.setText(orderInfo.getOrderNumber());
        viewHolder.mtvItem2.setText(orderInfo.getPosNumber());

        String createTime = orderInfo.getOrderCompleteTime();
        if (TextUtils.isEmpty(createTime) || "null".equals(createTime)) {
            viewHolder.mtvItem3.setText(createTime);
        } else {
            String[] createTimeArray = createTime.split(" ");
            viewHolder.mtvItem3.setText(createTimeArray[0].trim() + "\n" + createTimeArray[1].trim());
        }

        switch (orderInfo.getGatheringType()) {
            case 0:
                viewHolder.mtvIncomeType.setText(mContext.getString(R.string.pay_type_cash_receipts));
                break;
            case 1:
                viewHolder.mtvIncomeType.setText(mContext.getString(R.string.pay_type_orde_weixin));
                break;
            case 2:
                viewHolder.mtvIncomeType.setText(mContext.getString(R.string.pay_type_order_zhifubao));
                break;
        }

        viewHolder.mtvItem5.setText("￥" + decimalFormat.format(orderInfo.getOrderMoney()));

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
        TextView mtvIncomeType;

        @ViewInject(R.id.order_list_item_5)
        TextView mtvItem5;


        public ViewHolder(View view) {
            ViewUtils.inject(this, view);
        }
    }

}
