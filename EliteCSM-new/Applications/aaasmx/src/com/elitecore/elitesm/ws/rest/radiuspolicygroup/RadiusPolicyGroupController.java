package com.elitecore.elitesm.ws.rest.radiuspolicygroup;

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

import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.radius.radiuspolicygroup.RadiusPolicyGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class RadiusPolicyGroupController {

	private static final String RADIUS_POLICY_GROUP = "Radius Policy Group";
	private static final String MODULE = ConfigConstant.RADIUS_POLICY_GROUP;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String radiusPolicyGroupName) throws DataManagerException {

		RadiusPolicyGroupBLManager radiusPolicyGroupBLManager = new RadiusPolicyGroupBLManager();
		RadiusPolicyGroup radiusPolicyGroup = radiusPolicyGroupBLManager.getRadiusPolicyGroupByName(radiusPolicyGroupName);

		return Response.ok(radiusPolicyGroup).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String radiusPolicyGroupName) throws DataManagerException {
		return getByNameFromQuery(radiusPolicyGroupName);
	}
	
	@POST
	public Response create(@Valid RadiusPolicyGroup radiusPolicyGroup) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		RadiusPolicyGroupBLManager radiusPolicyGroupBLManager = new RadiusPolicyGroupBLManager();

		radiusPolicyGroupBLManager.createRadiusPolicyGroup(radiusPolicyGroup, staffData);

		return Response.ok(RestUtitlity.getResponse("Radius Policy Group created successfully")).build();

	}

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<RadiusPolicyGroup> radiusPolicyGroupDatas, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		RadiusPolicyGroupBLManager radiusPolicyGroupBLManager = new RadiusPolicyGroupBLManager();

		List<RadiusPolicyGroup> radiusPolicyGroups = radiusPolicyGroupDatas.getList();

		Map<String, List<Status>> responseMap = radiusPolicyGroupBLManager.createRadiusPolicyGroup(radiusPolicyGroups, staffData, URLInfo.isPartialSuccess(uri));

		return Response.ok(RestUtitlity.getResponse(RADIUS_POLICY_GROUP, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}

	@PUT
	public Response updateByQueryParam(@Valid RadiusPolicyGroup radiusPolicyGroup, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String radiusPolicyGroupName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		RadiusPolicyGroupBLManager radiusPolicyGroupBLManager = new RadiusPolicyGroupBLManager();
		radiusPolicyGroupBLManager.updateRadiusPolicyGroupByName(radiusPolicyGroup, staffData, radiusPolicyGroupName);

		LogManager.getLogger().info(MODULE, radiusPolicyGroup.toString());

		return Response.ok(RestUtitlity.getResponse("Radius Policy Group updated successfully")).build();

	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid RadiusPolicyGroup radiusPolicyGroup, @PathParam(value = "name") String radiusPolicyGroupName) throws DataManagerException {
		return updateByQueryParam(radiusPolicyGroup, radiusPolicyGroupName);
	}

	@DELETE
	public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String radiusPolicyGroupName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		RadiusPolicyGroupBLManager radiusPolicyGroupBLManager = new RadiusPolicyGroupBLManager();

		List<String> radiusPolicyGroupNames = Arrays.asList(radiusPolicyGroupName.split(","));

		radiusPolicyGroupBLManager.deleteRadiusPolicyGroupByName(radiusPolicyGroupNames, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Radius Policy Group(s) deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String radiusPolicyGroupName) throws DataManagerException {
		return deleteByQueryParam(radiusPolicyGroupName);
	}

	@GET
	@Path("/help")
	public Response getRadiusPolicyGroupHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RADIUS_POLICY_GROUP);
	}
	
}
