package com.elitecore.elitesm.ws.rest.eapservicepolicy;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.EAPPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyPluginConfig;
import com.elitecore.elitesm.util.constants.PolicyPluginConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class EapServicePolicyController {

	private static final String EAP_SERVICE_POLICIES = "Eap Service Policies ";

	@GET
	public Response getByQueryParameter(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String policyName)
			throws DataManagerException {

		EAPPolicyBLManager policyBLManager = new EAPPolicyBLManager();
		EAPPolicyData policyData = policyBLManager.getEAPPolicyByName(policyName);

		List<EAPPolicyPluginConfig> plugins = policyData.getEapPolicyPluginConfigList();

		for (EAPPolicyPluginConfig plugin : plugins) {
			if ("IN".equalsIgnoreCase(plugin.getPluginType())) {
				policyData.getPrePlugins().add(plugin);
			} else if ("OUT".equalsIgnoreCase(plugin.getPluginType())) {
				policyData.getPostPlugins().add(plugin);
			}
		}
		return Response.ok(policyData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByPathParameter(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam("name") String policyName)
			throws DataManagerException {
		return getByQueryParameter(policyName);
	}

	@DELETE
	public Response deleteByQueryParameter(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String policyName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EAPPolicyBLManager policyBLManager = new EAPPolicyBLManager();
		policyBLManager.deleteByName(policyName.split(","), staffData);
		return Response.ok(RestUtitlity.getResponse("Eap Service Policy/Policies deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParameter(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam("name") String policyName) throws DataManagerException {
		return deleteByQueryParameter(policyName);
	}

	@POST
	@Path("/bulk")
	public Response createEapServicePolicies(@Valid ListWrapper<EAPPolicyData> data, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, List<Status>> responseMap = null;
		if (Collectionz.isNullOrEmpty(data.getList()) == false) {

			for (EAPPolicyData policyData : data.getList()) {

				List<EAPPolicyPluginConfig> prePlugins = policyData.getPrePlugins();
				for (EAPPolicyPluginConfig prePlugin : prePlugins) {
					prePlugin.setPluginType(PolicyPluginConstants.IN_PLUGIN);
					policyData.getEapPolicyPluginConfigList().add(prePlugin);
				}

				List<EAPPolicyPluginConfig> postPlugins = policyData.getPostPlugins();
				for (EAPPolicyPluginConfig postPlugin : postPlugins) {
					postPlugin.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
					policyData.getEapPolicyPluginConfigList().add(postPlugin);
				}
			}
			EAPPolicyBLManager policyBLManager = new EAPPolicyBLManager();
			responseMap = policyBLManager.create(data.getList(), staffData, URLInfo.isPartialSuccess(uri));
		}
		return Response.ok(RestUtitlity.getResponse(EAP_SERVICE_POLICIES, "created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}

	@POST
	public Response createEapServicePolicy(@Valid EAPPolicyData data)
			throws DataManagerException {
		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<EAPPolicyPluginConfig> prePlugins = data.getPrePlugins();

		for (EAPPolicyPluginConfig prePlugin : prePlugins) {
			prePlugin.setPluginType(PolicyPluginConstants.IN_PLUGIN);
			data.getEapPolicyPluginConfigList().add(prePlugin);
		}

		List<EAPPolicyPluginConfig> postPlugins = data.getPostPlugins();

		for (EAPPolicyPluginConfig postPlugin : postPlugins) {
			postPlugin.setPluginType(PolicyPluginConstants.OUT_PLUGIN);
			data.getEapPolicyPluginConfigList().add(postPlugin);
		}

		EAPPolicyBLManager policyBLManager = new EAPPolicyBLManager();
		policyBLManager.create(data, staffData);
		return Response.ok(RestUtitlity.getResponse("Eap Service Policy created successfully")).build();
	}

	@PUT
	public Response updateByQueryParameter(@Valid EAPPolicyData policy, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String policyName)
			throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EAPPolicyBLManager policyBLManager = new EAPPolicyBLManager();
		policyBLManager.updateEAPPolicy(policy, policyName, staffData);
		return Response.ok(RestUtitlity.getResponse("Eap Service Policy updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParameter(@Valid EAPPolicyData policy, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam("name") String policyName)
			throws DataManagerException {
		return updateByQueryParameter(policy, policyName);
	}
	
	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.EAP_SERVICE_POLICY);
	}
}