package com.clinkworld.pay.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.text.TextUtils;
import android.view.*;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.activity.ScanGoodsActivity;
import com.clinkworld.pay.entity.ProductInfo;
import com.clinkworld.pay.util.HttpClientC;
import com.clinkworld.pay.util.SafeHandler;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by srh on 2015/10/27.
 * <p/>
 * 条形码信息弹框
 */
public class BarcodeInfoPop {

    private Context mContext;
    private PopupWindow mPopupWindow;
    private View mContentView;
    private LayoutInflater inflater;
    private String barcodeContent;
    private PopupWindowDismissListener popupWindowDismissListener;
    /**
     * 标识入口来源，是扫描二维码进入弹框还是手动输入二维码进入弹框
     * true：扫描二维码进入弹框
     * false：手动输入二维码进入弹框
     */
    private boolean type;
    public final static int MSG_SEARCH_PRODUCT_SUCCESS = 200;
    public final static int MSG_SEARCH_PRODUCT_FAILURE = 400;

    @ViewInject(R.id.capture_input_barcode)
    EditText metInputBarcode;

    @ViewInject(R.id.has_exist_barcode)
    TextView mtvHasExistBarcode;

    @ViewInject(R.id.capture_input_number)
    EditText metInputNumber;

    private ProductInfo mCurrentProductInfo;

    SafeHandler handler = new SafeHandler(mContext) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH_PRODUCT_SUCCESS:
                    /** 获取商品信息成功 */
                    mCurrentProductInfo = (ProductInfo) msg.obj;
                    mtvHasExistBarcode.setVisibility(View.GONE);
                    if (!type) {
                        mPopupWindow.dismiss();
                        if (popupWindowDismissListener != null) {
                            popupWindowDismissListener.dismiss();
                        }
                        /** 改变商品扫描列表数据 */
                        Intent intent = new Intent(ScanGoodsActivity.BROADCAST_UPDATE_PRODUCT_ACTION);
                        mCurrentProductInfo.setNumber(Integer.parseInt(metInputNumber.getText().toString()));
                        intent.putExtra("addProduct", mCurrentProductInfo);
                        mContext.sendBroadcast(intent, ScanGoodsActivity.UPDATE_PRODUCT_PERMISSION);
                    }
                    break;
                case MSG_SEARCH_PRODUCT_FAILURE:
                    /** 获取商品信息失败 */
                    mtvHasExistBarcode.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    /**
     * 展示条形码弹出框
     *
     * @param anchor
     * @param type   false: 手动输入二维码进入弹框
     *               true:  扫描二维码进入弹框
     */
    public void show(View anchor, boolean type) {
        this.type = type;
        mPopupWindow = new PopupWindow(mContentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.setAnimationStyle(R.style.BarcodePopupStyle);
        mPopupWindow.showAtLocation(anchor, Gravity.BOTTOM, 0, 0);

        metInputBarcode.setText(barcodeContent);
        if (type) {
            /** 扫描二维码进入 */
            metInputBarcode.setEnabled(false);
            metInputNumber.setEnabled(false);
            SearchProductRunnable runnable = new SearchProductRunnable(metInputBarcode.getText().toString());
            ClinkWorldApplication.httpHelper.execute(runnable);
        }
        metInputNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(metInputNumber.getText().toString())) {
                        metInputNumber.setText("1");
                    }
                }
            }
        });
    }

    public BarcodeInfoPop(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        mContentView = inflater.inflate(R.layout.barcode_pop, null);
        ViewUtils.inject(BarcodeInfoPop.this, mContentView);
        metInputNumber.setSelection(metInputNumber.length());
    }

    public void setBarcodeContent(String barcodeContent) {
        this.barcodeContent = barcodeContent;
    }

    public void setPopupWindowDismissListener(PopupWindowDismissListener popupWindowDismissListener) {
        this.popupWindowDismissListener = popupWindowDismissListener;
    }

    @OnClick({R.id.btn_add, R.id.btn_reduce, R.id.tv_cancel, R.id.tv_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                int numberAdd = Integer.parseInt(metInputNumber.getText().toString());
                metInputNumber.setText(String.valueOf(numberAdd + 1));
                break;
            case R.id.btn_reduce:
                int numberReduce = Integer.parseInt(metInputNumber.getText().toString());
                if (numberReduce == 1) {
                    metInputNumber.setText("1");
                } else {
                    metInputNumber.setText(String.valueOf(numberReduce - 1));
                }
                break;
            case R.id.tv_cancel:
                mPopupWindow.dismiss();
                if (popupWindowDismissListener != null) {
                    popupWindowDismissListener.dismiss();
                }
                break;
            case R.id.tv_ok:
                if (type) {
                    /** 扫描二维码进入的*/
                    if (mtvHasExistBarcode.getVisibility() == View.VISIBLE) {
                        /** 当前扫描到的二维码不存在 */
                        mPopupWindow.dismiss();
                        if (popupWindowDismissListener != null) {
                            popupWindowDismissListener.dismiss();
                        }
                    } else if (mtvHasExistBarcode.getVisibility() == View.GONE) {
                        /** 当前扫描到的二维码存在 TODO: 在商品扫描列表中新增商品*/
                        Intent intent = new Intent(ScanGoodsActivity.BROADCAST_UPDATE_PRODUCT_ACTION);
                        mCurrentProductInfo.setNumber(Integer.parseInt(metInputNumber.getText().toString()));
                        intent.putExtra("addProduct", mCurrentProductInfo);
                        mContext.sendBroadcast(intent, ScanGoodsActivity.UPDATE_PRODUCT_PERMISSION);
                        mPopupWindow.dismiss();
                        if (popupWindowDismissListener != null) {
                            popupWindowDismissListener.dismiss();
                        }
                    }
                } else {
                    /** 手动输入二维码进入弹框 TODO:查询商品信息*/
                    SearchProductRunnable runnable = new SearchProductRunnable(metInputBarcode.getText().toString());
                    ClinkWorldApplication.httpHelper.execute(runnable);
                }
                break;
        }
    }

    class SearchProductRunnable implements Runnable {

        private String mProductBarCode;

        public SearchProductRunnable(String productBarCode) {
            this.mProductBarCode = productBarCode;
        }

        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            String url = ServerUrl.BASE_URL + ServerUrl.PRODUCT_INIFO_PATH + mProductBarCode;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                handler.sendEmptyMessage(MSG_SEARCH_PRODUCT_FAILURE);
            } else {
                try {
                    JSONObject resultJsonObject = new JSONObject(result);
                    int status = resultJsonObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_SEARCH_PRODUCT_SUCCESS;
                        JSONObject dataJSONObject = resultJsonObject.getJSONObject("data");
                        ProductInfo productInfo = new ProductInfo();
                        productInfo.setProductBarCode(mProductBarCode);
                        productInfo.setName(dataJSONObject.optString("name"));
                        productInfo.setPrice(Float.valueOf(dataJSONObject.optString("selling_price")));
                        message.obj = productInfo;
                    } else {
                        message.what = MSG_SEARCH_PRODUCT_FAILURE;
                        message.obj = resultJsonObject.optString("info");
                    }
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(MSG_SEARCH_PRODUCT_FAILURE);
                }
            }
        }
    }

    public interface PopupWindowDismissListener {
        public void dismiss();
    }
}
