package com.clinkworld.pay.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.clinkworld.pay.R;

/**
 * Created by shirenhua on 2015/8/6.
 * 最新对话框
 */
public class CustomDialog extends Dialog {
    private Context mContext;
    public CustomDialog(Context context) {
        super(context);
        mContext = context;
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener, negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public CustomDialog show() {
            CustomDialog dialog = create();
            dialog.show();
            return dialog;
        }

        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final CustomDialog dialog = new CustomDialog(context, R.style.msgDialog);
            dialog.setCanceledOnTouchOutside(false);//android 4.0以上dialog点击其他地方也会消失false以后就只能点击按钮消失
            dialog.setCancelable(false);

            View layout = inflater.inflate(R.layout.dialog_layout, null);
            if (TextUtils.isEmpty(title)) {
                ((TextView) layout.findViewById(R.id.tv_title)).setVisibility(View.GONE);
            } else {
                ((TextView) layout.findViewById(R.id.tv_title)).setVisibility(View.VISIBLE);
                ((TextView) layout.findViewById(R.id.tv_title)).setText(title);
            }
            ((TextView) layout.findViewById(R.id.tv_message)).setText(message);
            if (positiveButtonClickListener != null) {
                ((TextView) layout.findViewById(R.id.tv_ok)).setText(positiveButtonText);
                ((TextView) layout.findViewById(R.id.tv_ok)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }
                });
            } else {
                ((TextView) layout.findViewById(R.id.tv_ok)).setVisibility(View.GONE);
                layout.findViewById(R.id.view_serpate_center).setVisibility(View.GONE);
            }

            if (negativeButtonClickListener != null) {
                ((TextView) layout.findViewById(R.id.tv_cancel)).setText(negativeButtonText);
                ((TextView) layout.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            } else {
                ((TextView) layout.findViewById(R.id.tv_cancel)).setVisibility(View.GONE);
                layout.findViewById(R.id.view_serpate_center).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
