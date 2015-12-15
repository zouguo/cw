package com.clinkworld.pay.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.AccessTokenKeeper;
import com.clinkworld.pay.util.CWLogUtils;
import com.clinkworld.pay.util.Constants;
import com.clinkworld.pay.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.*;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.open.t.Weibo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.json.JSONObject;

/**
 * Created by srh on 2015/11/5.
 * <p/>
 * 链接发券
 */
public class CouponsLinkActivity extends BaseActivity implements IWeiboHandler.Response {

    private LeftBackRightTextTitleBar titleBar;
    public final static String COUPONS_LINK = "coupons_link";
    public final static String WX_APP_ID = "wxce7d8730ac333565";
    Tencent mTencent;
    private IWXAPI wxApi;
    AuthInfo authInfo;
    Oauth2AccessToken mAccessToken;
    SsoHandler mSsoHandler;
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    /**
     * 微博微博分享接口实例
     */
    private IWeiboShareAPI mWeiboShareAPI = null;

    @ViewInject(R.id.tv_link_url)
    TextView mtvLinkUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTencent = Tencent.createInstance("1104876041", this.getApplicationContext());
        //实例化
        wxApi = WXAPIFactory.createWXAPI(this, WX_APP_ID);
        wxApi.registerApp(WX_APP_ID);

        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.WEIBO_KEY);
//        mWeiboShareAPI.registerApp();
        mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        authInfo = new AuthInfo(this, Constants.WEIBO_KEY, Constants.WEIBO_REDIRECT_URL, SCOPE);
//        mSsoHandler = new SsoHandler(CouponsLinkActivity.this, authInfo);
//        mSsoHandler.authorize(new AuthListener());
        initView();
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.coupons_link);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle(R.string.link_coupons_title);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
    }

    private void initView() {
        String linkUrl = getIntent().getStringExtra(COUPONS_LINK);
        if (!TextUtils.isEmpty(linkUrl)) {
            mtvLinkUrl.setText(linkUrl);
        }
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        ToastUtils.showToast(CouponsLinkActivity.this, "分享成功");
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle bundle) {
            String token = bundle.getString("access_token");
            String expires_in = bundle.getString("expires_in");
            mAccessToken = new Oauth2AccessToken(token, expires_in);
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(CouponsLinkActivity.this, mAccessToken);
                ToastUtils.showToast(CouponsLinkActivity.this, "授权成功");
                // TODO
                reqMsg();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ToastUtils.showToast(CouponsLinkActivity.this, "授权失败");
        }

        @Override
        public void onCancel() {
            ToastUtils.showToast(CouponsLinkActivity.this, "取消授权");
        }
    }

    public void reqMsg() {
         /*微博数据的message对象*/
        WeiboMultiMessage multmess = new WeiboMultiMessage();
        TextObject textobj = new TextObject();
        textobj.text = "这是我的测试微博分享消息，大家看的到吗？";

        multmess.textObject = textobj;
    /*微博发送的Request请求*/
        SendMultiMessageToWeiboRequest multRequest = new SendMultiMessageToWeiboRequest();
        multRequest.multiMessage = multmess;
        //以当前时间戳为唯一识别符
        multRequest.transaction = String.valueOf(System.currentTimeMillis());
        mWeiboShareAPI.sendRequest(this, multRequest);
    }

    /**
     * 获取分享的文本模板。
     *
     * @return 分享的文本模板
     */
    private String getSharedText() {
        int formatId = R.string.weibosdk_demo_share_text_template;
        String format = getString(formatId);
        String text = format;
        String demoUrl = getString(R.string.weibosdk_demo_app_url);
        format = getString(R.string.weibosdk_demo_share_text_template);
        return text;
    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = "分享文字";
        return textObject;
    }

    private void sendMultiMessage(boolean hasText) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
        weiboMessage.textObject = getTextObj();
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(CouponsLinkActivity.this);
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
                CWLogUtils.d("f", "d");
            }

            @Override
            public void onComplete(Bundle bundle) {
                CWLogUtils.d("f", "d");
            }

            @Override
            public void onCancel() {
                CWLogUtils.d("f", "d");
            }
        }); //发送请求消息到微博，唤起微博分享界面
    }

    private void wechatShare(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "这里填写链接url";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "这里填写标题";
        msg.description = "这里填写内容";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }


    public void shareQQ() {
        Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString("targetUrl", "http://connect.qq.com/");
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString("title", "我在测试");
        //分享的图片URL
        bundle.putString("imageUrl",
                "http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg");
        //分享的消息摘要，最长50个字
        bundle.putString("summary", "测试");
        //手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
        bundle.putString("appName", "商家运营平台");
        //标识该消息的来源应用，值为应用名称+AppId。
        bundle.putString("appSource", "商品运营平台1104951090");

        mTencent.shareToQQ(this, bundle, new BaseUiListener());
    }

    class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            CWLogUtils.d("d", "");
        }

        @Override
        public void onError(UiError uiError) {
            CWLogUtils.d("d", "");
        }

        @Override
        public void onCancel() {
            CWLogUtils.d("d", "");
        }
    }

    @OnClick({R.id.btn_copy_link_url, R.id.iv_share_weixin, R.id.iv_share_friends, R.id.iv_share_sina, R.id.iv_share_qq})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_copy_link_url:
                /** 复制链接 */
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(mtvLinkUrl.getText().toString().trim());
                ToastUtils.showToast(CouponsLinkActivity.this, "复制成功");
                break;
            case R.id.iv_share_weixin:
                /** 微信分享 */
                wechatShare(0);//分享到微信好友
                break;
            case R.id.iv_share_friends:
                /** 微信朋友圈分享 */
                wechatShare(1);//分享到微信朋友圈
                break;
            case R.id.iv_share_sina:
                /** 新浪分享 */
                mAccessToken = AccessTokenKeeper.readAccessToken(CouponsLinkActivity.this);

                if (mAccessToken.isSessionValid()) {
                    // TODO发微博
                    sendMultiMessage(true);
                } else {
                    /** 不使用SSO方式进行授权验证 */
                    // mWeibo.anthorize(AppMain.this, new AuthDialogListener());

                    /** 使用SSO方式进行授权验证 */
                    mSsoHandler = new SsoHandler(CouponsLinkActivity.this, authInfo);
                    mSsoHandler.authorize(new AuthListener());
                }
                break;
            case R.id.iv_share_qq:
                /** qq分享 */
                shareQQ();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != mTencent)
            mTencent.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

}
