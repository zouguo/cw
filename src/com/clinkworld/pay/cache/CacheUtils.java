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

    // Ĭ�ϻ����С
    public static final int DEFAULT_CACHE_FILE_SIZE = 10 * 512; // 512 kb

    // Ĭ�ϻ���ͼƬĿ¼
    public static final String IMAGE_CACHE_DIR = "thumbs";

    // 8k ����
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    /**
     * �����ṩ·���ж��ٿ��ÿռ�
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
     * Check if external storage is built-in or removable. 2.3�Ժ���ֻ���������
     * �ڲ��������洢���ⲿ �ڲ�Ϊ�������Ƴ�������֤���ǳ���Ļ����ȶ��ԡ�
     * ע�� 2.3��ǰû��������ԡ�
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        // 2.3 �Ժ��֧�ֵĲ���
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        // ����falseǿ��ʹ���ⲿ�洢
        return false;
    }

    /**
     * �Ƿ�֧���ⲿ����Ŀ¼
     *
     * @return
     */
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * ���sd���ϵĻ���Ŀ¼
     *
     * @param context The context to use
     * @return The external cache dir
     */
    public static File getExternalCacheDir(Context context) {
        // android 2.2 �Ժ��֧�ֵ�����
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        // 2.2��ǰ��Ҫ�Լ�����Ŀ¼
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
     * 2.1 ��ǰ�汾����Ҫ�ֶ�����
     */
    public static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (hasHttpConnectionBug()) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * ����??	 *
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

        // �ڶ���ʵ�ַ�??		// int b;
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
