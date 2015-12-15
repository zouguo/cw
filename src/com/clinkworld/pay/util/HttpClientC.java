package com.clinkworld.pay.util;

import android.graphics.Bitmap;
import android.net.Uri;
import com.clinkworld.pay.ClinkWorldApplication;
import com.clinkworld.pay.http.SimpleMultipartEntity;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.*;

public class HttpClientC {
    final String TAG = this.getClass().getSimpleName();

    /*
    * 访问的网络端口号
    */
    private static final int HTTPS_PORT = 443;
    /*
     * 网络字符编码格式
     */
    private static final String CHARSET = HTTP.UTF_8;
    /*
     * 网络连接、获取返回超时设置
	 */
    private static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;

    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    /*
     * 连接池获取超时时间
     */
    private static final int DEFAULT_MANGER_CONNECT_TIMEOUT = 20 * 1000;
    /*
 * 使用apach的httpclient进行http通信
 */
    private static DefaultHttpClient mHttpClient = null;
    private static HttpClient msgHttpClient = null;

    public static String HTTP_CLIENT_FAIL = "http_client_fail";

    public static synchronized HttpClient getHttpClient() {
        if (mHttpClient == null) {
            HttpParams params = new BasicHttpParams();
            try {

                // 设置一些基本参数
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, CHARSET);
                HttpProtocolParams.setUseExpectContinue(params, true);

                /* 从连接池中取连接的超时时间 */
                ConnManagerParams.setTimeout(params, DEFAULT_MANGER_CONNECT_TIMEOUT);
                /* 连接超时 */
                HttpConnectionParams.setConnectionTimeout(params, DEFAULT_MANGER_CONNECT_TIMEOUT);
                /* 请求超时 */
                HttpConnectionParams.setSoTimeout(params, DEFAULT_SOCKET_TIMEOUT);
                // *************** 注意
                // ***********************************************
//                 ************** 设置不验证服务器端证书，使用SSLSocketFactoryEx ******
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                CertificateFactory cerFactory = CertificateFactory
                        .getInstance("X.509");
                Certificate cer = cerFactory.generateCertificate(ClinkWorldApplication.mApplication.getResources().getAssets().open("server_mapi.crt"));
                trustStore.setCertificateEntry("trust", cer);


                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
                // 设置我们的HttpClient支持HTTP和HTTPS两种模式
                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schReg.register(new Scheme("https", sf, 443));
                //关闭100-continue
                params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
                // 使用线程安全的连接管理来创建HttpClient
                ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
                // 支持Android 1.5及以上
                mHttpClient = new DefaultHttpClient(conMgr, params);
            } catch (Exception e) {
                //关闭100-continue
                params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
                mHttpClient = new DefaultHttpClient(params);
            }
        }

        return mHttpClient;
    }

    public static String put(String url, Map<String, String> params) {
        String res = null;
        if (mHttpClient == null) {
            getHttpClient();
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        HttpPut put = new HttpPut(url);
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            put.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
        }
        HttpResponse response = null;
        try {
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(mHttpClient.getParams(), DEFAULT_MANGER_CONNECT_TIMEOUT);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(mHttpClient.getParams(), DEFAULT_SOCKET_TIMEOUT);
            mHttpClient.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            mHttpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
            response = mHttpClient.execute(put);
            HttpEntity entity = response.getEntity();
            String charset = EntityUtils.getContentCharSet(entity);
            res = EntityUtils.toString(entity, charset == null ? "UTF-8" : charset);
        } catch (Exception e) {
            res = HTTP_CLIENT_FAIL;
            CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.getEntity().consumeContent();
                } catch (Exception e) {
                    CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
                }
            }
        }
        return res;
    }

    public static String postFile(String url, Uri fileUri, Map<String, String> params) {
        String res = null;
        String contentType = "";
        if (mHttpClient == null) {
            getHttpClient();
        }
        File uploadFile = null;
        try {
            File file = new File(ImageUtils.getPathFromUri(ClinkWorldApplication.mApplication, fileUri));
            Bitmap bitmap = ImageUtils.getCompressBitmapByUri(ClinkWorldApplication.mApplication, fileUri, 640, 960);
            if (bitmap == null) {
                uploadFile = file;
            } else {
                uploadFile = ImageUtils.getOutputMediaFile();
                int compressQuality = 100;
                if (MemoryUtil.getBitmapMemory(bitmap) > 50 * 1024) {
                    compressQuality = 75;
                }
                FileUtils.writeByteArrayToFile(uploadFile, ImageUtils.bitmapToByteArray(bitmap, compressQuality));
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();
        HttpPost post = new HttpPost(url);
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            multipartEntity.addPart(key, params.get(key));
        }
        try {
            Bitmap bitmap = ImageUtils.getCompressBitmapByUri(ClinkWorldApplication.mApplication, fileUri, 30, 30);
            if (bitmap != null && bitmap.hasAlpha()) {
                contentType = "image/png";
            } else {
                contentType = "image/jpeg";
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            multipartEntity.addPart("file", uploadFile.getName(), new FileInputStream(uploadFile), contentType, true);
            post.setEntity(multipartEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(mHttpClient.getParams(), DEFAULT_MANGER_CONNECT_TIMEOUT);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(mHttpClient.getParams(), DEFAULT_SOCKET_TIMEOUT);
            mHttpClient.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            mHttpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
            response = mHttpClient.execute(post);
            HttpEntity entity = response.getEntity();
            String charset = EntityUtils.getContentCharSet(entity);
            res = EntityUtils.toString(entity, charset == null ? "UTF-8" : charset);
        } catch (Exception e) {
            res = HTTP_CLIENT_FAIL;
            CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.getEntity().consumeContent();
                } catch (Exception e) {
                    CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
                }
            }
        }
        return res;
    }

    public static String post(String url, Map<String, String> params) {
        String res = null;
        if (mHttpClient == null) {
            getHttpClient();
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        HttpPost post = new HttpPost(url);
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
        }
        HttpResponse response = null;
        try {
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(mHttpClient.getParams(), DEFAULT_MANGER_CONNECT_TIMEOUT);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(mHttpClient.getParams(), DEFAULT_SOCKET_TIMEOUT);
            mHttpClient.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            mHttpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
            response = mHttpClient.execute(post);
            HttpEntity entity = response.getEntity();
            String charset = EntityUtils.getContentCharSet(entity);
            res = EntityUtils.toString(entity, charset == null ? "UTF-8" : charset);
        } catch (Exception e) {
            res = HTTP_CLIENT_FAIL;
            CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.getEntity().consumeContent();
                } catch (Exception e) {
                    CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
                }
            }
        }
        return res;
    }

    /**
     * Http Get
     *
     * @param url
     * @param params
     * @return
     */
    public static String getHttpUrlWithParams(String url, Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            sb.append(key);
            sb.append("=");
            sb.append(params.get(key));
            sb.append("&");
        }
        String newurl = sb.toString();
        if (null != newurl && newurl.endsWith("&")) {
            newurl = newurl.substring(0, newurl.length() - 1);
        }
        String res = get(newurl);
        return res;
    }


    public static String get(String url) {
        if (mHttpClient == null) {
            getHttpClient();
        }
        String res = null;
        HttpGet get = new HttpGet(url);
        HttpResponse response = null;
        try {
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(mHttpClient.getParams(), DEFAULT_MANGER_CONNECT_TIMEOUT);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(mHttpClient.getParams(), DEFAULT_SOCKET_TIMEOUT);
            //关闭100-continue
            mHttpClient.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            response = mHttpClient.execute(get);
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            res = HTTP_CLIENT_FAIL;
            CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.getEntity().consumeContent();
                } catch (Exception e) {
                    CWLogUtils.e("httpclint", e.getLocalizedMessage(), e);
                }
            }
        }
        return res;
    }

}
