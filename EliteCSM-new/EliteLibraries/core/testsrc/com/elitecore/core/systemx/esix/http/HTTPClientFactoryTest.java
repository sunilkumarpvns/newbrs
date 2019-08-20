package com.elitecore.core.systemx.esix.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HTTPClientFactoryTest {
    @Before
    public void setup(){

    }

    @Test
    public void createHTTPClientFactoryAndGetSimpleClientFactoryAndFalseCheckForConnectionManagerInstance(){
        HTTPClientFactory factory = new HTTPClientFactory();
        HttpClient client= factory.getHttpClient(10,10);
        Assert.assertFalse(client.getConnectionManager() instanceof PoolingClientConnectionManager);
    }

    @Test
    public void createHTTPClientFactoryAndGetPoolableClientFactoryAndTrueCheckForConnectionManagerInstance(){
        HTTPClientFactory factory = new HTTPClientFactory();
        HttpClient client= factory.getPoolableHttpClient(10,10,10);
        Assert.assertTrue(client.getConnectionManager() instanceof PoolingClientConnectionManager);
    }
}
