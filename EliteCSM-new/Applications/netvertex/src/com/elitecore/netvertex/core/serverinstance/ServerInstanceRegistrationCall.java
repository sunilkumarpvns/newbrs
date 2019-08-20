package com.elitecore.netvertex.core.serverinstance;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.http.HTTPConnector;
import com.elitecore.core.systemx.esix.http.RemoteMethod;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceRegistrationRequest;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.netvertex.core.HTTPConnectorFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ServerInstanceRegistrationCall {
    public static final String MODULE = "SERVER-INSTANCE-REGISTRATION-CALL";
    private int smPort;
    private String smIp;
    private HTTPConnectorFactory httpConnectorFactory;

    public ServerInstanceRegistrationCall(int smPort,
                                          String smIp,
                                          HTTPConnectorFactory httpConnectorFactory){

        this.smIp=smIp;
        this.smPort=smPort;
        this.httpConnectorFactory = httpConnectorFactory;
    }

    public ServerInstanceRegistrationRequest callToSm(RemoteMethod remoteMethod) throws  InitializationFailedException,IOException,CommunicationException{
            HTTPConnector httpConnector = httpConnectorFactory.create("ServerRegistration",smIp,smPort);
            httpConnector.withBasicAuthentication(LicenseConstants.INTEGRATION_USER, LicenseConstants.INTEGRATION_SECRET);
            HttpResponse submit = (HttpResponse) httpConnector.submit(remoteMethod);
            return convertoResponse(submit);
    }

    private ServerInstanceRegistrationRequest convertoResponse(HttpResponse httpResponse) throws  IOException,CommunicationException {
        JsonObject responseObject;
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String responseString = handleResponse(httpResponse);

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Response from SM after Server Instance Registration:" + responseString);
        }

        if(responseString == null){
            return null;
        }

        if (statusCode != ResultCode.SUCCESS.code) {
            if(getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Call to SM request failed. Reason: Request failed with code " + statusCode + " and response " + responseString);
            }
            return null;
        }

        JsonParser parser = new JsonParser();
        responseObject = parser.parse(responseString).getAsJsonObject();
        if (responseObject == null) {
                return null;
        }
        int messageCode = responseObject.get("messageCode").getAsInt();
        if (messageCode == ResultCode.SUCCESS.code) {
            ServerInstanceRegistrationRequest serverInstanceRegistrationRequest = new ServerInstanceRegistrationRequest();
            serverInstanceRegistrationRequest.setServerInstanceId(responseObject.get("serverInstanceId").getAsString());
            serverInstanceRegistrationRequest.setServerName(responseObject.get("serverName").getAsString());
            serverInstanceRegistrationRequest.setUserName(responseObject.get("userName").getAsString());
            serverInstanceRegistrationRequest.setPassword(responseObject.get("password").getAsString());
            serverInstanceRegistrationRequest.setConnectionUrl(responseObject.get("connectionUrl").getAsString());
            serverInstanceRegistrationRequest.setDriverClassName(responseObject.get("driverClassName").getAsString());
            serverInstanceRegistrationRequest.setMaxTotal(responseObject.get("maxTotal").getAsInt());
            serverInstanceRegistrationRequest.setMaxIdle(responseObject.get("maxIdle").getAsInt());
            serverInstanceRegistrationRequest.setQueryTimeout(responseObject.get("queryTimeout").getAsInt());
            return serverInstanceRegistrationRequest;
        }
        return null;
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
}
