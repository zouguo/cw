package com.clinkworld.pay.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import com.clinkworld.pay.ClinkWorldApplication;

import java.io.*;

/**
 * Created by shirenhua on 2015/10/18.
 * <p/>
 * 扫描图片处理
 */
public class QrImageUtils {

    private static final String TAG = "QrImageUtils.this";

    /**
     * 得到旋转前的图片数据
     *
     * @param data
     * @return
     */
    public static byte[] rotationByteData(byte[] data) {
        try {
            createBitmapFile(data);
//            Bitmap beforBitmap = byte2Bitmap(data);
//            int degree = getExifOrientation(createBitmapFile(data));
//            Bitmap bitmap = null;
//            if (degree == 90 || degree == 180 || degree == 270) {
//                Matrix matrix = new Matrix();
//                matrix.postRotate(degree);
//                bitmap = Bitmap.createBitmap(
//                        beforBitmap,
//                        0, 0, beforBitmap.getWidth(), beforBitmap.getHeight(), matrix, true);
//            } else {
//                bitmap = beforBitmap;
//            }
//            return Bitmap2Bytes(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 位图转字节数组
     *
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, boas);
        return boas.toByteArray();
    }


    /**
     * 字节数组转位图
     *
     * @param data
     * @return
     */
    public static Bitmap byte2Bitmap(byte[] data) {
        if (data.length != 0) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } else {
            return null;
        }
    }


    /**
     * 位图数据保存返回图片地址
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static String createBitmapFile(byte[] data) throws IOException {
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
                .getExternalStorageState())) {
            String path = "/sdcard/ABC/IME_CRASH/shaomiao.png";
            File voiceCacheFile = new File(path);
            if (!voiceCacheFile.getParentFile().exists()) {
                QrImageUtils.createFileFolder(path);
            }
            if (voiceCacheFile.createNewFile()) {
                writeByteArrayToFile(voiceCacheFile, data);
            }
            return "";
        }
        return null;
    }

    public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        writeByteArrayToFile(file, data, false);
    }

    public static void writeByteArrayToFile(File file, byte[] data, boolean append) throws IOException {
        FileOutputStream out = null;

        try {
            out = openOutputStream(file, append);
            out.write(data);
            out.close();
        } finally {

        }

    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if(file.exists()) {
            if(file.isDirectory()) {
                throw new IOException("File \'" + file + "\' exists but is a directory");
            }

            if(!file.canWrite()) {
                throw new IOException("File \'" + file + "\' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if(parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Directory \'" + parent + "\' could not be created");
            }
        }

        return new FileOutputStream(file, append);
    }


    /**
     * 创建指定文件的目录。例如filePath=/sdcard/aaa/bbb/ccc/1.txt，会创建/sdcard/aaa/bbb/ccc/目录。
     */
    public static void createFileFolder(String filePath) {
        try {
            new File(filePath).getParentFile().mkdirs();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片的旋转角度
     *
     * @param filepath
     * @return
     */
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:
                        break;
                }
            }
        }

        return degree;
    }
}
