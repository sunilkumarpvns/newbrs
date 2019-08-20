package com.elitecore.corenetvertex.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.mockito.Mockito;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Created by harsh on 2/8/17.
 */
public class TestableHttpClient implements HttpClient {

    private ClientConnectionManager clientConnectionManager = Mockito.mock(ClientConnectionManager.class);
    private BasicHttpParams basicHttpParams = new BasicHttpParams();
    private Set<String> methodNameToParameters = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private Map<String, MethodResponse> methodNameToResponse = new ConcurrentHashMap<String, MethodResponse>();
    private boolean interaction = false;
    private String contextPath;

    public TestableHttpClient(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public HttpParams getParams() {
        return basicHttpParams;
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return clientConnectionManager;
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException, ClientProtocolException {
        interaction = true;

        System.out.println("MEthod is : " + httpUriRequest.getMethod());

        if ("POST".equalsIgnoreCase(httpUriRequest.getMethod())) {
            HttpPost httpPost  = (HttpPost) httpUriRequest;
            StringEntity entity = (StringEntity) httpPost.getEntity();
            String content = IOUtils.toString(entity.getContent());
            methodNameToParameters.add(httpUriRequest.getURI().getPath() + "/" + URLDecoder.decode(content, "UTF-8"));

        } else {
        methodNameToParameters.add(httpUriRequest.getURI().getPath());
        }


        MethodResponse methodResponse = methodNameToResponse.get(httpUriRequest.getURI().getPath());

        try {

            if (methodResponse != null) {
                return methodResponse.createHttpResponse(httpUriRequest);
            } else {
                return new BasicHttpResponse(new BasicStatusLine(httpUriRequest.getProtocolVersion(), 404, ""));
            }

        } finally {
             synchronized (methodNameToParameters) {
                 methodNameToParameters.notifyAll();
             }
        }



    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        //TODO if required then implements
        throw new UnsupportedOperationException(" Not supported currently");

    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException, ClientProtocolException {
        //TODO if required then implements
        throw new UnsupportedOperationException(" Not supported currently");
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        //TODO if required then implements
        throw new UnsupportedOperationException(" Not supported currently");
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        //TODO if required then implements
        throw new UnsupportedOperationException(" Not supported currently");
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        //TODO if required then implements
        throw new UnsupportedOperationException(" Not supported currently");
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        //TODO if required then implements
        throw new UnsupportedOperationException(" Not supported currently");
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        //TODO if required then implements
        throw new UnsupportedOperationException(" Not supported currently");
    }

    public void checkNoInteraction() {


        if (interaction  == false){
            return;
        }

        StringBuilder builder = new StringBuilder("Expected:Http Method should not be called. Actual: Method called are");
        createMethodCallString(builder);

        assertTrue(builder.toString(), false);

    }

    private void createMethodCallString(StringBuilder builder) {
        for (String value : methodNameToParameters) {
            builder.append(value).append(",");
        }
    }

    public void reset() {
        interaction = false;
        methodNameToParameters.clear();
    }

    public void checkPathCalled(String queryString) {



        if(methodNameToParameters.contains(contextPath + queryString)) {
            return;
        }

        StringBuilder builder = new StringBuilder("Expected:Http Get Method should be called with path:");
        builder.append(contextPath).append(queryString).append(". Actual: ");

        if(methodNameToParameters.isEmpty()) {
            builder.append("No method called");
        } else {
            builder.append("Method called are ");
            createMethodCallString(builder);
        }

        assertTrue(builder.toString(), false);

    }

    public void setResponseForPath(String path, int statusCode, String statusMessage, Serializable serializable) throws IOException {
        methodNameToResponse.put(contextPath + path, new MethodResponse(statusCode, statusMessage, new SerializableEntity(serializable, false)));
    }

    public void setResponseForPath(String path, int statusCode, String statusMessage, String obj) throws UnsupportedEncodingException {
        methodNameToResponse.put(contextPath + path, new MethodResponse(statusCode, statusMessage, new StringEntity(obj)));
    }

    public void throwExceptionOnAnyCall(String path, Exception exception) {
        methodNameToResponse.put(contextPath + path, new MethodResponse(exception));
    }

    public void waitTillCallReceived(String path, long waitTime) throws InterruptedException, TimeoutException {
        long endtime = System.currentTimeMillis() + waitTime;

        while(endtime > System.currentTimeMillis()) {
            if(methodNameToParameters.contains(contextPath + path)) {
                return;
            }

            long remainingTime = endtime - System.currentTimeMillis();

            if(remainingTime <= 0) {
                throw new TimeoutException("Call:" + path + " not received within " + waitTime + "ms");
            }

            synchronized (methodNameToParameters) {
                if(methodNameToParameters.contains(contextPath + path) == false) {
                    methodNameToParameters.wait(endtime - System.currentTimeMillis());
                }
            }
        }

        if(methodNameToParameters.contains(contextPath + path) == false) {
            throw new TimeoutException("Call:" + path + " not received within " + waitTime + "ms");
        }

    }

    public void checkNoInteraction(String path) {
        assertFalse(methodNameToParameters.contains(contextPath + path));
    }

    public void checkClosed() {
        verify(clientConnectionManager).shutdown();
    }


    private class MethodResponse {
        private int statusCode;
        private String statusMessage;
        private HttpEntity httpEntity;
        private Exception exception;

        public MethodResponse(int statusCode, String statusMessage, HttpEntity httpEntity) {
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
            this.httpEntity = httpEntity;
        }

        public MethodResponse(Exception exception) {
            this.exception = exception;
        }

        public HttpResponse createHttpResponse(HttpUriRequest httpUriRequest) throws ClientProtocolException {

            if (exception == null) {
                final BasicHttpResponse basicHttpResponse = new BasicHttpResponse(new BasicStatusLine(httpUriRequest.getProtocolVersion(), statusCode, statusMessage));
                basicHttpResponse.setEntity(httpEntity);
                return basicHttpResponse;
            } else {
                throw new ClientProtocolException(exception);
            }

        }
    }
}
