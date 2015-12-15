package com.clinkworld.pay.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.clinkworld.pay.R;
import com.clinkworld.pay.activity.AddProductActivity;


public class ChoosePictureDialog extends Dialog {

    private Context context;
    private Button cancelBtn;
    private Button cameraBtn;
    private Button photoFolderBtn;

    public ChoosePictureDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_picture_type);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(mCancelBtnClickListener);
        cameraBtn = (Button) findViewById(R.id.camera_btn);
        cameraBtn.setOnClickListener(mCameraBtnClickListener);
        photoFolderBtn = (Button) findViewById(R.id.photo_folder_btn);
        photoFolderBtn.setOnClickListener(mPhotoFolderBtnClickListener);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
    }

    View.OnClickListener mCancelBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ChoosePictureDialog.this.dismiss();
        }
    };

    View.OnClickListener mCameraBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (context instanceof AddProductActivity) {
                ((AddProductActivity) (ChoosePictureDialog.this.context)).toCamera();
            }
            ChoosePictureDialog.this.dismiss();
        }
    };

    View.OnClickListener mPhotoFolderBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (context instanceof AddProductActivity) {
                ((AddProductActivity) (ChoosePictureDialog.this.context)).getPhoto();
            }
            ChoosePictureDialog.this.dismiss();
        }
    };
}
