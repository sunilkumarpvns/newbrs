package com.elitecore.core.systemx.esix.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;

/**
 * Create HttpClient with connection pool
 *
 * @author Kartik Prajapati
 */


public class HTTPClientFactory {
    private PoolingClientConnectionManager poolingClientConnectionManager;

    public HTTPClientFactory(){
        super();
        poolingClientConnectionManager = new PoolingClientConnectionManager();
    }

    public HttpClient getPoolableHttpClient(int maxPoolSize, int connectionTimeOut, int readTimeOut){
        HttpClient httpClient = new DefaultHttpClient(poolingClientConnectionManager);
        poolingClientConnectionManager.setMaxTotal(maxPoolSize);
        poolingClientConnectionManager.setDefaultMaxPerRoute(maxPoolSize);
        httpClient.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connectionTimeOut);
        httpClient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, readTimeOut);
        return httpClient;
    }

    public HttpClient getHttpClient(int connectionTimeOut, int readTimeOut){
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connectionTimeOut);
        httpClient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, readTimeOut);
        return httpClient;
    }

    public void shutdown(){
        poolingClientConnectionManager.shutdown();
    }



}
