package com.test.tools.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class GetHtml {
    public static String get(String url, String getParameter, String cookies) {
        HttpGet httpGet = null;

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            String newUrl = url + "/?" + URLEncoder.encode(getParameter, "UTF-8");


            newUrl = newUrl.replace("%3D", "=").replace("%26", "&");
            httpGet = new HttpGet(newUrl);

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).setRedirectsEnabled(true).build();
            if (cookies != null && !cookies.equals("")) {
                httpGet.addHeader("cookie", cookies);
            }
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
            httpGet.addHeader("Content-Type", "application/json;charset=utf8");
            httpGet.setConfig(requestConfig);

            CloseableHttpResponse response = httpClient.execute((HttpUriRequest) httpGet);

            HttpEntity entity = response.getEntity();

            return EntityUtils.toString(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String post(String url, List<NameValuePair> param, String cookies) {
        HttpPost httpPost = null;

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            httpPost = new HttpPost(url);

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).setRedirectsEnabled(true).build();
            if (cookies != null && !cookies.equals("")) {
                httpPost.addHeader("cookie", cookies);
            }
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
            httpPost.setConfig(requestConfig);

//            new UrlEncodedFormEntity(param, "UTF-8"));
            CloseableHttpResponse response = httpClient.execute((HttpUriRequest) httpPost);


            HttpEntity entity = response.getEntity();


            return EntityUtils.toString(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static byte[] getByteByUrl(String url) {
        try {
            URL uri = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            InputStream inputStream = conn.getInputStream();

            byte[] buffer = new byte[1024];
            int len = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

