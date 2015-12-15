package com.clinkworld.pay.imagepicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import com.clinkworld.pay.cache.XmppDiskLruCache;
import com.clinkworld.pay.util.ImageUtils;
import com.clinkworld.pay.util.MemoryUtil;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ime on 14-1-21.
 */
public class BitmapLoadManager {

    private static Context mContext;

    // 线程池
    private static Executor EXECUTOR;
    private static AtomicInteger mCount;

    // 内存级和磁盘级缓存
    private static LruCache<String, Bitmap> mMemoryCache;
    private static XmppDiskLruCache mDiskLruCache;

    public static void init(Context context) {

        mContext = context.getApplicationContext();

        mCount = new AtomicInteger(1);
        EXECUTOR = Executors.newFixedThreadPool(5, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "BitmapBindTask #" + mCount.getAndIncrement());
            }
        });

        int cacheSize = (int) Runtime.getRuntime().maxMemory() / 32;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return MemoryUtil.getBitmapMemory(value);
            }
        };


        mDiskLruCache = XmppDiskLruCache.getInstance();
    }

    public static AsyncTask load(ImageView imageView, Uri imageUri, int width, int height) {
        Bitmap bitmap = mMemoryCache.get(imageUri.buildUpon().toString());
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return null;
        } else {
            BitmapBindTask task = new BitmapBindTask(mContext, mMemoryCache, mDiskLruCache, imageView, imageUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(EXECUTOR, width, height);
            } else {
                task.execute(width, height);
            }
            return task;
        }
    }

    public static void release() {
        mDiskLruCache = null;
        mMemoryCache.evictAll();
        mMemoryCache = null;
        mCount = null;
        EXECUTOR = null;
        mContext = null;
    }

    private static class BitmapBindTask extends AsyncTask<Integer, Void, Bitmap> {

        private final Context context;
        private final XmppDiskLruCache mDiskLruCache;
        private final LruCache<String, Bitmap> mMemoryCache;
        private final ImageView imageView;
        private final Uri imageUri;

        public BitmapBindTask(Context context, LruCache<String, Bitmap> memoryCache, XmppDiskLruCache diskLruCache, ImageView imageView, Uri imageUri) {
            this.context = context;
            this.mMemoryCache = memoryCache;
            this.mDiskLruCache = diskLruCache;
            this.imageView = imageView;
            this.imageUri = imageUri;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            String uriString = imageUri.buildUpon().toString();
            Bitmap bitmap = null;
            File file = mDiskLruCache.getFile(uriString);
            if (file != null && file.exists()) {
                bitmap = ImageUtils.getCompressBitmapByUri2(context, Uri.fromFile(file), params[0], params[1]);
            }
            if (bitmap == null) {
                bitmap = ImageUtils.getCompressBitmapByUri2(context, imageUri, params[0], params[1]);
                if (bitmap != null) {
                    mDiskLruCache.put(uriString, ImageUtils.bitmapToByteArray(bitmap, 100));
                    mMemoryCache.put(uriString, bitmap);
                }
            } else {
                mMemoryCache.put(uriString, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (!isCancelled()) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
