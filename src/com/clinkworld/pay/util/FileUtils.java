package com.clinkworld.pay.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import com.lidroid.xutils.util.IOUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.zip.GZIPOutputStream;

public class FileUtils {

    /**
     * 判断SD卡是否可用
     */
    public static boolean isSdcardValid() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断指定路径的文件是否存在
     */
    public static boolean isFileExist(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建目录
     *
     * @return 如果目录已经存在，或者目录创建成功，返回true；如果目录创建失败，返回false
     */
    public static boolean createFolder(String folderPath) {
        boolean success = false;
        try {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                success = true;
            } else {
                success = folder.mkdirs();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return success;
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
     * 移动指定文件到指定的路径
     */
    public static boolean copyFile(String fromPath, String toPath) {
        boolean success;
        // get channels
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            fis = new FileInputStream(fromPath);
            fos = new FileOutputStream(toPath);
            fcin = fis.getChannel();
            fcout = fos.getChannel();

            // do the file copy
            fcin.transferTo(0, fcin.size(), fcout);
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            try {
                // finish up
                if (fcin != null) {
                    fcin.close();
                }
                if (fcout != null) {
                    fcout.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return success;
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
            IOUtils.closeQuietly(out);
        }

    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File \'" + file + "\' exists but is a directory");
            }

            if (!file.canWrite()) {
                throw new IOException("File \'" + file + "\' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Directory \'" + parent + "\' could not be created");
            }
        }

        return new FileOutputStream(file, append);
    }

    /**
     * 移动指定文件到指定的路径
     */
    public static boolean moveFile(String fromPath, String toPath) {
        try {
            File fromFile = new File(fromPath);
            File toFile = new File(toPath);
            if (fromFile.exists()) {
                return fromFile.renameTo(toFile);
            } else {
                return false;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除指定路径的文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            return new File(filePath).delete();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除指定文件夹中的全部文件
     */
    public static boolean cleanDirectory(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return false;
        }
        try {
            for (File tempFile : new File(folderPath).listFiles()) {
                if (tempFile.isDirectory()) {
                    cleanDirectory(tempFile.getPath());
                }
                tempFile.delete();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 这个是手机内存的总空间大小
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 这个是手机内存的可用空间大小
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 这个是外部存储的可用空间大小
     */
    public static long getAvailableExternalMemorySize() {
        long availableExternalMemorySize = 0;
        if (isSdcardValid()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            availableExternalMemorySize = availableBlocks * blockSize;
        } else {
            availableExternalMemorySize = -1;
        }
        return availableExternalMemorySize;
    }

    /**
     * 这个是外部存储的总空间大小
     */
    public static long getTotalExternalMemorySize() {
        long totalExternalMemorySize = 0;
        if (isSdcardValid()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            totalExternalMemorySize = totalBlocks * blockSize;
        } else {
            totalExternalMemorySize = -1;
        }

        return totalExternalMemorySize;
    }

    /**
     * 返回一个文件大小的字符串
     *
     * @param size 文件长度（单位Byte）
     * @return 文件大小的字符串（单位是MB、KB或者Byte）
     */
    public static String fileSize(long size) {
        String str;
        if (size >= 1024) {
            str = "KB";
            size /= 1024;
            if (size >= 1024) {
                str = "MB";
                size /= 1024;
            }
        } else {
            str = "Byte";
        }
        DecimalFormat formatter = new DecimalFormat();
        /* 每3个数字用,分隔如：1,000 */
        formatter.setGroupingSize(3);
        return formatter.format(size) + str;
    }

    /**
     * 将指定文本内容写入文件（指定目录）
     */
    public static boolean writeFile(String filePath, String content,
                                    boolean append) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                    fileWriter = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将指定文本内容写入文件（指定文件）
     */
    public static boolean writeFile(File file, String content, boolean append) {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                    fileWriter = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 从指定位置读取文本内容（指定目录）
     */
    public static String readFile(String filePath) {

        FileReader fileReader = null;
        BufferedReader br = null;
        String content = null;
        try {
            StringBuilder sb = new StringBuilder();
            // 建立对象fileReader
            fileReader = new FileReader(filePath);
            br = new BufferedReader(fileReader);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append('\n');
            }
            // 将字符列表转换成字符串
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                    fileReader = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 从指定位置读取文本内容（指定文件）
     */
    public static String readFile(File file) {

        FileReader fileReader = null;
        BufferedReader br = null;
        String content = null;
        try {
            StringBuilder sb = new StringBuilder();
            // 建立对象fileReader
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append('\n');
            }
            // 将字符列表转换成字符串
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                    fileReader = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 从assets中获取文件并读取数据（资源文件只能读不能写）
     */
    public static String readAssetsFile(Context context, String fileName) {
        String res = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            res = readInputStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                is = null;
            }
        }
        return res;
    }

    /**
     * 从assets中获取文件并读取数据（资源文件只能读不能写）
     */
    public static String readRawFile(Context context, int fileResId) {
        String res = null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(fileResId);
            res = readInputStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                is = null;
            }
        }
        return res;
    }

    /**
     * 从一个InputStrem中读取String内容
     *
     * @throws IOException
     */
    private static String readInputStream(InputStream is) throws IOException {
        int length = is.available();
        byte[] buffer = new byte[length];
        is.read(buffer);
        return new String(buffer, "UTF-8");
    }

    /**
     * 将文本用Gzip压缩后写入文件（指定文件名）
     */
    public static boolean writeGzipFile(Context context, String filePath,
                                        String content) {
        File file;
        file = new File(filePath);

        FileOutputStream fos = null;
        GZIPOutputStream gos = null;
        try {
            fos = new FileOutputStream(file, false);
            gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            gos.write(content.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                gos.finish();
                gos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getExternalFilesAbsolutePath(Context context) {
        return isSdcardValid() ? context.getExternalFilesDir(null).getAbsolutePath() : "/sdcard/Android/data/com.ime.xmpp/files";
    }

    public static String getExternalCacheAbsolutePath(Context context) {
        return isSdcardValid() ? context.getExternalFilesDir(null).getAbsolutePath() : "/sdcard/Android/data/com.ime.xmpp/cache";
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readFile(Context context, String fileName) {
        if (!exists(context, fileName)) {
            return null;
        }
        FileInputStream fis = null;
        String content = null;
        try {
            fis = context.openFileInput(fileName);
            if (fis != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = fis.read(buffer);
                    if (readLength == -1)
                        break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                fis.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 存储文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名，要在系统内保持唯一
     * @param content  文本内容
     * @return boolean 存储成功的标志
     */
    public static boolean writeFile(Context context, String fileName,
                                    String content) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] byteContent = content.getBytes();
            fos.write(byteContent);

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return success;
    }

    /**
     * 文件是否存在
     *
     * @param context
     * @param fileName
     * @return
     */
    public static boolean exists(Context context, String fileName) {
        return new File(context.getFilesDir(), fileName).exists();
    }

    /**
     * 删除文件
     *
     * @param context
     * @param fileName
     */
    public static void delete(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}