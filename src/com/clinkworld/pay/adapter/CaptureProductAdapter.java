package com.clinkworld.pay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.clinkworld.pay.R;
import com.clinkworld.pay.entity.ProductInfo;
import com.clinkworld.pay.views.SwipeItemLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shirenhua on 2015/10/20.
 * <p/>
 * 扫描的商品列表
 */
public class CaptureProductAdapter extends BaseAdapter {

    private Context context;
    private List<ProductInfo> productInfoLists;
    private DecimalFormat df;
    private UpdateToalOrderPriceListener updateToalOrderPriceListener;

    public CaptureProductAdapter(Context context, List<ProductInfo> productInfoList, UpdateToalOrderPriceListener updateToalOrderPriceListener) {
        this.context = context;
        this.productInfoLists = productInfoList;
        if (productInfoLists == null) {
            productInfoLists = new ArrayList<ProductInfo>();
        }
        this.updateToalOrderPriceListener = updateToalOrderPriceListener;
        df = new DecimalFormat("0.00");
    }


    @Override
    public int getCount() {
        return productInfoLists.size();
    }

    @Override
    public Object getItem(int position) {
        return productInfoLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.capture_product_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        final ProductInfo productInfo = productInfoLists.get(position);
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mtvProductBarCode.setText(productInfo.getProductBarCode());
        viewHolder.mtvProductName.setText(productInfo.getName());
        viewHolder.mtvProductPrice.setText("￥" + df.format(productInfo.getPrice()));
        viewHolder.mtvProductNumeber.setText(String.valueOf(productInfo.getNumber()));
        viewHolder.mtvProductCost.setText("￥" + df.format(productInfo.getNumber() * productInfo.getPrice()));

        return convertView;
    }

    public void setProductInfoLists(List<ProductInfo> productInfoLists) {
        this.productInfoLists = productInfoLists;
    }

    public List<ProductInfo> getProductInfoLists() {
        return productInfoLists;
    }

    class ViewHolder {
        /**
         * 商品条码
         */
        TextView mtvProductBarCode;
        /**
         * 商品名称
         */
        TextView mtvProductName;
        /**
         * 商品价格
         */
        TextView mtvProductPrice;
        /**
         * 商品数量
         */
        TextView mtvProductNumeber;
        /**
         * 商品小计
         */
        TextView mtvProductCost;
        /**
         * 减少数量
         */
        Button mbtnReduce;
        /**
         * 增加数量
         */
        Button mbtnAdd;

        public ViewHolder(View view) {
            this.mtvProductBarCode = (TextView) view.findViewById(R.id.item_product_barcode);
            this.mtvProductName = (TextView) view.findViewById(R.id.item_product_name);
            this.mtvProductPrice = (TextView) view.findViewById(R.id.item_product_price);
            this.mtvProductNumeber = (TextView) view.findViewById(R.id.item_product_number);
            this.mtvProductCost = (TextView) view.findViewById(R.id.item_product_cost);
            this.mbtnReduce = (Button) view.findViewById(R.id.cash_item_btn_reduce);
            this.mbtnAdd = (Button) view.findViewById(R.id.cash_item_btn_add);
        }

    }

    public interface UpdateToalOrderPriceListener {
        public void updateTotalOrderPrice(float totalCost);
    }


}
