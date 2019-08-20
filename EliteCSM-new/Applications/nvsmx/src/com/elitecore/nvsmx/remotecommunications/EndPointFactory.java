package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.systeminformation.PDContextInformation;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;

import java.util.concurrent.ExecutorService;

/**
 * Created by aditya on 5/3/17.
 */
public class EndPointFactory {

    private static final String MODULE = "END-POINT-FACTORY";
    private TaskScheduler taskScheduler;
    private ExecutorService executorService;

    public EndPointFactory(TaskScheduler taskScheduler, ExecutorService executorService) {
        this.taskScheduler = taskScheduler;
        this.executorService = executorService;
    }
    //to create netveretx end points
    public EndPoint  createEndPoint(ServerGroupData serverInstanceGroupData, ServerInstanceData primaryServerInstanceData) throws InitializationFailedException {
        HTTPConnector httpConnector =  new HTTPConnector(taskScheduler,ServerInformation.from(primaryServerInstanceData), ServerInformation.from(serverInstanceGroupData), RemoteMethodConstant.NETVERTEX_RELOAD_CONFIGURATION_BASE_PATH_URI, executorService, primaryServerInstanceData.getRestApiUrl());
        httpConnector.init();
        return httpConnector;
    }

    //to create NVSMX end points
    public NVSMXEndPoint createNVSMXEndPoint(final PDContextInformation pdContext, String localPdContextId) throws InitializationFailedException {
        ServerInformation serverInstanceData = new ServerInformation(pdContext.getName(), pdContext.getId(), pdContext.getId());
        ServerInformation serverInstanceGroupData = new ServerInformation(pdContext.getName(), pdContext.getId(), pdContext.getId());
        DynamicHTTPConnector httpConnector = new DynamicHTTPConnector(pdContext,
                RemoteMethodConstant.NVSMX_STATUS_REST_PATH,
                serverInstanceData,
                serverInstanceGroupData, taskScheduler, executorService,localPdContextId);
        httpConnector.init();
        return httpConnector;

    }

    public void shutDown(){
        if (LogManager.getLogger().isInfoLogLevel()) {
            LogManager.getLogger().info(MODULE, "Shutting down end point factory");
        }
        this.executorService.shutdown();

    }
}
