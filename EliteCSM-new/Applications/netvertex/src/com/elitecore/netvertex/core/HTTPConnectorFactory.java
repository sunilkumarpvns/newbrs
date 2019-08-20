package com.elitecore.netvertex.core;

import com.elitecore.commons.net.AddressResolver;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.systemx.esix.http.HTTPClientFactory;
import com.elitecore.core.systemx.esix.http.HTTPConnector;
import com.elitecore.core.systemx.esix.http.HTTPScanner;
import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HTTPConnectorFactory {

    private static final int MAX_POOL = 100;
    private static final int CONNECTION_TIME_OUT = 3000;
    private static final int READ_TIME_OUT = 3000;

    private TaskScheduler taskScheduler;
    private AddressResolver addressResolver;
    private HTTPClientFactory clientFactory;
    private String contextPath;

    public HTTPConnectorFactory(TaskScheduler taskScheduler,
                                AddressResolver addressResolver,
                                HTTPClientFactory clientFactory,
                                String contextPath) {
        this.taskScheduler = taskScheduler;
        this.addressResolver = addressResolver;
        this.clientFactory = clientFactory;
        this.contextPath = contextPath;
    }


    public HTTPConnector create(String name, String restIpAddress, int restPort) throws InitializationFailedException {
        InetAddress inetAddress;
        try {
            inetAddress = addressResolver.byName(restIpAddress);
        } catch (UnknownHostException e) {
            throw new InitializationFailedException("Unable to resolve rest address: " + restIpAddress + " of " + name + " server", e);
        }


        HttpClient httpClient = clientFactory.getPoolableHttpClient(MAX_POOL, CONNECTION_TIME_OUT, READ_TIME_OUT);
        HTTPConnector httpConnector = new HTTPConnector(name,
                inetAddress,
                restPort,
                "/"+contextPath,
                httpClient,
                taskScheduler,
                60,
                new HTTPScanner());

            httpConnector.init();
            return httpConnector;
    }

}
