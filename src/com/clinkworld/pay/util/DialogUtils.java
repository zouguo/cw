package com.clinkworld.pay.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.clinkworld.pay.R;
import com.clinkworld.pay.views.CustomDialog;


public class DialogUtils {
    public static void showMsgDialog(Activity activity, String titleMsg, String message) {
        showMsgDialog(activity, titleMsg, message, null);
    }

    public static CustomDialog showMsgDialog(Activity activity, String titleMsg, String message, DialogInterface.OnClickListener listener) {
        CustomDialog customDialog = null;
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        if (!TextUtils.isEmpty(titleMsg)) {
            builder.setTitle(titleMsg);
        }
        builder.setMessage(message);
        if (null == listener) {
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        builder.setPositiveButton("我知道了", listener);
        if (!activity.isFinishing()) {
            customDialog = builder.create();
            customDialog.show();
        }
        return customDialog;
    }


    public static CustomDialog getShowMsgingDialog(Activity activity, String message) {
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }

    public static Dialog getLoadingDialog(Activity activity, String message) {
        final Dialog dialog = new Dialog(activity, R.style.loadingDialog);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCanceledOnTouchOutside(false);
        ImageView animationIV = (ImageView) dialog.findViewById(R.id.iv_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) animationIV.getDrawable();
        animationDrawable.start();
        TextView msg = (TextView) dialog.findViewById(R.id.tv_loading);
        msg.setText(message);
        return dialog;
    }

    /***/
    public static interface CommitClickListener {
        public void okCommit();
    }


}
