package com.clinkworld.pay;

import android.app.Application;
import com.clinkworld.pay.entity.UserDataInfo;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by srh on 2015/10/14.
 */
public class ClinkWorldApplication extends Application {

    public static ClinkWorldApplication mApplication;

    /**
     * 用户登录信息
     */
    public static UserDataInfo userDataInfo;

    private String paytypeSetting;

    public static ExecutorService httpHelper = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        httpHelper = Executors.newFixedThreadPool(20);
    }

    public String getPaytypeSetting() {
        return paytypeSetting;
    }

    public void setPaytypeSetting(String paytypeSetting) {
        this.paytypeSetting = paytypeSetting;
    }
}
