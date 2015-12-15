package com.clinkworld.pay.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import com.clinkworld.pay.R;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.UiUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Hashtable;


/**
 * Created by srh on 2015/11/5.
 * <p/>
 * 二维码发券
 */
public class QRcodeCouponsActivity extends BaseActivity {

    private LeftBackRightTextTitleBar titleBar;
    public final static String QRCODE_COUPONS = "qrcode_coupons";

    @ViewInject(R.id.iv_qrcode_coupons)
    ImageView mivQrcodeCoupons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String couponsQrcode = getIntent().getStringExtra(QRCODE_COUPONS);
        if (!TextUtils.isEmpty(couponsQrcode)) {
            createQRImage(couponsQrcode);
        }
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.qrcode_coupons);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle(R.string.qrcode_coupons_title);

    }

    public void createQRImage(String encodingData) {
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            int QR_WIDTH = UiUtils.dp2px(QRcodeCouponsActivity.this, 160);
            int QR_HEIGHT = UiUtils.dp2px(QRcodeCouponsActivity.this, 160);
            BitMatrix bitMatrix = new QRCodeWriter().encode(encodingData, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);

            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap mQrcodeBitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            mQrcodeBitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            mivQrcodeCoupons.setImageBitmap(mQrcodeBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
