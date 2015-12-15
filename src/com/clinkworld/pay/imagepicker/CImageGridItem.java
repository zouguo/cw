package com.clinkworld.pay.imagepicker;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.clinkworld.pay.R;
import com.clinkworld.pay.util.UiUtils;

public class CImageGridItem extends RelativeLayout implements View.OnClickListener {

    private ImageView mImaggeView = null;
    private int position;
    private Uri imageUri;
    private long imageId;
    private OnGridClickListener listener;
    private int imageSide;
    private AsyncTask bitmapBindTask;

    public CImageGridItem(Context context) {
        this(context, null, 0);
    }

    public CImageGridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CImageGridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        imageSide = UiUtils.dp2px(context, 48);
        LayoutInflater.from(context).inflate(R.layout.cimagelist_griditem, this);
        mImaggeView = (ImageView) findViewById(R.id.image);
        mImaggeView.setOnClickListener(this);
    }

    public void setData(int position, long imageId, Uri imageUri) {
        this.position = position;
        this.imageUri = imageUri;
        this.imageId = imageId;
        this.mImaggeView.setImageResource(R.color.top_dialog_bg_color);
        if (this.bitmapBindTask != null) {
            this.bitmapBindTask.cancel(true);
        }
        bitmapBindTask = BitmapLoadManager.load(mImaggeView, imageUri, imageSide, imageSide);
    }

    public void setListener(OnGridClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v, position, imageUri);
        }
    }

    public interface OnGridClickListener {
        void onClick(View imageView, int position, Uri imageUri);
    }
}
