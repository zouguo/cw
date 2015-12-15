package com.clinkworld.pay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.entity.OrderDetailInfo;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.text.DecimalFormat;

/**
 * Created by srh on 2015/11/2.
 * <p/>
 * 订单支付结果页面
 */
public class PayOrderResultActivity extends BaseActivity {

    private LeftBackRightTextTitleBar titleBar;
    private OrderDetailInfo orderDetailInfo;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public final static String ORDER_DETAIL_INFO = "order_detail_info";
    /**
     * 登录的POS机
     */
    @ViewInject(R.id.pos_number)
    TextView mtvPOSNumber;

    /**
     * 用户工号
     */
    @ViewInject(R.id.work_id)
    TextView mtvWorkId;

    /**
     * 收银员
     */
    @ViewInject(R.id.user_name)
    TextView mtvUserName;
    /**
     * 交易方式
     */
    @ViewInject(R.id.tv_pay_channel)
    TextView mtvPayChannel;
    /**
     * 交易金额
     */
    @ViewInject(R.id.tv_total_money)
    TextView mtvTotalMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.order_pay_result);
        titleBar.onPostActivityLayout();
        titleBar.setTitle("支付");
        titleBar.hideRightButton();
    }

    private void initView() {
        orderDetailInfo = (OrderDetailInfo) getIntent().getSerializableExtra(ORDER_DETAIL_INFO);
        if (ClinkWorldApplication.userDataInfo != null) {
            mtvPOSNumber.setText(getString(R.string.pos_number, ClinkWorldApplication.userDataInfo.getPOSId()));
            mtvWorkId.setText(getString(R.string.user_number, ClinkWorldApplication.userDataInfo.getWorkId()));
            mtvUserName.setText(getString(R.string.user_name, ClinkWorldApplication.userDataInfo.getTrueName()));
        }
        if (orderDetailInfo != null) {
            switch (orderDetailInfo.getPayChannelId()) {
                case 0:
                    /** 现金 */
                    mtvPayChannel.setText("订单交易成功，现金入账");
                    mtvTotalMoney.setText("￥" + decimalFormat.format(orderDetailInfo.getShouldReceiveMoney()));
                    break;
                case 1:
                    /** 微信 */
                    mtvPayChannel.setText("订单交易成功，微信入账");
                    mtvTotalMoney.setText("￥" + decimalFormat.format(orderDetailInfo.getOrderMoney()));
                    break;
                case 2:
                    /** 支付宝 */
                    mtvPayChannel.setText("订单交易成功，支付宝入账");
                    mtvTotalMoney.setText("￥" + decimalFormat.format(orderDetailInfo.getOrderMoney()));
                    break;
            }
        }
    }

    @OnClick({R.id.show_order_detail, R.id.show_cash_money})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_order_detail:
                /** 返回订单详情 */
                Intent intent = new Intent(PayOrderResultActivity.this, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.ORDER_ID, orderDetailInfo.getOrderId());
                intent.putExtra(OrderDetailActivity.ORDER_STATUS, orderDetailInfo.getPayStatus());
                startActivity(intent);
                finish();
                break;
            case R.id.show_cash_money:
                /** 返回收银 */
                Intent intentCashMoney = new Intent(PayOrderResultActivity.this, ScanGoodsActivity.class);
                startActivity(intentCashMoney);
                finish();
                break;
        }
    }


}
