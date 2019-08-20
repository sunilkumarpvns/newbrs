package com.elitecore.nvsmx.ws.internal;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.ws.reload.blmanager.ReloadPolicyBlManager;
import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by aditya on 4/19/17.
 * This class contain private webservices
 */
@Path("/")
public class ReloadPolicyWS {
    private static final String MODULE = "REST-RELOAD-POLICY-WS";
    public static final String SUCCESS = "SUCCESS";
    private ReloadPolicyBlManager reloadPolicyBlManager;
    private Gson gson;

    public ReloadPolicyWS(ReloadPolicyBlManager reloadPolicyBlManager, Gson gson) {
        this.reloadPolicyBlManager = reloadPolicyBlManager;
        this.gson = gson;
    }


    public static ReloadPolicyWS create(){
        Gson gson = GsonFactory.defaultInstance();
        return new ReloadPolicyWS(new ReloadPolicyBlManager(RMIGroupManager.getInstance(), EndPointManager.getInstance(), DefaultNVSMXContext.getContext().getPolicyRepository()),gson);

    }

    @GET
    @Path("/reload/allPolicies")
    @Produces({MediaType.TEXT_PLAIN})
    public String reloadAllPolicy(){
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"Calling reloadAllPolicy");
        }


        String reloadResponse = gson.toJson(reloadPolicyBlManager.reloadAllOwnPolicy());
        try {
            return URLEncoder.encode(reloadResponse, CommonConstants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LogManager.getLogger().error(MODULE,"Unable to encode response: "+reloadResponse+" . Reason:"+ e.getMessage());
            LogManager.getLogger().trace(MODULE,e);
        }
        return CommonConstants.EMPTY_STRING;
    }

    @GET
    @Path("/reload/reloadOwnDataSliceConfiguration")
    @Produces({MediaType.TEXT_PLAIN})
    public String reloadDataSliceConfiguration() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"Calling reloadPolicies");
        }
        return reloadPolicyBlManager.reloadOwnDataSliceConfiguration();
    }

    @POST
    @Path("/reload/policies")
    @Produces({MediaType.TEXT_PLAIN})
    public String reloadPolicies(String ids){
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"Calling reloadPolicies");
        }

        if (Strings.isNullOrBlank(ids)) {
            LogManager.getLogger().error(MODULE,"Unable to reload polices.Reason: no package id found");
            return CommonConstants.EMPTY_STRING;
        }
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading packages with Ids: " + ids);
        }

        try {
            ids = URLDecoder.decode(ids, CommonConstants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LogManager.getLogger().error(MODULE,"Unable to decode arguments: "+ids+" . Reason:"+ e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
            return CommonConstants.EMPTY_STRING;
        }

        String reloadResponse = gson.toJson(reloadPolicyBlManager.reloadOwnPoliciesByNames(ids));
        try {
            return URLEncoder.encode(reloadResponse, CommonConstants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LogManager.getLogger().error(MODULE,"Unable to encode response: " + reloadResponse +" . Reason:"+ e.getMessage());
            LogManager.getLogger().trace(MODULE,e);

        }
        return CommonConstants.EMPTY_STRING;
    }
    @POST
    @Path("/reload/policiesByGroups")
    @Produces({MediaType.TEXT_PLAIN})
    public String reloadPoliciesByGroups(String staffGroupIds){
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"Calling reloadPoliciesByGroups");
        }

        if (Strings.isNullOrBlank(staffGroupIds)) {
            LogManager.getLogger().error(MODULE,"Unable to reload polices.Reason: no staff group id found");
            return CommonConstants.EMPTY_STRING;
        }
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading packages with staff groups: " + staffGroupIds);
        }

        try {
            staffGroupIds = URLDecoder.decode(staffGroupIds, CommonConstants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LogManager.getLogger().error(MODULE,"Unable to decode arguments: "+ staffGroupIds +" . Reason:"+ e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
            return CommonConstants.EMPTY_STRING;
        }

        String reloadResponse = gson.toJson(reloadPolicyBlManager.reloadOwnPoliciesByGroups(staffGroupIds));
        try {
            return URLEncoder.encode(reloadResponse, CommonConstants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LogManager.getLogger().error(MODULE,"Unable to encode response: " + reloadResponse +" . Reason:"+ e.getMessage());
            LogManager.getLogger().trace(MODULE,e);

        }
        return CommonConstants.EMPTY_STRING;
    }
}
