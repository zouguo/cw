package com.clinkworld.pay.titlebar;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.clinkworld.pay.R;


public class LeftBackRightTextTitleBar extends CoupletTitleBar {

    private Button btnTitle;
    private View viewBack, viewRight;
    private View.OnClickListener onBackClickListener, onRightClickListener, onTitleClickListener;

    public LeftBackRightTextTitleBar(Activity activity) {
        super(activity);
    }

    @Override
    public void onPostActivityLayout() {
        super.onPostActivityLayout();
        // Back
        viewBack = setLeftView(R.layout.titlebar_back_view);
        // Title
        btnTitle = setMiddleView(R.layout.titlebar_title_btn);
        // Right
        viewRight = setRightView(R.layout.titlebar_right_text_btn);
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
        if (onRightClickListener != null) {
            onRightClickListener.onClick(view);
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

    public void setRightText(int resId) {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else {
            ((Button) viewRight.findViewById(R.id.right_btn)).setText(resId);
        }
    }

    public void setRightText(CharSequence text) {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else {
            ((Button) viewRight.findViewById(R.id.right_btn)).setText(text);
        }
    }
    public void setRightTextColor(int color)
    {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else {
            ((Button) viewRight.findViewById(R.id.right_btn)).setTextColor(color);
        }
    }

    public void setRightTextColor(ColorStateList color)
    {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else {
            ((Button) viewRight.findViewById(R.id.right_btn)).setTextColor(color);
        }
    }

    public void hideRightButton() {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else if (viewRight.getVisibility() != View.INVISIBLE) {
            viewRight.setVisibility(View.INVISIBLE);
        }
    }

    public void realHideRightButton() {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else if (viewRight.getVisibility() != View.GONE) {
            viewRight.setVisibility(View.GONE);
        }
    }

    public void showRightButton() {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else if (viewRight.getVisibility() != View.VISIBLE) {
            viewRight.setVisibility(View.VISIBLE);
        }
    }

    public void setOnBackClickListener(View.OnClickListener listener) {
        this.onBackClickListener = listener;
    }

    public void setOnRightClickListener(View.OnClickListener listener) {
        this.onRightClickListener = listener;
    }

    public void setOnTitleClickListener(View.OnClickListener listener) {
        this.onTitleClickListener = listener;
    }

    public void setRightEnabled(boolean enabled) {
        if (viewRight == null) {
            throw new RuntimeException("Right Text Button haven't been infalte yet!");
        } else {
            viewRight.findViewById(R.id.right_btn).setEnabled(enabled);
        }
    }

    public void setBackImage(int resId) {
        if (viewBack == null) {
            throw new RuntimeException("Back Button View haven't been infalte yet!");
        } else {
            ((ImageView) viewBack.findViewById(R.id.back)).setImageResource(resId);
        }
    }

    public void setBackImage(Drawable drawable) {
        if (viewBack == null) {
            throw new RuntimeException("Back Button View haven't been infalte yet!");
        } else {
            ((ImageView) viewBack.findViewById(R.id.back)).setImageDrawable(drawable);
        }
    }
}
