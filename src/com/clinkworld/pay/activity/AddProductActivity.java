package com.clinkworld.pay.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.R;
import com.clinkworld.pay.ServerUrl;
import com.clinkworld.pay.entity.ProductInfo;
import com.clinkworld.pay.entity.StorageDetailInfo;
import com.clinkworld.pay.qrcode.CaptureActivity;
import com.clinkworld.pay.titlebar.LeftBackRightTextTitleBar;
import com.clinkworld.pay.util.*;
import com.clinkworld.pay.views.ChoosePictureDialog;
import com.clinkworld.pay.views.SlideView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srh on 2015/10/30.
 * <p/>
 * 添加商品
 */
public class AddProductActivity extends BaseActivity {

    private LeftBackRightTextTitleBar titleBar;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private ChoosePictureDialog choosePictureDialog;
    public final static int REQUEST_CODE_CAPTURE_BARCODE = 0;
    public final static int RETURN_FROM_CAMERA = 10;
    public final static int RETURN_FROM_PHOTO = 11;
    private final static int MSG_STORAGE_PRODUCT_SUCCESS = 12;
    private final static int MSG_STORAGE_PRODUCT_FAILURE = 13;
    private final static int MSG_UPLOAD_IMAGE_SUCCESS = 14;
    private final static int MSG_UPLOAD_IMAGE_FAILURE = 15;
    public final static int MSG_SEARCH_PRODUCT_SUCCESS = 16;
    public final static int MSG_SEARCH_PRODUCT_FAILURE = 17;
    public final static String CAPTURE_BARCODE = "capture_barcode";
    private Dialog mLoadingDialog;
    private Dialog mUploadImageDialog;
    private Uri pictureUri;
    private List<BarcodeImageInfo> barcodeImageInfos = new ArrayList<BarcodeImageInfo>();
    private List<StorageDetailInfo> storageDetailInfoList = new ArrayList<StorageDetailInfo>();
    /**
     * 手动输入条形码
     */
    @ViewInject(R.id.et_input_barcode)
    EditText metInputBarcode;
    /**
     * 输入商品名称
     */
    @ViewInject(R.id.et_input_product_name)
    EditText metInputProductName;
    /**
     * 输入成本价
     */
    @ViewInject(R.id.et_input_product_purchasing)
    EditText metInputProductPurchasing;
    /**
     * 输入售价
     */
    @ViewInject(R.id.et_input_product_price)
    EditText metInputProductPrice;
    /**
     * 输入数量
     */
    @ViewInject(R.id.et_input_product_number)
    EditText metInputProductNumber;
    /**
     * 添加的商品列表
     */
    @ViewInject(R.id.ll_add_stoarge_product)
    LinearLayout mllAddStorageProduct;
    /**
     * 添加了商品显示的布局
     */
    @ViewInject(R.id.ll_storage_list)
    LinearLayout mllStorage;
    /**
     * 总的添加商品数量
     */
    @ViewInject(R.id.tv_total_number)
    TextView mtvTotalNumber;
    /**
     * 合计总成本
     */
    @ViewInject(R.id.tv_total_cost)
    TextView mtvTotalCost;
    /**
     * 上传成功的图片显示
     */
    @ViewInject(R.id.iv_upload_picture)
    ImageView mivUploadPicture;

    SafeHandler safeHandler = new SafeHandler(AddProductActivity.this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STORAGE_PRODUCT_SUCCESS:
                    /** 入库商品成功 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    ToastUtils.showToast(AddProductActivity.this, "入库商品成功");
                    Intent intent = new Intent(StorageListActivity.BROADCAST_UPDATE_STORAGE_ACTION);
                    sendBroadcast(intent, StorageListActivity.UPDATE_STORAGE_PERMISSION);
                    finish();
                    break;
                case MSG_STORAGE_PRODUCT_FAILURE:
                    /** 入库商品失败 */
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    String errorMessage = (String) msg.obj;
                    if (!TextUtils.isEmpty(errorMessage)) {
                        ToastUtils.showToast(AddProductActivity.this, errorMessage);
                    } else {
                        ToastUtils.showToast(AddProductActivity.this, getString(R.string.reg_httpclient_fail));
                    }
                    break;
                case MSG_UPLOAD_IMAGE_SUCCESS:
                    /** 商品图片上传成功 */
                    if (mUploadImageDialog != null) {
                        mUploadImageDialog.dismiss();
                    }
                    String uploadImageUrl = (String) msg.obj;
                    ImageLoader.getInstance().displayImage(uploadImageUrl, mivUploadPicture);
                    ToastUtils.showToast(AddProductActivity.this, "商品图片上传成功");
                    break;
                case MSG_UPLOAD_IMAGE_FAILURE:
                    if (mUploadImageDialog != null) {
                        mUploadImageDialog.dismiss();
                    }
                    String uploadErrorMessage = (String) msg.obj;
                    if (TextUtils.isEmpty(uploadErrorMessage)) {
                        ToastUtils.showToast(AddProductActivity.this, getString(R.string.reg_httpclient_fail));
                    } else {
                        ToastUtils.showToast(AddProductActivity.this, uploadErrorMessage);
                    }
                    break;
                case MSG_SEARCH_PRODUCT_SUCCESS:
                    ProductInfo productInfo = (ProductInfo) msg.obj;
                    metInputProductName.setText(productInfo.getName());
                    metInputProductPrice.setText(String.valueOf(productInfo.getPrice()));
                    break;
                case MSG_SEARCH_PRODUCT_FAILURE:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        ImageLoader.getInstance().init(
                ImageUtils.getSimpleImageLoaderConfig(getApplicationContext()));
    }

    @Override
    public void addCurrentLayout() {
        titleBar = new LeftBackRightTextTitleBar(this);
        titleBar.onRreActivityLayout();
        setContentView(R.layout.add_product);
        titleBar.onPostActivityLayout();
        titleBar.hideRightButton();
        titleBar.setTitle(R.string.add_product_title);
    }

    private void initView() {
        mLoadingDialog = DialogUtils.getLoadingDialog(AddProductActivity.this, "商品正在入库...");
        mUploadImageDialog = DialogUtils.getLoadingDialog(AddProductActivity.this, "商品图片上传...");
        choosePictureDialog = new ChoosePictureDialog(AddProductActivity.this, R.style.DownToUpSlideDialog);
        metInputProductNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(metInputProductNumber.getText().toString())) {
                        metInputProductNumber.setText("1");
                    }
                }
            }
        });

        metInputBarcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchProductBarcode();
                }
                return false;
            }
        });
        metInputProductNumber.setSelection(metInputProductNumber.length());
    }

    private void searchProductBarcode() {
        if (TextUtils.isEmpty(metInputBarcode.getText().toString())) {
            ToastUtils.showToast(AddProductActivity.this, "请输入商品条形码");
            return;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(AddProductActivity.this.getCurrentFocus()
                                .getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        ClinkWorldApplication.httpHelper.execute(new SearchProductRunnable(metInputBarcode.getText().toString()));
    }

    @OnClick({R.id.ll_capture_barcode, R.id.btn_add, R.id.btn_reduce, R.id.btn_add_product, R.id.ll_upload_product_image, R.id.btn_complete_storage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_capture_barcode:
                /** 条形码扫描 */
                Intent barCaptureIntent = new Intent(AddProductActivity.this, CaptureActivity.class);
                barCaptureIntent.putExtra(CaptureActivity.QRCODE_TYPE, 3);
                startActivityForResult(barCaptureIntent, REQUEST_CODE_CAPTURE_BARCODE);
                break;
            case R.id.btn_add:
                /** 增加商品数量 */
                if (TextUtils.isEmpty(metInputProductNumber.getText().toString())) {
                    metInputProductNumber.setText("1");
                } else {
                    int numberAdd = Integer.parseInt(metInputProductNumber.getText().toString());
                    metInputProductNumber.setText(String.valueOf(numberAdd + 1));
                }
                break;
            case R.id.btn_reduce:
                /** 减少商品数量 */
                int numberReduce = Integer.parseInt(metInputProductNumber.getText().toString());
                if (numberReduce == 1) {
                    metInputProductNumber.setText("1");
                } else {
                    metInputProductNumber.setText(String.valueOf(numberReduce - 1));
                }
                break;
            case R.id.btn_add_product:
                /** 添加商品 */
                addStorageProduct();
                break;
            case R.id.ll_upload_product_image:
                /** 上传图片选择 */
                if (TextUtils.isEmpty(metInputBarcode.getText().toString())) {
                    ToastUtils.showToast(AddProductActivity.this, "请输入商品条形码");
                    return;
                }
                if (choosePictureDialog != null) {
                    choosePictureDialog.show();
                }
                break;
            case R.id.btn_complete_storage:
                /** 商品入库 */
                storageProduct();
                break;
        }
    }

    private void storageProduct() {
        if (mLoadingDialog != null) {
            mLoadingDialog.show();
        }
        ClinkWorldApplication.httpHelper.execute(new StorageProductRunnable(storageDetailInfoList));
    }

    class StorageProductRunnable implements Runnable {

        private List<StorageDetailInfo> storageDetailInfos;

        public StorageProductRunnable(List<StorageDetailInfo> storageDetailInfos) {
            this.storageDetailInfos = storageDetailInfos;
        }

        @Override
        public void run() {
            if (storageDetailInfos == null) {
                return;
            }
            Map<String, String> params = new HashMap<String, String>();
            for (int i = 0; i < storageDetailInfos.size(); i++) {
                params.put("data[" + i + "][barcode]", storageDetailInfos.get(i).getProductBarcode());
                params.put("data[" + i + "][name]", storageDetailInfos.get(i).getProductProductName());
                params.put("data[" + i + "][number]", String.valueOf(storageDetailInfos.get(i).getProductNumber()));
                params.put("data[" + i + "][cost_price]", String.valueOf(storageDetailInfos.get(i).getProductPriceIn()));
                params.put("data[" + i + "][selling_price]", String.valueOf(storageDetailInfos.get(i).getProductPriceOut()));
                params.put("data[" + i + "][image_url]", String.valueOf(storageDetailInfos.get(i).getImageUrl()));
            }
            String url = ServerUrl.BASE_URL + ServerUrl.INCOME_BATCH_SUBMIT_PATH;
            String result = HttpClientC.post(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_STORAGE_PRODUCT_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_STORAGE_PRODUCT_SUCCESS;
                    } else {
                        message.what = MSG_STORAGE_PRODUCT_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_STORAGE_PRODUCT_FAILURE);
                }
            }
        }
    }

    private void addStorageProduct() {
        StorageDetailInfo storageDetailInfo = new StorageDetailInfo();
        if (TextUtils.isEmpty(metInputBarcode.getText().toString())) {
            ToastUtils.showToast(AddProductActivity.this, "请输入商品条形码");
            return;
        } else {
            storageDetailInfo.setProductBarcode(metInputBarcode.getText().toString());
        }

        if (TextUtils.isEmpty(metInputProductName.getText().toString())) {
            ToastUtils.showToast(AddProductActivity.this, "请输入商品名称");
            return;
        } else {
            storageDetailInfo.setProductProductName(metInputProductName.getText().toString());
        }

        if (TextUtils.isEmpty(metInputProductPurchasing.getText().toString())) {
            ToastUtils.showToast(AddProductActivity.this, "请输入商品成本价");
            return;
        } else {
            storageDetailInfo.setProductPriceIn(Float.parseFloat(metInputProductPurchasing.getText().toString()));
        }

        if (TextUtils.isEmpty(metInputProductPrice.getText().toString())) {
            ToastUtils.showToast(AddProductActivity.this, "请输入商品售价");
            return;
        } else {
            storageDetailInfo.setProductPriceOut(Float.parseFloat(metInputProductPrice.getText().toString()));
        }

        if (TextUtils.isEmpty(metInputProductNumber.getText().toString()) || Integer.parseInt(metInputProductNumber.getText().toString()) == 0) {
            ToastUtils.showToast(AddProductActivity.this, "请输入商品数量");
            return;
        } else {
            storageDetailInfo.setProductNumber(Integer.parseInt(metInputProductNumber.getText().toString()));
        }
        metInputBarcode.setText("");
        metInputProductName.setText("");
        metInputProductPurchasing.setText("");
        metInputProductPrice.setText("");
        metInputProductNumber.setText("1");
        mivUploadPicture.setImageBitmap(null);
        boolean hasSameProduct = false;
        for (StorageDetailInfo item : storageDetailInfoList) {
            if (storageDetailInfo.getProductBarcode().equals(item.getProductBarcode())) {
                int number = item.getProductNumber();
                item.setProductNumber(number + storageDetailInfo.getProductNumber());
                hasSameProduct = true;
                break;
            }
        }
        if (!hasSameProduct) {
            storageDetailInfoList.add(storageDetailInfo);
        }
        refreshStorageList(storageDetailInfoList);
        mllStorage.setVisibility(View.VISIBLE);
    }

    public void toCamera() {
        pictureUri = ImageUtils.getOutputMediaFileUri();
        if (pictureUri != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
            startActivityForResult(intent, RETURN_FROM_CAMERA);
        } else {
            Toast.makeText(this, R.string.image_no_save_path, Toast.LENGTH_SHORT).show();
        }
    }

    public void getPhoto() {
        Intent intent = new Intent("android.intent.action.CPICK");
        intent.setPackage(this.getPackageName());
        intent.setType("image/*");
        intent.putExtra("imageNum", 1);
        startActivityForResult(intent, RETURN_FROM_PHOTO);
    }

    private void refreshStorageList(final List<StorageDetailInfo> storageDetailInfos) {
        if (storageDetailInfos != null) {
            mllAddStorageProduct.removeAllViews();
            if (storageDetailInfos.size() == 0) {
                mllStorage.setVisibility(View.GONE);
                return;
            }
            int totalNumber = 0;
            float totalCostAll = 0f;
            for (int i = 0; i < storageDetailInfos.size(); i++) {
                final int position = i;
                View view = LayoutInflater.from(this).inflate(R.layout.add_storage_product_item, null);
                TextView mtvProductName = (TextView) view.findViewById(R.id.item_product_name);
                TextView mtvProductCost = (TextView) view.findViewById(R.id.item_product_cost);
                TextView mtvProductSell = (TextView) view.findViewById(R.id.item_product_sell);
                TextView mtvProductNumeber = (TextView) view.findViewById(R.id.item_product_number);
                TextView mtvProductCostAll = (TextView) view.findViewById(R.id.item_product_cost_all);
                ImageView mivProduct = (ImageView) view.findViewById(R.id.product_icon);

                final StorageDetailInfo storageDetailInfo = storageDetailInfos.get(position);
                mtvProductName.setText(storageDetailInfo.getProductProductName());
                mtvProductCost.setText("￥" + decimalFormat.format(storageDetailInfo.getProductPriceIn()));
                mtvProductSell.setText("￥" + decimalFormat.format(storageDetailInfo.getProductPriceOut()));
                mtvProductNumeber.setText(String.valueOf(storageDetailInfo.getProductNumber()));
                float costAll = storageDetailInfo.getProductPriceIn() * storageDetailInfo.getProductNumber();
                mtvProductCostAll.setText("￥" + decimalFormat.format(storageDetailInfo.getProductPriceIn() * storageDetailInfo.getProductNumber()));

//                DisplayImageOptions options =
//                        new DisplayImageOptions.Builder()
//                                .showStubImage(R.drawable.icon_default)
//                                .showImageForEmptyUri(R.drawable.icon_default)
//                                .showImageOnFail(R.drawable.icon_default)
//                                .cacheInMemory(true).cacheOnDisc()
//                                .bitmapConfig(Bitmap.Config.RGB_565)
//                                .imageScaleType(ImageScaleType.EXACTLY).build();
                for (BarcodeImageInfo item : barcodeImageInfos) {
                    if (item.getBarcode().equals(storageDetailInfo.getProductBarcode())) {
                        storageDetailInfo.setImageUrl(item.getProductUrl());
                        ImageLoader.getInstance().displayImage(item.getProductUrl(), mivProduct);
                        break;
                    }
                }


                totalNumber += storageDetailInfo.getProductNumber();
                totalCostAll += costAll;

                SlideView slideView = new SlideView(this);
                slideView.setContentView(view);
                slideView.setmBtnDeleteListener(new SlideView.OnBtnDeleteListener() {
                    @Override
                    public void onClick() {
                        /** 删除当前条目 */
                        storageDetailInfos.remove(position);
                        refreshStorageList(storageDetailInfos);
                    }
                });
                slideView.setOnSlideListener(new SlideView.OnSlideListener() {
                    @Override
                    public void onSlide(View view, int status) {
                        if (status == SLIDE_STATUS_START_SCROLL) {
                            view.setBackgroundColor(Color.parseColor("#F6F6F6"));
                        } else if (status == SLIDE_STATUS_OFF) {
                            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                });

                mllAddStorageProduct.addView(slideView);
                if (i != storageDetailInfos.size() - 1) {
                    View separateView = new View(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UiUtils.dp2px(this, 1));
                    separateView.setBackgroundResource(R.drawable.dotted_line);
                    mllAddStorageProduct.addView(separateView, params);
                }
            }
            mtvTotalNumber.setText("共计" + totalNumber + "件，合计成本");
            mtvTotalCost.setText("￥" + decimalFormat.format(totalCostAll));
        }
    }

    class BarcodeImageInfo {
        private String barcode;
        private String productUrl;

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public String getProductUrl() {
            return productUrl;
        }

        public void setProductUrl(String productUrl) {
            this.productUrl = productUrl;
        }
    }

    class SearchProductRunnable implements Runnable {

        private String mProductBarCode;

        public SearchProductRunnable(String productBarCode) {
            this.mProductBarCode = productBarCode;
        }

        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            String url = ServerUrl.BASE_URL + ServerUrl.PRODUCT_INIFO_PATH + mProductBarCode;
            String result = HttpClientC.getHttpUrlWithParams(url, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_SEARCH_PRODUCT_FAILURE);
            } else {
                try {
                    JSONObject resultJsonObject = new JSONObject(result);
                    int status = resultJsonObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_SEARCH_PRODUCT_SUCCESS;
                        JSONObject dataJSONObject = resultJsonObject.getJSONObject("data");
                        ProductInfo productInfo = new ProductInfo();
                        productInfo.setProductBarCode(mProductBarCode);
                        productInfo.setName(dataJSONObject.optString("name"));
                        productInfo.setPrice(Float.valueOf(dataJSONObject.optString("selling_price")));
                        message.obj = productInfo;
                    } else {
                        message.what = MSG_SEARCH_PRODUCT_FAILURE;
                        message.obj = resultJsonObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    safeHandler.sendEmptyMessage(MSG_SEARCH_PRODUCT_FAILURE);
                }
            }
        }
    }


    class UploadFileRunnable implements Runnable {

        private Uri imageUri;
        private String barcode;

        public UploadFileRunnable(Uri uri, String barcode) {
            this.imageUri = uri;
            this.barcode = barcode;
        }

        @Override
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("barcode", barcode);
            String url = ServerUrl.BASE_URL + ServerUrl.INCOME_UPLOAD_IAMGE_FILE;
            String result = HttpClientC.postFile(url, imageUri, params);
            if (TextUtils.isEmpty(result) || HttpClientC.HTTP_CLIENT_FAIL.equals(result)) {
                safeHandler.sendEmptyMessage(MSG_UPLOAD_IMAGE_FAILURE);
            } else {
                try {
                    JSONObject resultJSONObject = new JSONObject(result);
                    int status = resultJSONObject.optInt("status");
                    Message message = new Message();
                    if (status == 200) {
                        message.what = MSG_UPLOAD_IMAGE_SUCCESS;
                        BarcodeImageInfo barcodeImageInfo = new BarcodeImageInfo();
                        barcodeImageInfo.setBarcode(barcode);
                        barcodeImageInfo.setProductUrl(resultJSONObject.optString("data"));
                        barcodeImageInfos.add(barcodeImageInfo);
                        message.obj = resultJSONObject.optString("data");
                    } else {
                        message.what = MSG_UPLOAD_IMAGE_FAILURE;
                        message.obj = resultJSONObject.optString("info");
                    }
                    safeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_BARCODE:
                if (resultCode == Activity.RESULT_OK) {
                    String barcode = data.getStringExtra(CAPTURE_BARCODE);
                    metInputBarcode.setText(barcode);
                    metInputProductName.setText("");
                    metInputProductPurchasing.setText("");
                    metInputProductPrice.setText("");
                    searchProductBarcode();
                }
                break;
            case RETURN_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (pictureUri != null) {
                        if (FileUtils.isFileExist(pictureUri.getSchemeSpecificPart())) {
                            MediaScannerConnection.scanFile(this, new String[]{pictureUri.getSchemeSpecificPart()}, new String[]{"image/jpeg", "image/png"}, null);
                            ArrayList<Uri> pictureUris = new ArrayList<Uri>();
                            pictureUris.add(pictureUri);
                            ClinkWorldApplication.httpHelper.execute(new UploadFileRunnable(pictureUri, metInputBarcode.getText().toString()));
                        } else {
                            Toast.makeText(this, "抱歉，拍摄照片失败，请稍候再试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else if (data != null) {
                        if (data.getData() != null) {
                            pictureUri = data.getData();
                            ArrayList<Uri> pictureUris = new ArrayList<Uri>();
                            pictureUris.add(pictureUri);
                            ClinkWorldApplication.httpHelper.execute(new UploadFileRunnable(pictureUri, metInputBarcode.getText().toString()));
                        }
                    }
                }
                break;
            case RETURN_FROM_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri pictureUri = data.getParcelableExtra(Intent.EXTRA_STREAM);
                    ClinkWorldApplication.httpHelper.execute(new UploadFileRunnable(pictureUri, metInputBarcode.getText().toString()));
                }
                break;
        }
    }
}
