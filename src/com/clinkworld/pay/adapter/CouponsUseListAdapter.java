package com.clinkworld.pay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.clinkworld.pay.R;
import com.clinkworld.pay.entity.CouponsCategoryInfo;
import com.clinkworld.pay.entity.OrderInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 优惠券使用适配器
 */
public class CouponsUseListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CouponsCategoryInfo> couponsCategoryInfos;

    public CouponsUseListAdapter(Context context, List<CouponsCategoryInfo> couponsCategoryInfos) {
        this.mContext = context;
        this.couponsCategoryInfos = couponsCategoryInfos;
        if (couponsCategoryInfos == null) {
            couponsCategoryInfos = new ArrayList<CouponsCategoryInfo>();
        }
    }

    @Override
    public int getCount() {
        return couponsCategoryInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return couponsCategoryInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.coupons_use_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        CouponsCategoryInfo couponsCategoryInfo = couponsCategoryInfos.get(position);
        viewHolder.mtvUseItem.setText("会员" + couponsCategoryInfo.getMemberName() + "于订单" + couponsCategoryInfo.getOrderId() + "中使用了优惠券");

        return convertView;
    }

    public List<CouponsCategoryInfo> getCouponsCategoryInfos() {
        return couponsCategoryInfos;
    }

    class ViewHolder {

        @ViewInject(R.id.tv_use_item)
        TextView mtvUseItem;

        public ViewHolder(View view) {
            ViewUtils.inject(this, view);
        }
    }

}
