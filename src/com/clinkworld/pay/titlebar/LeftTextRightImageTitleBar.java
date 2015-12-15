package com.clinkworld.pay.titlebar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.clinkworld.pay.R;


public class LeftTextRightImageTitleBar extends CoupletTitleBar {

    private Button btnTitle;
    private View viewBack, viewRight;
    private View.OnClickListener onBackClickListener, onImageClickListener, onTitleClickListener;

    public LeftTextRightImageTitleBar(Activity activity) {
        super(activity);
    }

    @Override
    public void onPostActivityLayout() {
        super.onPostActivityLayout();
        // Back
        viewBack = setLeftView(R.layout.titlebar_left_text_btn);
        // Title
        btnTitle = setMiddleView(R.layout.titlebar_title_btn);
        //btnTitle = setMiddleView(R.layout.titlebar_title_image_btn);
        // Image
        viewRight = setRightView(R.layout.titlebar_right_image_btn);
    }

    @Override
    void onLeftViewClick(View view) {
    	if (onBackClickListener != null) {
            onBackClickListener.onClick(view);
        } else {
            mActivity.onBackPressed();
        }
    }

    @Override
    void onMiddleViewClick(View view) {
        if (onTitleClickListener != null) {
            onTitleClickListener.onClick(view);
        }
    }

    @Override
    void onRightViewClick(View view) {
        if (onImageClickListener != null) {
            onImageClickListener.onClick(view);
        }
    }

    public void setCountText(int resId) {
        if (viewBack == null) {
            throw new RuntimeException("Back Button View haven't been infalte yet!");
        } else {
            ((TextView) viewBack.findViewById(R.id.count)).setText(resId);
        }
    }

    public void setCountText(CharSequence text) {
        if (viewBack == null) {
            throw new RuntimeException("Back Button View haven't been infalte yet!");
        } else {
            ((TextView) viewBack.findViewById(R.id.count)).setText(text);
        }
    }
    
    public void hideCountText() {
        if (viewBack == null) {
            throw new RuntimeException("Back Button View haven't been infalte yet!");
        } else if (viewBack.findViewById(R.id.count).getVisibility() != View.INVISIBLE) {
            viewBack.findViewById(R.id.count).setVisibility(View.INVISIBLE);
        }
    }

    public void showCountText() {
        if (viewBack == null) {
            throw new RuntimeException("Back Button View haven't been infalte yet!");
        } else if (viewBack.findViewById(R.id.count).getVisibility() != View.VISIBLE) {
            viewBack.findViewById(R.id.count).setVisibility(View.VISIBLE);
        }
    }

    public void hideBackButton() {
        if (viewBack == null) {
            throw new RuntimeException("Back Button View haven't been infalte yet!");
        } else if (viewBack.getVisibility() != View.INVISIBLE) {
            viewBack.setVisibility(View.INVISIBLE);
        }
    }

    public void showBackButton() {
        if (viewBack == null) {
            throw new RuntimeException("Back Button View haven't been infalte yet!");
        } else if (viewBack.getVisibility() != View.VISIBLE) {
            viewBack.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(int resId) {
        if (btnTitle == null) {
            throw new RuntimeException("Middle Title View haven't been infalte yet!");
        } else {
            btnTitle.setText(resId);
        }
    }

    public void setTitle(CharSequence title) {
        if (btnTitle == null) {
            throw new RuntimeException("Middle Title View haven't been infalte yet!");
        } else {
            btnTitle.setText(title);
        }
    }

    public void setRightImage(int resId) {
        if (viewRight == null) {
            throw new RuntimeException("Right Image Button haven't been infalte yet!");
        } else {
            ((ImageButton) viewRight.findViewById(R.id.right_btn)).setImageResource(resId);
        }
    }

    public void setRightImage(Drawable drawable) {
        if (viewRight == null) {
            throw new RuntimeException("Right Image Button haven't been infalte yet!");
        } else {
            ((ImageButton) viewRight.findViewById(R.id.right_btn)).setImageDrawable(drawable);
        }
    }

    public void hideImageButton() {
        if (viewRight == null) {
            throw new RuntimeException("Right Image Button haven't been infalte yet!");
        } else if (viewRight.getVisibility() != View.INVISIBLE) {
            viewRight.setVisibility(View.INVISIBLE);
        }
    }

    public void realHideImageButton() {
        if (viewRight == null) {
            throw new RuntimeException("Right Image Button haven't been infalte yet!");
        } else {
            viewRight.setVisibility(View.GONE);
        }
    }

    public void showImageButton() {
        if (viewRight == null) {
            throw new RuntimeException("Right Image Button haven't been infalte yet!");
        } else if (viewRight.getVisibility() != View.VISIBLE) {
            viewRight.setVisibility(View.VISIBLE);
        }
    }
    
    public void setLeftText(CharSequence text) {
        if (viewBack == null) {
            throw new RuntimeException("Left Text TextView haven't been infalte yet!");
        } else {
            ((TextView) viewBack.findViewById(R.id.tv_left)).setText(text);
        }
    }

    public void setOnBackClickListener(View.OnClickListener listener) {
        this.onBackClickListener = listener;
    }

    public void setOnImageClickListener(View.OnClickListener listener) {
        this.onImageClickListener = listener;
    }

    public void setOnTitleClickListener(View.OnClickListener listener) {
        this.onTitleClickListener = listener;
    }

    public void setRightEnabled(boolean enabled) {
        if (viewRight == null) {
            throw new RuntimeException("Right Image Button haven't been infalte yet!");
        } else {
            viewRight.setEnabled(enabled);
        }
    }

    /**
     * 获取返回按钮
     */
    public View getViewBack() {
        if (viewBack == null) {
            return null;
        }
        if (viewBack.getVisibility() == View.VISIBLE) {
            return viewBack;
        }
        return null;
    }
}
