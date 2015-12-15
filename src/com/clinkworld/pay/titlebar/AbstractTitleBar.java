package com.clinkworld.pay.titlebar;

import android.R;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import com.clinkworld.pay.util.ReflactUtils;


public abstract class AbstractTitleBar {

    protected final Activity mActivity;
    private int mLayoutRes;
    private View mLayout;
    private boolean isTitleBarShowing = true;

    public AbstractTitleBar(Activity activity) {
        mActivity = activity;
    }

    /**
     * Set layout of the title bar.
     * Must be called before onPostActivityLayout()
     */
    protected void setLayout(int layoutRes) {
        mLayoutRes = layoutRes;
    }

    /**
     * Must be called before Activity.onCreate()
     */
    public void onRreActivityLayout() {
        mActivity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    }

    /**
     * Must be called after Activity.onCreate()
     */
    public void onPostActivityLayout() {
        if (mLayoutRes == 0) {
            throw new RuntimeException("Please call method 'setLayout(int layout)' before onPostActivityLayout()!");
        } else {
            mActivity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, mLayoutRes);
            mLayout = mActivity.findViewById(R.id.title);
            if (mLayout == null) {
                throw new RuntimeException("The resource id of the layout must be 'android.R.id.title'!");
            }
        }
    }

    public <T extends View> T getViewById(int resId) {
        return (T) mLayout.findViewById(resId);
    }

    public boolean isTitleBarShowing() {
        return isTitleBarShowing;
    }

    public void setTilteBarShowing(boolean showing) {
        View titleContainer = getTitleContainer();
        if (showing != isTitleBarShowing && titleContainer != null) {
            if (showing) {
                titleContainer.setVisibility(View.VISIBLE);
            } else {
                titleContainer.setVisibility(View.GONE);
            }
            isTitleBarShowing = showing;
        }
    }

    public View getTitleContainer() {
        Object obj = ReflactUtils.reflactFiled("com.android.internal.R$id", "title_container");
        if (obj != null && obj instanceof Integer) {
            return mActivity.findViewById((Integer) obj);
        } else {
            return null;
        }
    }
}
