package com.elitecore.nvsmx.ws.internal;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by aditya on 5/22/17.
 */
@Path("/")
public class ServerStatusWS {

    private static final String MODULE = "SERVER-STATUS-WS";
    private EndPointManager endPointManager ;

    public ServerStatusWS(EndPointManager endPointManager) {
        this.endPointManager = endPointManager;
    }

    public static ServerStatusWS create(){
        return new ServerStatusWS(EndPointManager.getInstance());
    }

    @GET
    @Path("/serverStatus/{id}")
    @Produces({MediaType.TEXT_PLAIN })
    public String getStatus(@PathParam("id") String id){
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE, "Calling getStatus method");
        }
        remoteWakeUp(id);
        return LifeCycleState.RUNNING.name();

    }


    @GET
    @Path("/remoteShutDown/{id}")
    @Produces({MediaType.TEXT_PLAIN })
    public String remoteShutDown(@PathParam(value = "id")String id){
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"remote shutdown call received");
        }

        if(Strings.isNullOrBlank(id)){
            LogManager.getLogger().error(MODULE, "Error while marking remote instance as shut down. Reason: no instance id found");
            return ResultCode.INPUT_PARAMETER_MISSING.name();
        }
        endPointManager.markShutDown(id);
        return ResultCode.SUCCESS.name();
    }

    @GET
    @Path("/remoteWakeUp/{id}")
    @Produces({MediaType.TEXT_PLAIN })
    public String remoteWakeUp(@PathParam(value = "id")String id){
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"remote wakeup call received");
        }
        if(Strings.isNullOrBlank(id)){
            LogManager.getLogger().error(MODULE, "Error while marking remote instance as started. Reason: no instance id found");
            return ResultCode.INPUT_PARAMETER_MISSING.name();
        }
        endPointManager.markStarted(id);
        return ResultCode.SUCCESS.name();
    }
}
