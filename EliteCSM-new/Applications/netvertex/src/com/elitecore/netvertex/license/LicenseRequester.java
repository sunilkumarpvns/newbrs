package com.elitecore.netvertex.license;

import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.http.EndPointManager;
import com.elitecore.core.systemx.esix.http.HTTPConnector;
import com.elitecore.core.systemx.esix.http.HTTPConnectorGroupImpl;
import com.elitecore.core.systemx.esix.http.HTTPMethodType;
import com.elitecore.core.systemx.esix.http.RemoteMethod;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.commons.LicenseMessages;
import com.elitecore.license.publickey.ElitePublickeyGenerator;
import com.elitecore.license.util.SystemUtil;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.alerts.Alerts;
import com.elitecore.netvertex.escommunication.data.PDInstanceConfiguration;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Requests license from SM
 *
 * @author Kartik Prajapati
 */
public class LicenseRequester extends BaseIntervalBasedTask {

    private static final String MODULE = "LICENSE-REQUESTER";
    private static final long INITIAL_DELAY = 3600; //Initial delay is kept at 3600, first license request will be sent manually when initializing services.
    private static final long INTERVAL_SECONDS = 3600;
    private NetVertexServerContext serverContext;
    private HTTPConnectorGroupImpl pdConnectorGroup;
    private String validationKey;
    private RemoteMethod remoteMethod;

    public LicenseRequester(NetVertexServerContext serverContext){
        this.serverContext = serverContext;
        validationKey = new ElitePublickeyGenerator(ElitePublickeyGenerator.PLAIN_TEXT_FORMAT)
                .generatePublicKey(serverContext.getServerHome(),
                        LicenseConstants.DEFAULT_ADDITIONAL_KEY,
                        serverContext.getServerInstanceId(),
                        serverContext.getServerInstanceName());
        remoteMethod = new RemoteMethod("/integration/license/license/",serverContext.getServerInstanceId()+"/getLicense.json",HTTPMethodType.GET);

        remoteMethod.addArgument("instanceName",serverContext.getServerInstanceName());
        remoteMethod.addArgument("id",serverContext.getServerInstanceId());
        remoteMethod.addArgument("ip", getIPAddress());
        remoteMethod.addArgument("key",validationKey);
    }

    public void initPDConnectorGroup(){
        pdConnectorGroup = new HTTPConnectorGroupImpl();
        List<PDInstanceConfiguration> pdInstanceConfigurationList =serverContext.getServerConfiguration().getPDInstanceConfiguration();

        for(PDInstanceConfiguration pdInstanceConfiguration: pdInstanceConfigurationList){
            HTTPConnector connector = EndPointManager.getInstance().getEndPoint(pdInstanceConfiguration.getId());
            if(connector!=null){
                pdConnectorGroup.addCommunicator(connector);
            }
        }
    }

    private String getIPAddress(){
        String ipAddress = null;
        try {
            ipAddress = SystemUtil.getIPAddress();
        } catch (UnknownHostException e){
            getLogger().error(MODULE, "Could not get host IP address. " +
                    "Reason: "+e.getMessage());
            getLogger().trace(e);
        }
        return  ipAddress;
    }

    @Override
    public long getInitialDelay() {
        return INITIAL_DELAY;
    }

    @Override
    public boolean isFixedDelay() {
        return true;
    }

    @Override
    public void execute(AsyncTaskContext context) {
        if(pdConnectorGroup == null){
            if(getLogger().isWarnLogLevel()){
                getLogger().warn(MODULE, "Not executing the task. " +
                        "Reason: Connector group has not been initialized.");
            }
            return;
        }

        try {
            HttpResponse httpResponse = (HttpResponse)pdConnectorGroup.submit(remoteMethod);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            HttpEntity entity = httpResponse.getEntity();
            String responseString = new BufferedReader(new InputStreamReader(entity.getContent())).readLine();

            if (statusCode == LicenseMessages.SUCCESS.getMessageCode()) {

                JsonParser parser = new JsonParser();
                JsonObject licenseResponse = parser.parse(responseString).getAsJsonObject();
                LicenseMessages responseCode = LicenseMessages.getByMessageCode(licenseResponse.get("messageCode").getAsInt());
                if(LicenseMessages.SUCCESS != responseCode){
                    getLogger().warn("LICENSE-REQUESTER", "License request failed. " +
                            "Reason: Request failed with code "+licenseResponse.get("messageCode").getAsString()+" and message: "+licenseResponse.get("message").getAsString());
                }
                serverContext.uploadLicense(
                        isNullOrAbsent(licenseResponse,"messageCode")==false?licenseResponse.get("messageCode").getAsInt():null,
                        isNullOrAbsent(licenseResponse,"message")==false?licenseResponse.get("message").getAsString():null,
                        isNullOrAbsent(licenseResponse,"licenseKey")==false?licenseResponse.get("licenseKey").getAsString():null);

            } else {
                serverContext.uploadLicense(LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessageCode() ,LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessage(), null);
                getLogger().warn(MODULE, "License request failed. " +
                        "Reason: Request failed with code "+statusCode+" and response "+responseString);
                serverContext.generateSystemAlert(AlertSeverity.CRITICAL, Alerts.LICENSE_REQUEST_FAILED, MODULE,
                        "NetVertex is not able to connect to the Server Manager for license purpose. " +
                                "If this behaviour continues for longer period of time, NetVertex will be forced to stop.");
            }
        } catch (Exception e) {
            serverContext.uploadLicense(LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessageCode() ,LicenseMessages.FAILED_TO_CONNECT_WITH_SM_SERVER.getMessage(), null);
            getLogger().error(MODULE, "Exception occurred. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

            serverContext.generateSystemAlert(AlertSeverity.CRITICAL, Alerts.LICENSE_REQUEST_FAILED, MODULE,
                    "NetVertex is not able to connect to the Server Manager for license purpose. " +
                            "If this behaviour continues for longer period of time, NetVertex will be forced to stop.");
        }
    }

    private boolean isNullOrAbsent(JsonObject licenseResponse, String key){
        if(licenseResponse.has(key)==false){
            return true;
        }
        return licenseResponse.get(key).isJsonNull()? true : false;
    }

    public long getInterval() {
        return (int) INTERVAL_SECONDS;
    }
}
