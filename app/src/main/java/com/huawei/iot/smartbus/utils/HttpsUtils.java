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
        // �ж���http������https����
        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            http = (HttpsURLConnection) url.openConnection();
            ((HttpsURLConnection) http).setHostnameVerifier(new MyHostnameVerifier());// ������������ȷ��

        } else {
            http = (HttpURLConnection) url.openConnection();
        }
        http.setConnectTimeout(20000);// ���ó�ʱʱ��
        http.setReadTimeout(50000);
        http.setRequestMethod("GET");// ������������Ϊ
        http.setDoInput(true);
        http.setRequestProperty("Content-Type", "text/xml");
        //http.getResponseCode());http��https����״̬200����403
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
        // �ж���http������https����
        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            http = (HttpsURLConnection) url.openConnection();
            ((HttpsURLConnection) http).setHostnameVerifier(new MyHostnameVerifier());// ������������ȷ��

        } else {
            http = (HttpURLConnection) url.openConnection();
        }
        http.setConnectTimeout(20000);// ���ó�ʱʱ��
        http.setReadTimeout(50000);
        http.setRequestMethod("POST");// ������������Ϊ
        http.setDoInput(true);
        //http.getResponseCode());http��https����״̬200����403
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
     * ������������-�����κ�֤�鶼�������
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android ����X509��֤����Ϣ����
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, xtmArray, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
            // HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);//
            // ������������ȷ��
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
