package com.clinkworld.pay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.clinkworld.pay.R;
import com.clinkworld.pay.entity.StorageDetailInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srh on 2015/11/1.
 * <p/>
 * 订单详情商品列表适配器
 */
public class OrderDetailProductListAdapter extends BaseAdapter {

    private Context mContext;
    private List<StorageDetailInfo> storageDetailInfos;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public OrderDetailProductListAdapter(Context context, List<StorageDetailInfo> storageDetailInfos) {
        this.mContext = context;
        this.storageDetailInfos = storageDetailInfos;
        if (storageDetailInfos == null) {
            storageDetailInfos = new ArrayList<StorageDetailInfo>();
        }
    }

    @Override
    public int getCount() {
        return storageDetailInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return storageDetailInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_detail_list_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final StorageDetailInfo storageDetailInfo = storageDetailInfos.get(position);
        viewHolder.mtvProductBarCode.setText(storageDetailInfo.getProductBarcode());
        viewHolder.mtvProductName.setText(storageDetailInfo.getProductProductName());
        viewHolder.mtvProductPrice.setText("￥" + decimalFormat.format(storageDetailInfo.getProductPriceOut()));
        viewHolder.mtvProductNumeber.setText(String.valueOf(storageDetailInfo.getProductNumber()));
        viewHolder.mtvProductCost.setText("￥" + decimalFormat.format(storageDetailInfo.getProductPriceOut() * storageDetailInfo.getProductNumber()));
        return convertView;
    }

    public List<StorageDetailInfo> getStorageDetailInfos() {
        return storageDetailInfos;
    }

    class ViewHolder {
        /**
         * 商品条码
         */
        @ViewInject(R.id.detail_product_barcode)
        TextView mtvProductBarCode;
        /**
         * 商品名称
         */
        @ViewInject(R.id.detail_product_name)
        TextView mtvProductName;
        /**
         * 商品价格
         */
        @ViewInject(R.id.detail_product_price)
        TextView mtvProductPrice;
        /**
         * 商品数量
         */
        @ViewInject(R.id.detail_product_num)
        TextView mtvProductNumeber;
        /**
         * 商品小计
         */
        @ViewInject(R.id.detail_product_cost)
        TextView mtvProductCost;

        public ViewHolder(View view) {
            ViewUtils.inject(this, view);
        }
    }

}
