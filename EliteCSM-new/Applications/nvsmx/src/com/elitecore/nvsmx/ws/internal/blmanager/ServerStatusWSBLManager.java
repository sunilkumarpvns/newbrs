package com.elitecore.nvsmx.ws.internal.blmanager;

import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RemoteMessageCommunicator;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by aditya on 6/15/17.
 */
public class ServerStatusWSBLManager {
    private static final String MODULE = "SERVER-STATUS-WS-BL-MNGR";
    private EndPointManager endPointManager;

    public ServerStatusWSBLManager(EndPointManager endPointManager) {
        this.endPointManager = endPointManager;
    }


    public void announcingShutDown(String id){
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE,"Announcing shutdown for id "+id);
        }

        RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NVSMX_STATUS_REST_PATH,RemoteMethodConstant.NVSMX_REMOTE_SHUT_DOWN,id, HTTPMethodType.GET);
        RemoteMessageCommunicator.broadcast(endPointManager.getALLNvsmxEndPoints(), remoteMethod);
    }


    public void announcingWakeUp(String id){
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE,"Announcing wakeup for id "+id);
        }
        RemoteMethod remoteMethod = new RemoteMethod(RemoteMethodConstant.NVSMX_STATUS_REST_PATH,RemoteMethodConstant.NVSMX_REMOTE_WAKE_UP,id, HTTPMethodType.GET);
        RemoteMessageCommunicator.broadcast(endPointManager.getALLNvsmxEndPoints(), remoteMethod);
    }

}