package com.clinkworld.pay.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.lidroid.xutils.ViewUtils;

/**
 * Created by shirenhua on 2015/10/15.
 */
public class BaseActivity extends Activity {

    public Context instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addCurrentLayout();
        ViewUtils.inject(this);
        instance = this;
    }

    /**
     * 子类实现
     * <p/>
     * 用来加载布局文件（setcontentView()）
     */
    public void addCurrentLayout() {

    }


}
