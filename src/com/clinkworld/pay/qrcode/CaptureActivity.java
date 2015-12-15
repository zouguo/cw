package com.clinkworld.pay.qrcode;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.activity.AddProductActivity;
import com.clinkworld.pay.activity.BaseActivity;
import com.clinkworld.pay.activity.PayOrderActivity;
import com.clinkworld.pay.activity.ScanGoodsActivity;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.ToastUtils;
import com.clinkworld.pay.views.BarcodeInfoPop;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.clinkworld.pay.qrcode.camera.CameraManager;
import com.clinkworld.pay.qrcode.decoding.CaptureActivityHandler;
import com.clinkworld.pay.qrcode.decoding.InactivityTimer;
import com.clinkworld.pay.qrcode.view.ViewfinderView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by srh on 2015/10/18.
 * <p/>
 * 扫描商品
 */
public class CaptureActivity extends BaseActivity implements Callback {
    public final static String QRCODE_TYPE = "qrcode_type";
    public final static String PAY_TYPE_CHANNEL = "pay_type_channel";
    /**
     * 标识来源入
     * 1:扫描条形码(添加商品)
     * 2:扫描二维码（付款码）
     * 3:扫描条形码(商品入库)
     * 4:扫描二维码(优惠券)
     */
    private int captureType = 1;
    private int payorderType = 1;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private LeftBackRightTextTitleBar titleBar;

    /**
     * 手动输入条形码按钮
     */
    @ViewInject(R.id.btn_input_barcode)
    Button mbtnInputBarcode;

    @ViewInject(R.id.ll_capture)
    LinearLayout mllCapture;
    /**
     * 条形码、二维码提示
     */
    @ViewInject(R.id.tv_capture_type_prompt)
    TextView mtvCaptureTyePrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CameraManager.init(ClinkWorldApplication.mApplication);

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.act = this;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        captureType = getIntent().getIntExtra(QRCODE_TYPE, 1);
        payorderType = getIntent().getIntExtra(PAY_TYPE_CHANNEL, 1);
        initView();
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.capture);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle(R.string.capture_barcode_title);
        titleBar.hideRightButton();
    }

    private void initView() {
        switch (captureType) {
            case 1:
                mbtnInputBarcode.setVisibility(View.VISIBLE);
                mtvCaptureTyePrompt.setText(getString(R.string.bar_code_capture_tips));
                titleBar.setTitle(R.string.capture_barcode_title);
                titleBar.setRightText(R.string.capture_right_title);
                titleBar.setOnRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                titleBar.showRightButton();
                break;
            case 2:
                mbtnInputBarcode.setVisibility(View.GONE);
                mtvCaptureTyePrompt.setText(getString(R.string.qr_code_capture_tips));
                titleBar.setTitle(R.string.capture_qrcode_title);
                break;
            case 3:
                mbtnInputBarcode.setVisibility(View.GONE);
                mtvCaptureTyePrompt.setText(getString(R.string.bar_code_capture_tips));
                titleBar.setTitle(R.string.capture_barcode_title);
                break;
            case 4:
                mbtnInputBarcode.setVisibility(View.GONE);
                mtvCaptureTyePrompt.setText(getString(R.string.qr_code_capture_tips));
                titleBar.setTitle(R.string.capture_qrcode_title);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(final Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        switch (captureType) {
            case 1:
                BarcodeInfoPop popup = new BarcodeInfoPop(CaptureActivity.this);
                popup.setBarcodeContent(obj.getText());
                popup.setPopupWindowDismissListener(new BarcodeInfoPop.PopupWindowDismissListener() {
                    @Override
                    public void dismiss() {
                        handler = new CaptureActivityHandler(CaptureActivity.this, decodeFormats, characterSet);
                    }
                });
                popup.show(mllCapture, true);
                break;
            case 2:
                /** 扫到付款码 TODO:支付 */
                Intent intentPayOrder = new Intent(CaptureActivity.this, PayOrderActivity.class);
                intentPayOrder.putExtra(PayOrderActivity.ORDER_PAY_QRCODE_TYPE, payorderType);
                intentPayOrder.putExtra(PayOrderActivity.ORDER_CAPTURE_QRCODE, obj.getText());
                setResult(RESULT_OK, intentPayOrder);
                finish();
                break;
            case 3:
                Intent intent = new Intent(CaptureActivity.this, AddProductActivity.class);
                intent.putExtra(AddProductActivity.CAPTURE_BARCODE, obj.getText());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case 4:
                Intent intentConpous = new Intent(CaptureActivity.this, ScanGoodsActivity.class);
                intentConpous.putExtra(ScanGoodsActivity.CAPTURE_QRCODE, obj.getText());
                setResult(RESULT_OK, intentConpous);
                finish();
                break;
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @OnClick(R.id.btn_input_barcode)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_input_barcode:
                BarcodeInfoPop popup = new BarcodeInfoPop(CaptureActivity.this);
                popup.show(mllCapture, false);
                break;
        }
    }

}