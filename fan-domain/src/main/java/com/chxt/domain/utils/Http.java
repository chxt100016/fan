package com.chxt.domain.utils;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.mime.MultipartEntityBuilder;


@Slf4j
public class Http {

    private static final CloseableHttpClient httpClient;

    static {
        RequestConfig config = RequestConfig.custom()
//                .setConnectionRequestTimeout(1000)
//                .setConnectTimeout(5000)
//                .setSocketTimeout(5000)
                .build();

        httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();
    }





    private Object entity;

    private HashMap<String, String> entityMap;

    private String fileName;

    private byte[] fileData;

    private String uri;

    private volatile Map<String, String> header;

    private volatile List<NameValuePair> param;

    private HttpRequestBase request;

    private StatusLine statusLine;

    private byte[] contentByteArray;


    private Http() {
    }


    public static Http uri(String uri) {
        Http http = new Http();
        http.uri = uri;
        return http;
    }

    public Http param(String name, String value){
        if (this.param == null) {
            synchronized (this) {
                if (this.param == null) {
                    this.param = new ArrayList<>();
                }
            }
        }
        this.param.add(new BasicNameValuePair(name, value));
        return this;
    }

    public Http file(String name, byte[] data) {
        this.fileName = name;
        this.fileData = data;
        return this;
    }

    public Http entity(Object entity) {
        this.entity = entity;
        return this;
    }

    public Http entity(String key, String value) {
        if (this.entityMap == null) this.entityMap = new HashMap<>();
        entityMap.put(key, value);
        return this;
    }

    public Http header(String name, String value) {
        if (this.header == null) {
            synchronized (this) {
                if (this.header == null) {
                    this.header = new HashMap<>();
                }
            }
        }
        this.header.put(name, value);
        return this;
    }

    public Http multiPartHeader(){
        this.header("Content-Type", "multipart/form-data");
        return this;
    }

    public Http jsonHeader(){
        this.header("Content-Type", "application/json");
        return this;
    }

    public Http header(Map<String, String> header) {
        for (Map.Entry<String, String> entry : header.entrySet()) {
            this.header(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @SneakyThrows
    public <T> T result(Class<T> clazz) {
        return StringUtils.isBlank(new String(this.contentByteArray)) ? null : JSON.parseObject(new String(this.contentByteArray), clazz);
    }

    @SneakyThrows
    public <T> T result(TypeReference<T> typeReference) {
        return StringUtils.isBlank(new String(this.contentByteArray)) ? null : JSON.parseObject(new String(this.contentByteArray), typeReference);
    }

    @SneakyThrows
    public String result() {
        return new String(this.contentByteArray);
    }

    @SneakyThrows
    public void download(String path){
        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(this.contentByteArray), new File(path));
    }

    @SneakyThrows
    public byte[] byteArray(){
        return this.contentByteArray;
    }


    public int getStatusCode() {
        return this.statusLine.getStatusCode();
    }

    @SneakyThrows
    public Http doPost() {
        return this.doPost(null);
    }

    @SneakyThrows
    public Http doPost(PostProcessor postProcessor) {
        this.request = new HttpPost();
        this.execute(postProcessor);
        return this;
    }

    @SneakyThrows
    public Http doPut() {
        return this.doPut(null);
    }

    @SneakyThrows
    public Http doPut(PostProcessor postProcessor) {
        this.request = new HttpPut();
        this.execute(postProcessor);
        return this;
    }

    @SneakyThrows
    public Http doGet(){
        return this.doGet(null);
    }

    @SneakyThrows
    public Http doGet(PostProcessor postProcessor){
        this.request = new HttpGet();
        this.execute(postProcessor);
        return this;
    }

    @SneakyThrows
    public Http doDelete() {
        this.request = new HttpDelete();
        this.execute(null);
        return this;
    }

    @SneakyThrows
    private void execute(PostProcessor postProcessor){
        this.setHeader();
        this.setUri();
        this.setEntity();

        try {
            CloseableHttpResponse response = Http.httpClient.execute(this.request);

            if(postProcessor != null) {
                CloseableHttpResponse responseTmp = postProcessor.process(Http.httpClient, this.request, response);
                if (response != responseTmp) {
                    response.close();
                    response = responseTmp;
                }
            }

            this.statusLine = response.getStatusLine();
            if (response.getEntity() != null) {
                this.contentByteArray = EntityUtils.toByteArray(response.getEntity());
            } else {
                this.contentByteArray = new byte[0];
                this.request.releaseConnection();
            }
            
        } catch (Exception e) {
            log.error("http请求异常, url:{}, entity:{}", JSON.toJSONString(this.request.getURI()), JSON.toJSONString(this.entity), e);
        } finally {
            this.request.releaseConnection();
        }
    }

    private void setHeader(){
        if (this.header != null) {
            for (Map.Entry<String, String> e : this.header.entrySet()) {
                this.request.setHeader(e.getKey(), e.getValue());
            }
        }
    }

    @SneakyThrows
    private void setUri(){
        URIBuilder uriBuilder = new URIBuilder(this.uri);
        if (this.param != null) {
            uriBuilder.setParameters(this.param);
        }
        URI uri = uriBuilder.build();
        this.request.setURI(uri);
    }

    private void setEntity(){
        
        if (!(this.request instanceof HttpGet)) {
            if (this.fileName != null && this.fileData != null) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .addBinaryBody("media", this.fileData, ContentType.MULTIPART_FORM_DATA, this.fileName);
                ((HttpEntityEnclosingRequestBase) this.request).setEntity(builder.build());
            } else if (this.entity == null && this.entityMap != null) {
                ((HttpEntityEnclosingRequestBase) this.request).setEntity(new StringEntity(JSON.toJSONString(this.entityMap), "UTF-8"));
            } else {
                ((HttpEntityEnclosingRequestBase) this.request).setEntity(new StringEntity(JSON.toJSONString(this.entity), "UTF-8"));
            }
        }
    }



    public interface PostProcessor {

        CloseableHttpResponse process(CloseableHttpClient client, HttpRequestBase request, CloseableHttpResponse response) throws IOException;
    }

}
