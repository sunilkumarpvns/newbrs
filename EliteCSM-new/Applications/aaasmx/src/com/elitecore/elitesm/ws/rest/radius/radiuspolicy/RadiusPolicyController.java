package com.elitecore.elitesm.ws.rest.radius.radiuspolicy;

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

import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class RadiusPolicyController {

	private static final String RADIUS_POLICY = "Radius Policy";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
			@QueryParam(value = "name") String radiusPolicyName) throws DataManagerException {
		
		RadiusPolicyBLManager radiusPolicyBLManager = new RadiusPolicyBLManager();
		RadiusPolicyData radiusPolicyData = (RadiusPolicyData) radiusPolicyBLManager.getRadiusPolicyDataByName(radiusPolicyName, ConfigManager.chekForCaseSensitivity());
		radiusPolicyData.setName(radiusPolicyName);
		return Response.ok(radiusPolicyData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String radiusPolicyName) throws DataManagerException {
			return getByNameFromQuery(radiusPolicyName);
	}

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<RadiusPolicyData> radiusPolicyDatas,@Context UriInfo url) throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RadiusPolicyBLManager radiusPolicyBLManager = new RadiusPolicyBLManager();
		List<RadiusPolicyData> list = radiusPolicyDatas.getList();
		List<IRadiusPolicyData> radiusPolicyList = new  ArrayList<IRadiusPolicyData>();
		
		for (RadiusPolicyData radiusPolicyData : list) {

			radiusPolicyData.setCreatedByStaffId(String.valueOf(staffData.getStaffId()));
			radiusPolicyData.setLastModifiedByStaffId(String.valueOf(staffData.getStaffId()));
			radiusPolicyData.setCreateDate(new Timestamp(System.currentTimeMillis()));
			radiusPolicyData.setLastUpdated(new Timestamp(System.currentTimeMillis()));
			radiusPolicyData.setStatusChangeDate(new Timestamp(System.currentTimeMillis()));
			radiusPolicyData.setSystemGenerated("N");
			radiusPolicyData.setEditable("Y");
			radiusPolicyList.add(radiusPolicyData);
			}
		
		Map<String, List<Status>> responseMap = radiusPolicyBLManager.create(radiusPolicyList, staffData, URLInfo.isPartialSuccess(url), ConfigManager.chekForCaseSensitivity());
		
		return Response.ok(RestUtitlity.getResponse(RADIUS_POLICY, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(url), URLInfo.isStatusRecord(url))).build();
		}

	@POST
	public Response create(@Valid RadiusPolicyData radiusPolicyData) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RadiusPolicyBLManager radiusPolicyBLManager = new RadiusPolicyBLManager();

		radiusPolicyData.setCreatedByStaffId(String.valueOf(staffData.getStaffId()));
		radiusPolicyData.setLastModifiedByStaffId(String.valueOf(staffData.getStaffId()));
		radiusPolicyData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		radiusPolicyData.setLastUpdated(new Timestamp(System.currentTimeMillis()));
		radiusPolicyData.setStatusChangeDate(new Timestamp(System.currentTimeMillis()));
		radiusPolicyData.setSystemGenerated("N");
		radiusPolicyData.setEditable("Y");

		radiusPolicyBLManager.create(radiusPolicyData, staffData, ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse("Radius Policy created successfully")).build();
	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String radiusPolicyName, 
			@Valid RadiusPolicyData radiusPolicyData) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		RadiusPolicyBLManager dataManager = new RadiusPolicyBLManager();

		radiusPolicyData.setLastModifiedByStaffId(String.valueOf(staffData.getStaffId()));
		radiusPolicyData.setLastUpdated(new Timestamp(System.currentTimeMillis()));
		radiusPolicyData.setSystemGenerated("N");
		radiusPolicyData.setEditable("Y");

		dataManager.updateBasicDetailsByName(radiusPolicyData, staffData, radiusPolicyName, ConfigManager.chekForCaseSensitivity());

		return Response.ok(RestUtitlity.getResponse("Radius Policy updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String radiusPolicyName, 
			@Valid RadiusPolicyData radiusPolicyData) throws DataManagerException {
		return updateByQueryParam(radiusPolicyName, radiusPolicyData);
	}

	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
			@QueryParam(value = "name") String radiusPolicyName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		RadiusPolicyBLManager dataManager = new RadiusPolicyBLManager();

		List<String> radiusPolicyNameList = Arrays.asList(radiusPolicyName.split(","));
		dataManager.deleteByName(radiusPolicyNameList, staffData,ConfigManager.chekForCaseSensitivity());
		return Response.ok(RestUtitlity.getResponse("Radius Policy(s) deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String radiusPolicyName) throws DataManagerException {
		return deleteByQueryParam(radiusPolicyName);
	}

	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RADIUS_POLICY);
}
}
