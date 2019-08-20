package com.elitecore.elitesm.ws.rest.diameter.authorizationpolicies;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
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

import com.elitecore.elitesm.blmanager.diameter.diameterpolicy.DiameterPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class AuthorizationPolicyController {
	
	private static final String AUTHORIZATION_POLICY_S = "Authorization Policy(s) ";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
			@QueryParam(value = "name") String authorizationPolicyName) throws DataManagerException {
		
		DiameterPolicyBLManager diameterPolicyBLManager = new DiameterPolicyBLManager();
		DiameterPolicyData diameterPolicyData = (DiameterPolicyData) diameterPolicyBLManager.getDiameterPolicyDataByName(authorizationPolicyName,ConfigManager.chekForCaseSensitivity());
		diameterPolicyData.setName(authorizationPolicyName);
		return Response.ok(diameterPolicyData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String authorizationPolicyName) throws DataManagerException {
			return getByNameFromQuery(authorizationPolicyName);
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DiameterPolicyData> diameterPolicyDatas, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DiameterPolicyBLManager diameterPolicyBLManager = new DiameterPolicyBLManager();
		List<DiameterPolicyData> list = diameterPolicyDatas.getList();
		List<DiameterPolicyData> diameterPolicyList = new  ArrayList<DiameterPolicyData>();

		for (DiameterPolicyData diameterPolicyData : list) {

			diameterPolicyData.setCreatedByStaffId(staffData.getStaffId());
			diameterPolicyData.setLastModifiedByStaffId(staffData.getStaffId());
			diameterPolicyData.setCreateDate(new Timestamp(System.currentTimeMillis()));
			diameterPolicyData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			diameterPolicyData.setStatusChangeDate(new Timestamp(System.currentTimeMillis()));
			diameterPolicyData.setSystemGenerated("N");
			diameterPolicyData.setEditable("Y");
			diameterPolicyList.add(diameterPolicyData);
		}

		Map<String, List<Status>> responseMap = diameterPolicyBLManager.create(diameterPolicyList, staffData, URLInfo.isPartialSuccess(uri),ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse(AUTHORIZATION_POLICY_S, "created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid DiameterPolicyData diameterPolicyData) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		DiameterPolicyBLManager diameterPolicyBLManager = new DiameterPolicyBLManager();

		diameterPolicyData.setCreatedByStaffId(staffData.getStaffId());
		diameterPolicyData.setLastModifiedByStaffId(staffData.getStaffId());
		diameterPolicyData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		diameterPolicyData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		diameterPolicyData.setStatusChangeDate(new Timestamp(System.currentTimeMillis()));
		diameterPolicyData.setSystemGenerated("N");
		diameterPolicyData.setEditable("Y");

		diameterPolicyBLManager.create(diameterPolicyData, staffData,ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse("Authorization Policy created successfully")).build();
	}
	
	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPolicyName, 
			@Valid DiameterPolicyData diameterPolicyData) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterPolicyBLManager diameterPolicyBLManager = new DiameterPolicyBLManager();

		diameterPolicyData.setLastModifiedByStaffId(staffData.getStaffId());
		diameterPolicyData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
		diameterPolicyData.setSystemGenerated("N");
		diameterPolicyData.setEditable("Y");

		diameterPolicyBLManager.updateByName(diameterPolicyData, staffData, diameterPolicyName, ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse("Authorization Policy updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String authorizationPolicyName, @Valid DiameterPolicyData diameterPolicyData) throws DataManagerException {
		return updateByQueryParam(authorizationPolicyName, diameterPolicyData);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String diameterPolicyName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterPolicyBLManager diameterPolicyBLManager = new DiameterPolicyBLManager();

		List<String> diameterPolicyNameList = Arrays.asList(diameterPolicyName.split(","));
		diameterPolicyBLManager.deleteByName(diameterPolicyNameList, staffData, ConfigManager.chekForCaseSensitivity());
		return Response.ok(RestUtitlity.getResponse("Authorization Policy(s) deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String authorizationPolicyName) throws DataManagerException {
		return deleteByQueryParam(authorizationPolicyName);
	}

	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.AUTHORIZATION_POLICY);
	}
	
}
