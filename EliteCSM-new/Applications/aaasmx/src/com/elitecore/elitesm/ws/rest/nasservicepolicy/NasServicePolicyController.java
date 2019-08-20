package com.elitecore.elitesm.ws.rest.nasservicepolicy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.DiameterNASPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthPluginConfig;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class NasServicePolicyController {

	private static final String NAS_SERVICE_POLICIES = "Nas Service Policies";

	@GET
	public Response getPolicyByQueryParameter(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String policyName) throws DataManagerException {
		DiameterNASPolicyBLManager blManager = new DiameterNASPolicyBLManager();
		NASPolicyInstData data = blManager.getDiameterServicePolicyDataByPolicyByName(policyName);
		
		List<NASPolicyAuthPluginConfig> authPluginList = data.getNasPolicyAuthPluginConfigList();
		for (NASPolicyAuthPluginConfig plugin : authPluginList) {
			if ("IN".equalsIgnoreCase(plugin.getPluginType())) {
				data.getAuthPrePlugins().add(plugin);
			} else if ("OUT".equalsIgnoreCase(plugin.getPluginType())) {
				data.getAuthPostPlugins().add(plugin);
			}
		}
		
		List<NASPolicyAcctPluginConfig> acctPluginList = data.getNasPolicyAcctPluginConfigList();
		for (NASPolicyAcctPluginConfig plugin : acctPluginList) {
			if ("IN".equalsIgnoreCase(plugin.getPluginType())) {
				data.getAcctPrePlugins().add(plugin);
			} else if ("OUT".equalsIgnoreCase(plugin.getPluginType())) {
				data.getAcctPostPlugins().add(plugin);
			}
		}
		return Response.ok(data).build();
	}

	@GET
	@Path("/{name}")
	public Response getPolicyByPathParameter(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam("name") String policyName) throws DataManagerException {
		return getPolicyByQueryParameter(policyName);
	}
	
	@DELETE
	public Response deleteByQueryParameter(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String policyName)
			throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DiameterNASPolicyBLManager blManager = new DiameterNASPolicyBLManager();
		blManager.deleteNASPolicyByName(Arrays.asList(policyName.split(",")), staffData);
		return Response.ok(RestUtitlity.getResponse("Nas Service Policy/Policies deleted successfully.")).build();
	}
	
	@DELETE
	@Path("/{name}")
	public Response deleteByPathParameter(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value = "name") String policyName)
			throws DataManagerException {
		return deleteByQueryParameter(policyName);
	}
	
	@POST
	@Path("/bulk")
	public Response createNasServicePolicies(@Valid ListWrapper<NASPolicyInstData> policies, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterNASPolicyBLManager blManager = new DiameterNASPolicyBLManager();
		Map<String, List<Status>> responseMap = blManager.createPolicies(policies.getList(), staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(NAS_SERVICE_POLICIES, " created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response createNasServicePolicy(@Valid NASPolicyInstData policy) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DiameterNASPolicyBLManager blManager = new DiameterNASPolicyBLManager();
		blManager.createPolicy(policy, staffData);
		return Response.ok(RestUtitlity.getResponse("Nas Service Policy created successfully")).build();
	}
	
	@PUT
	public Response updateByQueryParameter(@Valid NASPolicyInstData policy, @QueryParam("name") String policyName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DiameterNASPolicyBLManager blManager = new DiameterNASPolicyBLManager();
		blManager.update(policy, policyName, staffData);
		return Response.ok(RestUtitlity.getResponse("Nas Service Policy updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParameter(@Valid NASPolicyInstData policy, @PathParam("name") String policyName) throws DataManagerException {
		return updateByQueryParameter(policy, policyName);
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.NAS_SERVICE_POLICY);
	}
}