package com.elitecore.elitesm.ws.rest.gracepolicy;

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

import com.elitecore.elitesm.blmanager.servermgr.gracepolicy.GracePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class GracePolicyController {
	
	private static final String GRACE_POLICY_POLICIES = "Grace Policy/Policies ";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String gracePolicyName) throws DataManagerException {

		GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
		GracepolicyData gracePolicy = gracePolicyBLManager.getGracePolicyByName(gracePolicyName, ConfigManager.chekForCaseSensitivity());

		gracePolicy.setName(gracePolicyName);
		return Response.ok(gracePolicy).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String gracePolicyName) throws DataManagerException {
		
		return getByNameFromQuery(gracePolicyName);
		
	}

	@POST
	public Response createGracePolicy(@Valid GracepolicyData gracePolicy) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
		
		gracePolicyBLManager.createGracePolicy(gracePolicy, staffData, ConfigManager.chekForCaseSensitivity());
		
		return Response.ok(RestUtitlity.getResponse("Grace Policy created successfully")).build();

	}
	
	@POST
	@Path("/bulk")
	public Response createGracePolicys(@Valid ListWrapper<GracepolicyData> gracePolicyDatas, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
		
		List<GracepolicyData> gracePolicies = gracePolicyDatas.getList();
		
		Map<String, List<Status>> responseMap = gracePolicyBLManager.createGracePolicy(gracePolicies, staffData, URLInfo.isPartialSuccess(uri), ConfigManager.chekForCaseSensitivity());
		
		return Response.ok(RestUtitlity.getResponse(GRACE_POLICY_POLICIES,"created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();

	}

	
	@PUT
	public Response updateGracePolicyByQueryParam(@Valid GracepolicyData gracePolicy, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String gracePolicyName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();
		
		gracePolicyBLManager.updateGracePolicyByName(gracePolicy, staffData, gracePolicyName, ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse("Grace Policy updated successfully")).build();

	}
	
	@PUT
	@Path("/{name}")
	public Response updateGracePolicyByPathParam(@Valid GracepolicyData gracePolicy, @PathParam(value = "name") String gracePolicyName) throws DataManagerException {
		
		return updateGracePolicyByQueryParam(gracePolicy, gracePolicyName);
		
	}
	
	@DELETE
	public Response deleteGracePolicyByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String gracePolicyDataNames) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		GracePolicyBLManager gracePolicyBLManager = new GracePolicyBLManager();

		List<String> gracePolicyNames = Arrays.asList(gracePolicyDataNames.split(","));

		gracePolicyBLManager.deleteGracePolicyByName(gracePolicyNames, staffData, ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse("Grace Policy/Policies deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteGracePolicyByPathParam(@PathParam(value = "name") String gracePolicyNames) throws DataManagerException {
		
		return deleteGracePolicyByQueryParam(gracePolicyNames);
		
	}
	
	@GET
	@Path("/help")
	public Response getGracePolicyHelp() throws IOException  {
		return RestUtitlity.getHelp(RestHelpConstant.GRACE_POLICY);
		
	}

}
