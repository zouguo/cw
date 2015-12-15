package com.clinkworld.pay.cache;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class CacheUtils {
    public final static String TAG = "cache";

    private CacheUtils() {
    }

    ;

    public static final int HTTP_CACHE_SIZE = 1 * 1024 * 1024; // 10MB
    public static final String HTTP_CACHE_DIR = "http";

    // 默认缓存大小
    public static final int DEFAULT_CACHE_FILE_SIZE = 10 * 512; // 512 kb

    // 默认缓存图片目录
    public static final String IMAGE_CACHE_DIR = "thumbs";

    // 8k 缓存
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    /**
     * 按照提供路径有多少可用空间
     *
     * @param path
     * @return
     */
    @SuppressLint("NewApi")
    public static long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Check if external storage is built-in or removable. 2.3以后的手机可以区分
     * 内部大容量存储和外部 内部为不可以移除这样保证我们程序的缓存稳定性。
     * 注意 2.3以前没有这个特性。
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        // 2.3 以后才支持的参数
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        // 返回false强制使用外部存储
        return false;
    }

    /**
     * 是否支持外部缓存目录
     *
     * @return
     */
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * 获得sd卡上的缓存目录
     *
     * @param context The context to use
     * @return The external cache dir
     */
    public static File getExternalCacheDir(Context context) {
        // android 2.2 以后才支持的特性
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        // 2.2以前需要自己构此目录
        final String cacheDir = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/.clinkworld" + "/data/" + context.getPackageName()
                + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    /**
     * Check if OS version has a http URLConnection bug. See here for more
     * information:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     *
     * @return
     */
    public static boolean hasHttpConnectionBug() {
        // return Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO;
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR;
    }

    /**
     * Workaround for bug pre-Froyo, see here for more info:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     * 2.1 以前版本你需要手动设置
     */
    public static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (hasHttpConnectionBug()) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * 复制??	 *
     *
     * @param is
     * @param os
     */
    public static void CopyStream(InputStream in, OutputStream out) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = in.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                out.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }

        // 第二种实现方??		// int b;
        // while ((b = in.read()) != -1) {
        // out.write(b);
        // }

    }

    /**
     * Get the memory class of this device (approx. per-app memory limit)
     *
     * @param context
     * @return
     */
    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
    }


}
