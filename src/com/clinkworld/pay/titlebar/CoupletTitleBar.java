package com.clinkworld.pay.titlebar;

import android.app.Activity;
import android.view.View;
import android.view.ViewStub;
import com.clinkworld.pay.R;

abstract class CoupletTitleBar extends AbstractTitleBar implements View.OnClickListener {

    protected View leftView, rightView, middleView;
    private ViewStub leftViewStub, rightViewStub, middleViewStub;

    public CoupletTitleBar(Activity activity) {
        super(activity);
        setLayout(R.layout.couplet_titlebar);
    }

    public void onPostActivityLayout() {
        super.onPostActivityLayout();

        middleViewStub = getViewById(R.id.middleViewStub);
        leftViewStub = getViewById(R.id.leftViewStub);
        rightViewStub = getViewById(R.id.rightViewStub);
    }

    protected <T extends View> T setLeftView(int viewLayoutRes) {
        leftViewStub.setLayoutResource(viewLayoutRes);
        leftView = leftViewStub.inflate();
        leftView.setOnClickListener(this);
        return (T) leftView;
    }

    protected <T extends View> T getLeftView() {
        if (leftView == null) {
            return null;
        } else {
            return (T) leftView;
        }
    }

    protected <T extends View> T setRightView(int viewLayoutRes) {
        rightViewStub.setLayoutResource(viewLayoutRes);
        rightView = rightViewStub.inflate();
        rightView.findViewById(R.id.right_btn).setOnClickListener(this);
        return (T) rightView;
    }

    protected <T extends View> T getRightView() {
        if (rightView == null) {
            return null;
        } else {
            return (T) rightView;
        }
    }

    protected <T extends View> T setMiddleView(int viewLayoutRes) {
        middleViewStub.setLayoutResource(viewLayoutRes);
        middleView = middleViewStub.inflate();
        middleView.setOnClickListener(this);
        return (T) middleView;
    }

    protected <T extends View> T getMiddleView() {
        if (middleView == null) {
            return null;
        } else {
            return (T) middleView;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == leftView.getId()) {
            onLeftViewClick(view);
        } else if (view.getId() == rightView.findViewById(R.id.right_btn).getId()) {
            onRightViewClick(view);
        } else if (view.getId() == middleView.getId()) {
            onMiddleViewClick(view);
        }
    }

    abstract void onLeftViewClick(View view);

    abstract void onMiddleViewClick(View view);

    abstract void onRightViewClick(View view);
}
