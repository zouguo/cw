package com.clinkworld.pay.titlebar;


import android.app.Activity;
import android.view.View;
import android.widget.Button;
import com.clinkworld.pay.R;

public class LeftLogoRightTextTitleBar extends CoupletTitleBar {

    private Button btnTitle;
    private View viewLogo, viewRight;

    public LeftLogoRightTextTitleBar(Activity activity) {
        super(activity);
    }

    @Override
    public void onPostActivityLayout() {
        super.onPostActivityLayout();

        // Logo
        viewLogo = setLeftView(R.layout.titlebar_logo_view);
        // Title
        btnTitle = setMiddleView(R.layout.titlebar_title_btn);
        // Right
        viewRight = setRightView(R.layout.titlebar_right_text_btn);

    }

    @Override
    void onLeftViewClick(View view) {

    }

    @Override
    void onMiddleViewClick(View view) {

    }

    @Override
    void onRightViewClick(View view) {

    }

    public void setTitle(int resId) {
        if (btnTitle == null) {
            throw new RuntimeException("Middle Title View haven't been infalte yet!");
        } else {
            btnTitle.setText(resId);
        }
    }

    public void hideRightButton() {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else if (viewRight.getVisibility() != View.INVISIBLE) {
            viewRight.setVisibility(View.INVISIBLE);
        }
    }

    public void showRightButton() {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else if (viewRight.getVisibility() != View.VISIBLE) {
            viewRight.setVisibility(View.VISIBLE);
        }
    }
}

