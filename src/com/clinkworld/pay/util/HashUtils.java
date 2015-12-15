package com.clinkworld.pay.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: ime
 * Date: 13-3-31
 * Time: 上午10:33
 * To change this template use File | Settings | File Templates.
 */
public class HashUtils {

    //MD5加密，32位
    public static String MD5(String str) {
        return str == null ? null : MD5(str.getBytes());
    }

    //MD5加密，32位
    public static String MD5(byte[] bytes) {
        String result = null;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(bytes);
            result = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
