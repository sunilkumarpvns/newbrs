package com.elitecore.nvsmx.ws.reload;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.ws.reload.blmanager.ReloadPolicyBlManager;
import com.elitecore.nvsmx.ws.reload.response.ReloadDataSliceConfigurationResponse;
import com.elitecore.nvsmx.ws.reload.response.ReloadPolicyResponse;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by aditya on 4/18/17.
 */
@Path("/reload")
public class RestReloadPolicyWS {
    private static final String MODULE = "RELOAD-POLICY-WS";
    private ReloadPolicyBlManager reloadPolicyBlManager;


    public RestReloadPolicyWS(ReloadPolicyBlManager reloadPolicyBlManager){
        this.reloadPolicyBlManager = reloadPolicyBlManager;
    }

    public static RestReloadPolicyWS create(){
        return new RestReloadPolicyWS(new ReloadPolicyBlManager(RMIGroupManager.getInstance(), EndPointManager.getInstance(), DefaultNVSMXContext.getContext().getPolicyRepository()));


    }

    /**This method is used to reload all the policies across all the instances
     * @return
     */
    @GET
    @Path("/policy")
    @Produces({MediaType.APPLICATION_XML})
    public ReloadPolicyResponse reloadPolicy(){
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"Reloading Policies");
        }
        return reloadPolicyBlManager.reloadALLPolicy();
    }

    /**This method is used to reload policies for ids
     * @param ids
     * @return
     */
    @POST
    @Path("/policy")
    @Produces({MediaType.APPLICATION_XML})
    public ReloadPolicyResponse reloadPolicies(String ids){

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading packages with Ids: " + ids);
        }
        if (Strings.isNullOrBlank(ids)) {
            return new ReloadPolicyResponse(ResultCode.INPUT_PARAMETER_MISSING.code,"Reason: package ids not found",null,null,null,null);
        }

        try {
            ids = URLDecoder.decode(ids, CommonConstants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            String responseMessage = " Unable to decode arguments: "+ids+ " . Reason:"+ e.getMessage();
            LogManager.getLogger().error(MODULE, responseMessage);
            LogManager.getLogger().trace(MODULE, e);
            return new ReloadPolicyResponse(ResultCode.INTERNAL_ERROR.code,responseMessage,null,null,null,null);
        }

       return reloadPolicyBlManager.reloadPolicies(ids);

    }


    /** This method is used to reload policies for staffGroupIds across instances<br/>
     * i.e only those policies will be reloaded which belongs to staffGroupsIds passed as a parameter
     * @param staffGroupIds
     * @return
     */
    @POST
    @Path("/policyByGroups")
    @Produces({MediaType.APPLICATION_XML})
    public ReloadPolicyResponse reloadPoliciesByGroups(String staffGroupIds){

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading packages with staff group ids: " + staffGroupIds);
        }
        if (Strings.isNullOrBlank(staffGroupIds)) {
            return new ReloadPolicyResponse(ResultCode.INPUT_PARAMETER_MISSING.code,"Reason: staff groups ids not found",null,null,null,null);
        }

        try {
            staffGroupIds = URLDecoder.decode(staffGroupIds, CommonConstants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            String responseMessage = " Unable to decode arguments: "+ staffGroupIds + " . Reason:"+ e.getMessage();
            LogManager.getLogger().error(MODULE,responseMessage);
            LogManager.getLogger().trace(MODULE, e);
            return new ReloadPolicyResponse(ResultCode.INTERNAL_ERROR.code,responseMessage,null,null,null,null);
        }

        return reloadPolicyBlManager.reloadPoliciesByGroups(staffGroupIds);

    }

    @GET
    @Path("/reloadDataSliceConfiguration")
    @Produces({MediaType.APPLICATION_XML})
    public ReloadDataSliceConfigurationResponse reloadDataSliceConfiguration() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reloading Data Slice Configuration.");
        }

        return reloadPolicyBlManager.reloadDataSliceConfiguration();
    }
}
