package com.clinkworld.pay.wxapi;


import android.app.Activity;
import android.os.Bundle;
import com.clinkworld.pay.util.ToastUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.*;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    public static final String APP_ID = "wxd930ea5d5a258f4f";

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                ToastUtils.showToast(WXEntryActivity.this, "分享成功");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                ToastUtils.showToast(WXEntryActivity.this, "分享取消");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //分享拒绝
                ToastUtils.showToast(WXEntryActivity.this, "分享拒绝");
                break;
        }

    }
}