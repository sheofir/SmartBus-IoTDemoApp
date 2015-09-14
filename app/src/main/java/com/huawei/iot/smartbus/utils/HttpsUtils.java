package com.huawei.iot.smartbus.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by sylar on 2015/8/12.
 */
public class HttpsUtils {

    static TrustManager[] xtmArray = new MyTrustManager[] { new MyTrustManager() };

    public static String requestHTTPSPage( URL url) throws Exception{
        String result = "can't get url datas";
        HttpURLConnection http = null;
        // 判断是http请求还是https请求
        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            http = (HttpsURLConnection) url.openConnection();
            ((HttpsURLConnection) http).setHostnameVerifier(new MyHostnameVerifier());// 不进行主机名确认

        } else {
            http = (HttpURLConnection) url.openConnection();
        }
        http.setConnectTimeout(10000);// 设置超时时间
        http.setReadTimeout(50000);
        http.setRequestMethod("GET");// 设置请求类型为
        http.setDoInput(true);
        http.setRequestProperty("Content-Type", "text/xml");
        //http.getResponseCode());http或https返回状态200还是403
        BufferedReader in = null;
        if (http.getResponseCode() == 200) {
            in = new BufferedReader(new InputStreamReader(
                    http.getInputStream()));
        } else
            in = new BufferedReader(new InputStreamReader(
                    http.getErrorStream()));
        result = in.readLine();
        Log.i("result", result);
        in.close();
        http.disconnect();
        return result;
    }

    public static String requestPostHTTPSPage( URL url, String params) throws Exception{
        String result = "can't get url datas";
        HttpURLConnection http = null;
        // 判断是http请求还是https请求
        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            http = (HttpsURLConnection) url.openConnection();
            ((HttpsURLConnection) http).setHostnameVerifier(new MyHostnameVerifier());// 不进行主机名确认

        } else {
            http = (HttpURLConnection) url.openConnection();
        }
        http.setConnectTimeout(10000);// 设置超时时间
        http.setReadTimeout(50000);
        http.setRequestMethod("POST");// 设置请求类型为
        http.setDoInput(true);
        //http.getResponseCode());http或https返回状态200还是403
        PrintWriter pw = new PrintWriter(http.getOutputStream());
        pw.print(params);
        pw.flush();

        BufferedReader in = null;
        if (http.getResponseCode() == 200) {
            in = new BufferedReader(new InputStreamReader(
                    http.getInputStream()));
        } else
            in = new BufferedReader(new InputStreamReader(
                    http.getErrorStream()));
        result = in.readLine();
        Log.i("mylog", result);
        in.close();
        http.disconnect();
        return result;
    }

    /**
     * 信任所有主机-对于任何证书都不做检查
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android 采用X509的证书信息机制
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, xtmArray, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
            // HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);//
            // 不进行主机名确认
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
