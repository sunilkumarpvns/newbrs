package com.elitecore.nvsmx.ws.util;

import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RemoteMessageCommunicator;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.remotecommunications.ws.WebServiceMethods;

public class AlternateIdOperationUtils {

    private EndPointManager endPointManager;

    public AlternateIdOperationUtils(EndPointManager endPointManager) {
        this.endPointManager = endPointManager;
    }

    public static AlternateIdOperationUtils create(EndPointManager endPointManager){
        return new AlternateIdOperationUtils(endPointManager);
    }

    public void removeAlternateIdFromCache(String alternateId){
        RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_RELOAD_CONFIGURATION_BASE_PATH_URI, WebServiceMethods.NETVERTEX_REMOVE_ALTERNATE_ID_FROM_CACHE.name(),alternateId, HTTPMethodType.GET);
        RemoteMessageCommunicator.broadcast(endPointManager.getAllNetvertexEndPoint(), remoteMethod);
    }
}
