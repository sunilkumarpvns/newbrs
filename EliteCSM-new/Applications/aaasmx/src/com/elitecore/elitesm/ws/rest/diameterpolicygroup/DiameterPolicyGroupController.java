package com.elitecore.elitesm.ws.rest.diameterpolicygroup;

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
import com.elitecore.elitesm.blmanager.diameter.diameterpolicygroup.DiameterPolicyGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpolicygroup.data.DiameterPolicyGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class DiameterPolicyGroupController {

	private static final String MODULE = ConfigConstant.DIAMETER_POLICY_GROUP;

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPolicyGroupName) throws DataManagerException {

		DiameterPolicyGroupBLManager diameterPolicyGroupBLManager = new DiameterPolicyGroupBLManager();
		DiameterPolicyGroup diameterPolicyGroup = diameterPolicyGroupBLManager.getDiameterPolicyGroupDataByName(diameterPolicyGroupName);

		return Response.ok(diameterPolicyGroup).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String diameterPolicyGroupName) throws DataManagerException {
		return getByNameFromQuery(diameterPolicyGroupName);
	}

	@POST
	public Response create(@Valid DiameterPolicyGroup diameterPolicyGroup) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPolicyGroupBLManager diameterPolicyGroupBLManager = new DiameterPolicyGroupBLManager();

		diameterPolicyGroupBLManager.createDiameterPolicyGroup(diameterPolicyGroup, staffData);

		return Response.ok(RestUtitlity.getResponse("Diameter Policy Group created successfully")).build();

	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<DiameterPolicyGroup> diameterPolicyGroup, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPolicyGroupBLManager diameterPolicyGroupBLManager = new DiameterPolicyGroupBLManager();

		List<DiameterPolicyGroup> diameterPolicyGroupDatas = diameterPolicyGroup.getList();

		Map<String, List<Status>> responseMap = diameterPolicyGroupBLManager.createDiameterPolicyGroup(diameterPolicyGroupDatas, staffData, URLInfo.isPartialSuccess(uri));

		return Response.ok(RestUtitlity.getResponse("Diameter Policy Group","(s) created successfully", responseMap, URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();

	}

	@PUT
	public Response updateByQueryParam(@Valid DiameterPolicyGroup diameterPolicyGroup, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPolicyGroupName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		DiameterPolicyGroupBLManager diameterPolicyGroupBLManager = new DiameterPolicyGroupBLManager();
		diameterPolicyGroupBLManager.updateDiameterPolicyGroupByName(diameterPolicyGroup, staffData, diameterPolicyGroupName);

		LogManager.getLogger().info(MODULE, diameterPolicyGroup.toString());

		return Response.ok(RestUtitlity.getResponse("Diameter Policy Group updated successfully")).build();

	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@Valid DiameterPolicyGroup diameterPolicyGroup, @PathParam(value = "name") String diameterPeerGroupName) throws DataManagerException {
		
		return updateByQueryParam(diameterPolicyGroup, diameterPeerGroupName);
		
	}

	@DELETE
	public Response deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String diameterPolicyGroupName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DiameterPolicyGroupBLManager diameterPolicyGroupBLManager = new DiameterPolicyGroupBLManager();

		List<String> diameterPolicyGroupNames = Arrays.asList(diameterPolicyGroupName.split(","));
		
		diameterPolicyGroupBLManager.deleteDiameterPolicyGroupByName(diameterPolicyGroupNames, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Diameter Policy Group(s) deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteByPathParam(@PathParam(value = "name") String diameterPolicyGroupName) throws DataManagerException {
		return deleteByQueryParam(diameterPolicyGroupName);
	}
	
	@GET
	@Path("/help")
	public Response getDiameterPolicyGroupHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_POLICY_GROUP);
	}
	
}
