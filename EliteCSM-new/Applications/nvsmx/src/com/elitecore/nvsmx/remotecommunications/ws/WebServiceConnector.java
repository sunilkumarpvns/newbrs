package com.elitecore.nvsmx.remotecommunications.ws;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.remotecommunications.exception.CommunicationException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * This class is responsible to communicate with Rest Server which is hosted
 * at NetVertex Server, through the creating Rest Client
 * Created by aditya on 9/13/16.
 */
public class WebServiceConnector {

    /**
     *
     */
    private String MODULE;
    private String restServerAddress;
    private HttpClient httpClient;
    private ExecutorService threadPoolExecutor;

    private static final int MAX_POOL=100;
    private static final int CONNECTION_TIME_OUT=3000;
    private static final int READ_TIME_OUT=3000;

    public WebServiceConnector(String name,String restServerAddress, ExecutorService executorService){
        this.restServerAddress = restServerAddress;
        this.threadPoolExecutor = executorService;
        this.MODULE = "WEBSERVICE-CONCTR-"+name + "@" + restServerAddress;
    }




    private <V> Future<V> submit(Callable<V> callable) throws CommunicationException {
        return threadPoolExecutor.submit(callable);
    }

    /**
     * To call web service
     * @param method
     * @param <V>
     * @return
     * @throws CommunicationException
     */
    public <V> Future<V> call(RemoteMethod method) throws CommunicationException {
        if(getLogger().isDebugLogLevel()){
           getLogger().debug(MODULE,"Calling web service "+ method.getName()+" with parameter "+ method.getArgument());
        }
        WebServiceMethods webServiceMethod = WebServiceMethods.of(method.getName());
        if(LogManager.getLogger().isDebugLogLevel()){
            if(webServiceMethod !=null)
                getLogger().debug(MODULE, "Invoking method: " + webServiceMethod.getMethodURL());

        }

        return submit(new RemoteMethodInvoker<V>(webServiceMethod,method.getArgument(),method.getBaseURI(),method.getMethodType()));
    }

    public void init() {
        initHTTPConnectionPool();
    }


    private class RemoteMethodInvoker<T> implements  Callable<T> {

        private WebServiceMethods webServiceMethod;
        private String params;
        private String baseURI;
        private HTTPMethodType httpMethodType;

        public RemoteMethodInvoker(@Nonnull WebServiceMethods webServiceMethod, String params, String baseURI, HTTPMethodType httpMethodType) {
            this.webServiceMethod = webServiceMethod;
            this.params = params;
            this.baseURI = baseURI;
            this.httpMethodType = httpMethodType;
        }


        @SuppressWarnings("unchecked")
        @Override
        public T call() throws Exception {
            if(getLogger().isDebugLogLevel()) {
                if(webServiceMethod !=null)
                getLogger().debug(MODULE, "Invoking method: " + webServiceMethod.getMethodURL());
            }
            String responseString;
            StringBuilder uri = new StringBuilder("http://").append(restServerAddress).append(baseURI).append(webServiceMethod.getMethodURL());
            if(HTTPMethodType.GET.equals(httpMethodType)){
                if(Strings.isNullOrBlank(params) == false){
                    uri.append(URLEncoder.encode(params, CommonConstants.UTF_8));
                }
                responseString = executeGet(uri.toString());
            }else{
                responseString = executePOST(uri.toString(),params);
              }
            if (Thread.interrupted()){
            	if(getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Getting delayed response" );
                }
            	return null;
            }

            return (T) webServiceMethod.getTransformer().apply(responseString);
        }
    }


    private String executeGet (String uri) throws CommunicationException {
        HttpGet request = new HttpGet(uri);

        HttpResponse response = null;

        if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
            LogManager.getLogger().info(MODULE, "HTTP Get Request URI : " + request.getURI());
        try {
        	
        	if (Thread.interrupted()){
            	if(getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Http call to: " + uri +  " skipped. Reason: Task is cancelled");
                }
            	return null;
            } 
        	
            response = httpClient.execute(request);
        } catch (Exception e) {
        	
        	if (Thread.interrupted()) {
        		getLogger().error(MODULE, "Response delayed. Reason: " + e.getMessage());
        		getLogger().trace(MODULE, e);
        	} else {
        		throw new CommunicationException("Error while executing request. Reason: "+e.getMessage(),e);
        	}
        }

        
        return handleResponse(response);
    }

    private String executePOST (String uri,String ids) throws CommunicationException {
        if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
            LogManager.getLogger().info(MODULE, "HTTP POST Request URI : " + uri);
        }


        HttpPost request = new HttpPost(uri);
        HttpResponse response = null;
        try {
            request.setEntity(new StringEntity(URLEncoder.encode(ids,CommonConstants.UTF_8), CommonConstants.UTF_8));
            if (Thread.interrupted()){
                if(getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Http call: " + uri +  " skipped. Reason: Task is cancelled");
                }
                return null;
            }

            response = httpClient.execute(request);
        } catch (UnsupportedEncodingException e) {
            getLogger().error(MODULE, "Error while encoding/decoding. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            return null;
        }catch (Exception e) {
            if (Thread.interrupted()) {
                getLogger().error(MODULE, "Response delayed. Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            } else {
                throw new CommunicationException("Error while executing request. Reason: "+e.getMessage(),e);
            }
        }

        return handleResponse(response);
    }
    
    private String handleResponse(HttpResponse response) throws CommunicationException {
        try {
            StatusLine statusLine = response.getStatusLine();
            if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                LogManager.getLogger().debug(MODULE, "Http Response Code : " + statusLine.getStatusCode() + " and Reason Phrase: " + statusLine.getReasonPhrase());
            }
            HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300 || statusLine.getStatusCode() < 200) {
                EntityUtils.consume(entity);
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
            return entity == null ? null : EntityUtils.toString(entity);
        }catch (Exception e){
            throw new CommunicationException("Error while executing request.Reason: "+e.getMessage(),e);

        }
    }

    private void initHTTPConnectionPool() {
        PoolingClientConnectionManager poolingClientConnectionManager = new PoolingClientConnectionManager();
        httpClient = new DefaultHttpClient(poolingClientConnectionManager);
        poolingClientConnectionManager.setMaxTotal(MAX_POOL);
        poolingClientConnectionManager.setDefaultMaxPerRoute(MAX_POOL);
        httpClient.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, CONNECTION_TIME_OUT);
        httpClient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, READ_TIME_OUT);
    }

    public void shutDown(){
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"Shutting down connection manager for "+restServerAddress);
        }

        httpClient.getConnectionManager().shutdown();
    }
}