package com.elitecore.core.systemx.esix.http;

/**
 * EndPoint implementation for the HTTP connection
 *
 * @author Kartik Prajapati
 */

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.EndPoint;
import com.elitecore.core.systemx.esix.TaskScheduler;
import org.apache.commons.net.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class HTTPConnector extends ESCommunicatorImpl implements EndPoint{

    public static final String MODULE = "HTTP-CONNECTOR";
    private InetAddress[] addresses;

    private static final int CONNECTION_TIME_OUT=3000;

    private HttpClient httpClient = null;
    private int statusCheckDuration =10;
    private int port = 80;

    private String encodedCredentials=null;

    private Map<String, String> endPointDetails;

    private InetAddress reachableAddress;
    private String name;
    private String contextPath="";
    private Scanner scanner;

    public HTTPConnector(String name, InetAddress address,int port, String contextPath,HttpClient httpClient ,@Nullable TaskScheduler scheduler, int statsCheckDuration, Scanner scanner){
        this(name, port, contextPath, statsCheckDuration, httpClient, scheduler, scanner, address);
    }

    public HTTPConnector(String name, int port, String contextPath, int statusCheckDuration, HttpClient httpClient, @Nullable TaskScheduler scheduler, Scanner scanner, InetAddress... addresses){
        super(scheduler);
        this.name = name;
        this.contextPath = contextPath;
        this.addresses = addresses;
        this.statusCheckDuration = statusCheckDuration;
        this.endPointDetails = new HashMap();
        this.port = port;
        this.httpClient = httpClient;
        this.scanner = scanner;
    }

    /**
     * <PRE>
     * 		calls HTTP API and returns HttpResponse. Response can be null.
     * 	No checks(i.e. validate reset intervals or reset balance) are performed on the result.
     * </PRE>
     *
     * @throws CommunicationException
     *             in case of a problem (i.e. the server connection was aborted or encoding problem)
     * @param method Server information and arguments
     */
    @Override
    public @Nullable Object submit(RemoteMethod method) throws CommunicationException {

        if(method==null){
            throw new CommunicationException("Aborting request. Reason: method value is null.");
        }

        if(isAlive()==false){
            scan();
            throw new CommunicationException("Error while executing request, Try again later. Reason: Server is not reachable");
        }

        if(reachableAddress==null){
            throw new CommunicationException("Aborting request. Reason: connector has not been" +
                    " initialized or has not been scanned for aliveness.");
        }

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Invoking method: " + method.getName());
        }

        HttpResponse response;
        StringBuilder uri = new StringBuilder("http://")
                .append(reachableAddress.getHostAddress())
                .append(":")
                .append(port).append(contextPath)
                .append(method.getBaseURI())
                .append(method.getName());
        if(HTTPMethodType.GET.equals(method.getMethodType())){
            response = executeGet(uri,method.getArguments());
        }else{
            response = executePost(uri,method.getArguments());
        }

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Getting Result : " + (response==null?null:response.toString()));
        }

        return response;

    }

    private List<NameValuePair> setNameValuePairs(Map<String, String> arguments){
        List<NameValuePair> parameters = new ArrayList<>();

        if(arguments!=null) {
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        return parameters;
    }

    public void withBasicAuthentication(String username, String password){
        encodedCredentials = new String(Base64.encodeBase64((username+":"+password).getBytes()));
    }

    private HttpResponse executeGet(StringBuilder uri, Map<String, String> arguments) throws CommunicationException {

        if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
            LogManager.getLogger().info(MODULE, "HTTP Get Request URI : " + uri);
        }
        if(uri.charAt(uri.length()-1)!='?'){
            uri.append("?");
        }

        uri.append(URLEncodedUtils.format(setNameValuePairs(arguments), CommonConstants.UTF8));

        HttpGet request = new HttpGet(uri.toString());

        HttpResponse response;

        try {
            if(encodedCredentials!=null){
                request.setHeader("Authorization", "Basic " + encodedCredentials);
            }

            response = httpClient.execute(request);
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while accessing server. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

            markDead();

            throw new CommunicationException("Error while accessing server. Reason: "+e.getMessage(),e);
        }
        return response;
    }

    private HttpResponse executePost(StringBuilder uri, Map<String, String> arguments) throws CommunicationException {
        if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
            LogManager.getLogger().info(MODULE, "HTTP POST Request URI : " + uri);
        }

        HttpPost request = new HttpPost(uri.toString());
        HttpResponse response;
        try {
            request.setEntity(new UrlEncodedFormEntity(setNameValuePairs(arguments), CommonConstants.UTF8));
            if(encodedCredentials!=null){
                request.setHeader("Authorization", "Basic " + encodedCredentials);
            }

            response = httpClient.execute(request);
        } catch (UnsupportedEncodingException e) {
            getLogger().error(MODULE, "Error while encoding/decoding. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            throw new CommunicationException("Error while encoding/decoding request. Reason: "+e.getMessage(),e);
        } catch (IOException e) {
            getLogger().error(MODULE, "Error while accessing server. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

            markDead();

            throw new CommunicationException("Error while accessing server. Reason: "+e.getMessage(),e);
        } 
        return response;
    }

    public void addEndPointDetail(String key, String value){
        endPointDetails.put(key,value);
    }

    @Override
    public void init() throws InitializationFailedException{
        super.init();
        scan();
    }

    @Override
    protected int getStatusCheckDuration() {
        return statusCheckDuration;
    }

    @Override
    public void scan(){
        reachableAddress = getReachableAddress();

        if(reachableAddress == null || scanner==null || scanner.isAccessible(reachableAddress.getHostAddress(),port)==false){
            markDead();
            return;
        }

        markAlive();

    }

    private InetAddress getReachableAddress(){
        for (InetAddress address : addresses) {
            try {
                if(address.isReachable(CONNECTION_TIME_OUT)){
                    return address;
                }
            } catch (IOException e){
                getLogger().error(MODULE, "Unable to check status for server accessibility. Reason: " + e.getMessage());
                getLogger().trace(e);
            }
        }

        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTypeName() {
        return "HTTP";
    }

    public String getContextPath() {
        return contextPath;
    }
    public int getPort() {
        return port;
    }
    public InetAddress[] getAddresses(){
        return addresses;
    }

}
