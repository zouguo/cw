package com.clinkworld.pay.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.clinkworld.pay.R;
import com.clinkworld.pay.cache.DiskLruCache;
import com.lidroid.xutils.util.IOUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by srh on 2015/11/9.
 */
public class ImageUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static Uri getOutputMediaFileUri() {
        File outputMediaFile = getOutputMediaFile();
        return outputMediaFile == null ? null : Uri.fromFile(outputMediaFile);
    }

    @TargetApi(8)
    public static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!FileUtils.isSdcardValid()) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = DATE_FORMAT.format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "CWorld_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap, int compressQuality) {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();

        if (bitmap.hasAlpha()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, compressQuality, localByteArrayOutputStream);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, localByteArrayOutputStream);
        }
        byte[] result = localByteArrayOutputStream.toByteArray();
        IOUtils.closeQuietly(localByteArrayOutputStream);
        return result;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String path = null;
        String uriScheme = uri.getScheme();
        if (uriScheme.equals("file")) {
            path = uri.getSchemeSpecificPart();
        } else if (uriScheme.equals("content")) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(0);
                }
                cursor.close();
            }

            if (TextUtils.isEmpty(path) && FileUtils.isSdcardValid()) {
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    path = FileUtils.getExternalFilesAbsolutePath(context) + "/images/";
                    inputStream = context.getContentResolver().openInputStream(uri);
                    File file = new File(path, HashUtils.MD5(uri.toString()));
                    path = path + file.getName();
                    if (!file.exists()) {
                        if (!file.getParentFile().exists()) {
                            FileUtils.createFileFolder(file.getPath());
                        }
                        file.createNewFile();
                        outputStream = new FileOutputStream(file);
                        copy(inputStream, outputStream);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        IOUtils.closeQuietly(inputStream);
                    }
                    if (outputStream != null) {
                        IOUtils.closeQuietly(outputStream);
                    }
                }
            }
        }
        return path;
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L ? -1 : (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        return copyLarge(input, output, new byte[4096]);
    }

    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0L;

        int n1;
        for (boolean n = false; -1 != (n1 = input.read(buffer)); count += (long) n1) {
            output.write(buffer, 0, n1);
        }

        return count;
    }

    private static Bitmap getBitmapByUri(Context context, Uri uri, BitmapFactory.Options options, int sampleSize) {
        Bitmap result = null;
        String filePath = ImageUtils.getPathFromUri(context, uri);
        if (FileUtils.isFileExist(filePath)) {
            InputStream is = null;
            try {
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                is = new FileInputStream(filePath);
                Bitmap originalBitmap = BitmapFactory.decodeStream(is, null, options);
                // Determine the orientation for this image
                final int orientation = ExifUtils.getExifOrientation(filePath);
                if (originalBitmap != null && orientation != 0) {
                    final Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);
                    Bitmap bitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(),
                            originalBitmap.getHeight(), matrix, true);
                    originalBitmap.recycle();
                    result = bitmap;
                } else {
                    result = originalBitmap;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError oome) {
                oome.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return result;
    }

    public static final Bitmap getCompressBitmapByUri(Context context, Uri uri, int reqWith, int reqHeight) {
        BitmapFactory.Options options = getBoundOptionByUri(context, uri);
        int sampleSize = ImageUtils.findSampleTargetDesire(options.outWidth, options.outHeight, reqWith, reqHeight);
        return getBitmapByUri(context, uri, options, sampleSize);
    }

    /**
     * 得到压缩比例值
     *
     * @param actualWidth
     * @param actualHeight
     * @param desireWidth
     * @param desireHeight
     * @return
     */
    public static final int findSampleTargetDesire(int actualWidth, int actualHeight, int desireWidth, int desireHeight) {
        double wr = (double) actualWidth / desireWidth;
        double hr = (double) actualHeight / desireHeight;
        double ratio = Math.max(wr, hr);
        return (int) ratio;
    }

    public static final Bitmap getCompressBitmapByUri2(Context context, Uri uri, int reqWith, int reqHeight) {
        BitmapFactory.Options options = getBoundOptionByUri(context, uri);
        int sampleSize = ImageUtils.findSampleSizeLargerThanDesire(options.outWidth, options.outHeight, reqWith, reqHeight);
        return getBitmapByUri(context, uri, options, sampleSize);
    }

    public static final BitmapFactory.Options getBoundOptionByUri(Context context, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return options;
    }

    public static final int findSampleSizeLargerThanDesire(
            int actualWidth, int actualHeight, int desireWidth, int desireHeight) {
        double wr = (double) actualWidth / desireWidth;
        double hr = (double) actualHeight / desireHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    public static ImageLoaderConfiguration getSimpleImageLoaderConfig(
            Context context) {
        // 根据SDK等级设置内存缓存大小(Android 2.0以前没有提供获取系统分配内存大小的API)
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            // Android 2.0及以后的版本设置内存缓存大小为系统分配内存的1/6
            int memClass = ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 6) * 1024 * 1024; // 1/6 of app memory
            // limit
        } else {
            // Android 2.0以前的设置默认缓存大小4M
            memoryCacheSize = 4 * 1024 * 1024;
        }

        // This configuration tuning is custom. You can tune every option, you
        // may
        // tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                context);
        builder.threadPoolSize(5)
                // 3 5
                // 设置线程池同时进行的线程数
                .threadPriority(Thread.NORM_PRIORITY - 2)
                        // new WeakMemoryCache()
                .memoryCache(new LruMemoryCache(memoryCacheSize))
                        // 设置线程优先级
                .memoryCacheSize(memoryCacheSize)
                        // 设置内存缓存大小
                .denyCacheImageMultipleSizesInMemory()
                        // 设置同一张图片以不同尺寸载入内存时,前面的会被后面的取代
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .defaultDisplayImageOptions(getDefaultOption());
        ImageLoaderConfiguration config = builder.build();
        return config;
    }

    /**
     * 默认装在额外配置
     *
     * @return
     */
    public static DisplayImageOptions getDefaultOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.color.white)
                .showImageForEmptyUri(R.color.white)
                .showImageOnFail(R.color.white).cacheOnDisk(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
        return options;
    }

}
