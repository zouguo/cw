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
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srh on 2015/10/30.
 * <p/>
 * 入库单详情适配器
 */
public class StorageDetailAdapter extends BaseAdapter {

    private Context mContext;
    private List<StorageDetailInfo> mstorageDetailInfoList;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.

    public StorageDetailAdapter(Context context, List<StorageDetailInfo> storageDetailInfos) {
        this.mContext = context;
        this.mstorageDetailInfoList = storageDetailInfos;
        if (mstorageDetailInfoList == null) {
            mstorageDetailInfoList = new ArrayList<StorageDetailInfo>();
        }
    }

    @Override
    public int getCount() {
        return mstorageDetailInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mstorageDetailInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.storage_detail_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        StorageDetailInfo mcurrentDetailInfo = mstorageDetailInfoList.get(position);
        viewHolder.mtvProduct.setText(mcurrentDetailInfo.getProductProductName() + mcurrentDetailInfo.getProductBarcode());
        viewHolder.mtvNumber.setText(String.valueOf(mcurrentDetailInfo.getProductNumber()));
        viewHolder.mtvPurchasing.setText("￥" + decimalFormat.format(mcurrentDetailInfo.getProductPriceIn()));
        viewHolder.mtvPrice.setText("￥" + decimalFormat.format(mcurrentDetailInfo.getProductPriceOut()));
        return convertView;
    }

    public List<StorageDetailInfo> getMstorageDetailInfoList() {
        return mstorageDetailInfoList;
    }

    class ViewHolder {
        /**
         * 商品名称编码
         */
        @ViewInject(R.id.tv_storage_detail_product)
        TextView mtvProduct;
        /**
         * 商品数量
         */
        @ViewInject(R.id.tv_storage_detail_number)
        TextView mtvNumber;
        /**
         * 商品成本价
         */
        @ViewInject(R.id.tv_storage_detail_purchasing)
        TextView mtvPurchasing;
        /**
         * 商品售价
         */
        @ViewInject(R.id.tv_storage_deatil_price)
        TextView mtvPrice;

        public ViewHolder(View view) {
            ViewUtils.inject(this, view);
        }
    }

}
