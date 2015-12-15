package com.clinkworld.pay.util;

import android.content.Context;

public class PreferencesManager extends BasePerferencesManager {

    private static PreferencesManager instance;

    private static final String LOGIN_PLATFORM = "login_platform";
    private static final String LOGIN_MERCHANT = "login_merchant";
    private static final String LOGIN_USERNAME = "login_username";
    private static final String LOGIN_PASSWORD = "login_password";

    private static final String USER_INFO = "userInfo";


    public static PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    private PreferencesManager(Context context) {
        super(context);
    }

    public String getUserInfo(String defaultUserInfo) {
        return getString(USER_INFO, defaultUserInfo);
    }

    public void setUserInfo(String defaultUserInfo) {
        saveString(USER_INFO, defaultUserInfo);
    }

    public String getLoginPlatform(String defaultPlatform) {
        return getString(LOGIN_PLATFORM, defaultPlatform);
    }

    public void setLoginPlatform(String defaultPlatform) {
        saveString(LOGIN_PLATFORM, defaultPlatform);
    }

    public String getLoginMerchant(String defaultMerchant) {
        return getString(LOGIN_MERCHANT, defaultMerchant);
    }

    public void setLoginMerchant(String defaultMerchant) {
        saveString(LOGIN_MERCHANT, defaultMerchant);
    }

    public String getLoginUsername(String defaultUserName) {
        return getString(LOGIN_USERNAME, defaultUserName);
    }

    public void setLoginUsername(String defaultUserName) {
        saveString(LOGIN_USERNAME, defaultUserName);
    }

    public String getLoginPassword(String defaultPassword) {
        return getString(LOGIN_PASSWORD, defaultPassword);
    }

    public void setLoginPassword(String defaultPassword) {
        saveString(LOGIN_PASSWORD, defaultPassword);
    }

}
