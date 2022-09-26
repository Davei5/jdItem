package com.kingstar.milldata.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class HttpUtil {

    private PoolingHttpClientConnectionManager cm;

    public HttpUtil() {
        this.cm = new PoolingHttpClientConnectionManager();

        //设置最大连接数
        this.cm.setMaxTotal(5000);

        //设置每个主机的最大连接数
        this.cm.setDefaultMaxPerRoute(20);
    }

    /**
     * 根据请求地址下载页面数据
     *
     * @param url
     * @return 页面数据
     */
    public String doGetHtml(String url) {
        //获取HttpClient对象
//        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();

        //创建连接池管理器
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(60000,
                TimeUnit.MILLISECONDS);
        connectionManager.setMaxTotal(5000);
        connectionManager.setDefaultMaxPerRoute(50);

        //创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .disableAutomaticRetries()
                .build();

        //创建httpGet请求对象，设置url地址
        HttpGet httpGet = new HttpGet(url);

        //设置请求信息
        httpGet.setConfig(this.getConfig());

        //设置请求头模拟浏览器
//        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:72.0) Gecko/20100101 Firefox/72.0");
        httpGet.setHeader("User-Agent","Mozilla/5.0");
//        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) ");
//        httpGet.setHeader("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko)");
        httpGet.setHeader("Accept-Encoding","gzip");
        httpGet.setHeader("Accept","text/html");
        httpGet.setHeader("Accept-Language","zh-CN,zh");
        httpGet.setHeader("sec-ch-ua-platform","Android");


        CloseableHttpResponse response = null;


        try {
            //使用HttpClient发起请求，获取响应
            response = httpClient.execute(httpGet);

            //解析响应，返回结果
            if (response.getStatusLine().getStatusCode() == 200) {
                //判断响应体Entity是否不为空，如果不为空就可以使用EntityUtils
                if (response.getEntity() != null) {
                    return EntityUtils.toString(response.getEntity(), "utf8");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭response
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //返回空串
        return "";
    }

    //设置请求信息
    private RequestConfig getConfig() {
        // 设置代理IP
        HttpHost proxy = new HttpHost("54.254.208.229", 12365);
        RequestConfig config = RequestConfig.custom()
//                .setProxy(proxy)
                 // 创建连接的最长时间
                .setConnectTimeout(1000)
                 // 获取连接的最长时间
                .setConnectionRequestTimeout(1000)
                 //数据传输的最长时间
                .setSocketTimeout(10000)
                .build();

        return config;
    }
}
