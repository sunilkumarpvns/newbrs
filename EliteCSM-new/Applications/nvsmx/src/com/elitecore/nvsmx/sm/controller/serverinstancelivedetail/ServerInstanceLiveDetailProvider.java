package com.elitecore.nvsmx.sm.controller.serverinstancelivedetail;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.ServerInfo;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.remotecommunications.RMIGroup;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;
import com.elitecore.nvsmx.remotecommunications.RemoteMessageCommunicator;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.net.URLDecoder;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ServerInstanceLiveDetailProvider {

    private static  final String MODULE = "SRV-INST-LIVE-DETAIL-PROVIDER";
    private static final String UTF_8 = "UTF-8";
    private RMIGroup rmiGroup = null;
    private ServerInstanceData serverInstanceData;

    public ServerInstanceLiveDetailProvider(ServerInstanceData serverInstanceData){
        this.serverInstanceData = serverInstanceData;
        this.rmiGroup = RMIGroupManager.getInstance().getRMIGroupFromServerCode(serverInstanceData.getId());
    }

    public String getLiveDataFor(String webserviceName){
        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called getLiveDataFor()");
            getLogger().debug(MODULE, "Calling WebService: "+webserviceName);
        }
        try {
            RMIResponse<String> rmiResponse = RemoteMessageCommunicator.callSync(
                    new RemoteMethod(RemoteMethodConstant.NETVERTEX_RELOAD_CONFIGURATION_BASE_PATH_URI,webserviceName,"", HTTPMethodType.GET),
                    rmiGroup, serverInstanceData.getId());

            if(getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Received Response : " + rmiResponse.getResponse());
            }

            if(Strings.isNullOrBlank(rmiResponse.getResponse())==false && ResultCode.INVALID_INPUT_PARAMETER.name().equals(rmiResponse.getResponse())==false) {
                return URLDecoder.decode(rmiResponse.getResponse(),UTF_8);
            }
        }catch(Exception ex){
            getLogger().error(MODULE,"Error while making REST call to get "+webserviceName+" for ServerInstance '"+serverInstanceData.getName()+"'. Reason: "+ex.getMessage());
            getLogger().trace(MODULE, ex);
        }
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(MODULE,"Returning Null response");
        }
        return null;
    }

    public static String getRestApiUrl(String restApiUrl){

        StringBuilder restUrl = new StringBuilder("http://");
        restUrl.append(restApiUrl);
        restUrl.append(RemoteMethodConstant.NETVERTEX_RELOAD_CONFIGURATION_BASE_PATH_URI);

        return restUrl.toString();
    }

    /* This method is used while updating ServerInstance.
       It is used to make sure, the server is alive on the given REST:IP port or not ! */
    public static ServerInfo getRestIpPortStatus(String restApiUrl){
        try {
            String path = getRestApiUrl(restApiUrl) + "/serverInfo";
            HttpGet postRequest = new HttpGet(path);

            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpResponse httpResponse = httpClient.execute(postRequest);
            if(httpResponse==null || httpResponse.getStatusLine().getStatusCode()!=200){
                return null;
            }

            HttpEntity httpEntity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(httpEntity, UTF_8);
            String decodedResponse = URLDecoder.decode(responseString,UTF_8);

            if(!Strings.isNullOrBlank(decodedResponse)){
                return GsonFactory.defaultInstance().fromJson(decodedResponse, ServerInfo.class);
            }
        }catch(Exception ex){
            getLogger().error(MODULE,"Error while checking REST IP:PORT status. Reason: "+ex.getMessage());
            getLogger().trace(MODULE, ex);
        }
        return null;
    }
}
