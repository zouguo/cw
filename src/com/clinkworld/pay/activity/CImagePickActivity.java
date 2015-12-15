package com.clinkworld.pay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.clinkworld.pay.R;
import com.clinkworld.pay.imagepicker.BitmapLoadManager;
import com.clinkworld.pay.imagepicker.CImageBucketFragment;
import com.clinkworld.pay.imagepicker.CImageGridFragment;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;


public class CImagePickActivity extends FragmentActivity {

    private LeftBackRightTextTitleBar titleBar;
    Fragment gridFragment;

    public int imageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.activity_fragment_frame);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        BitmapLoadManager.init(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new CImageBucketFragment()).commit();
        }
    }

    public void showBackButton(boolean show) {
        if (titleBar != null) {
            if (show) {
                titleBar.showBackButton();
            } else {
                titleBar.hideBackButton();
            }
        }
    }

    @Override
    protected void onDestroy() {
        BitmapLoadManager.release();
        super.onDestroy();
    }

    @Override
    public void setTitle(int resId) {
        if (titleBar != null) {
            titleBar.setTitle(resId);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (titleBar != null) {
            titleBar.setTitle(title);
        }
    }

    public void openBucket(Bundle args) {
        gridFragment = new CImageGridFragment();
        gridFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, gridFragment).addToBackStack(null).commit();
    }

    public void cancelPick() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void finishPick(Intent data) {
        setResult(RESULT_OK, data);
        finish();
    }
}
