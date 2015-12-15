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
import com.clinkworld.pay.activity.StorageDetailActivity;
import com.clinkworld.pay.entity.IncomeProductBatchInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srh on 2015/10/14.
 */
public class InComeProductAdapter extends BaseAdapter {

    private Context mContext;
    private List<IncomeProductBatchInfo> mIncomeProductBatchInfos;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.

    public InComeProductAdapter(Context context, List<IncomeProductBatchInfo> incomeProductBatchInfoList) {
        this.mContext = context;
        this.mIncomeProductBatchInfos = incomeProductBatchInfoList;
        if (mIncomeProductBatchInfos == null) {
            mIncomeProductBatchInfos = new ArrayList<IncomeProductBatchInfo>();
        }
    }

    @Override
    public int getCount() {
        return mIncomeProductBatchInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mIncomeProductBatchInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.storage_list_item, null);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final IncomeProductBatchInfo incomeProductBatchInfo = mIncomeProductBatchInfos.get(position);
        viewHolder.mtvStorageNumber.setText(incomeProductBatchInfo.getRecordId());
        String date = incomeProductBatchInfo.getDate();
        if (TextUtils.isEmpty(date) || "null".equals(date)) {
            viewHolder.mtvStorageDate.setText(date);
        } else {
            String[] dateData = date.split(" ");
            viewHolder.mtvStorageDate.setText(dateData[0].trim() + "\n" + dateData[1].trim());
        }
        viewHolder.mtvStoragePurchasingCost.setText("￥" + decimalFormat.format(incomeProductBatchInfo.getMoneryIn()));
        viewHolder.mtvStorageGrossGoods.setText("￥" + decimalFormat.format(incomeProductBatchInfo.getMoneryOut()));
        viewHolder.mtvStorageGossProfit.setText(decimalFormat.format(incomeProductBatchInfo.getGrossProfit() * 100));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StorageDetailActivity.class);
                intent.putExtra(StorageDetailActivity.STORAGE_NUMBER, incomeProductBatchInfo.getRecordId());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public List<IncomeProductBatchInfo> getmIncomeProductBatchInfos() {
        return mIncomeProductBatchInfos;
    }

    class ViewHolder {
        /**
         * 入库单号
         */
        @ViewInject(R.id.tv_storage_number)
        TextView mtvStorageNumber;
        /**
         * 日期
         */
        @ViewInject(R.id.tv_storage_date)
        TextView mtvStorageDate;
        /**
         * 进货成本
         */
        @ViewInject(R.id.tv_storage_purchasing_cost)
        TextView mtvStoragePurchasingCost;
        /**
         * 货物总值
         */
        @ViewInject(R.id.tv_storage_gross_goods)
        TextView mtvStorageGrossGoods;
        /**
         * 毛利
         */
        @ViewInject(R.id.tv_storage_gross_profit)
        TextView mtvStorageGossProfit;

        public ViewHolder(View view) {
            ViewUtils.inject(this, view);
        }
    }

}
