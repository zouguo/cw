package com.clinkworld.pay.wxapi;


import android.app.Activity;
import android.os.Bundle;
import com.clinkworld.pay.util.ToastUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.*;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    public static final String APP_ID = "wxd930ea5d5a258f4f";

    // IWXAPI �ǵ�����app��΢��ͨ�ŵ�openapi�ӿ�
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ͨ��WXAPIFactory��������ȡIWXAPI��ʵ��
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    // ΢�ŷ������󵽵�����Ӧ��ʱ����ص����÷���
    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //����ɹ�
                ToastUtils.showToast(WXEntryActivity.this, "����ɹ�");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //����ȡ��
                ToastUtils.showToast(WXEntryActivity.this, "����ȡ��");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //����ܾ�
                ToastUtils.showToast(WXEntryActivity.this, "����ܾ�");
                break;
        }

    }
}